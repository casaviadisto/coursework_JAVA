package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * CLIWindow is a JavaFX-based GUI component that displays a command-line interface (CLI)
 * inside a window. It enables the user to interact with the AirlineCLI class through
 * text commands and view output in a scrollable, read-only terminal-style display.
 */
public class CLIWindow {

    /**
     * Displays the CLI window as a modal dialog owned by the given stage.
     * Sets up the text input/output components and starts the CLI in a separate thread.
     *
     * @param owner the parent stage to which this modal window belongs
     */
    public static void show(Stage owner) {
        Stage stage = new Stage();
        stage.setTitle("Консольний режим (CLI)");
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setPrefSize(1600, 700);

        // TextArea for displaying CLI output
        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(900);
        logArea.setStyle("-fx-font-family: 'DejaVu Sans Mono', 'Liberation Mono', 'Courier New', monospace; -fx-font-size: 16px;");

        // Automatically scroll to bottom on new text
        logArea.textProperty().addListener((obs, oldText, newText) -> {
            logArea.setScrollTop(Double.MAX_VALUE);
        });

        // Wrap TextArea in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(logArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(900);

        // TextField for user input
        TextField inputField = new TextField();
        inputField.setPromptText("Введіть команду та натисніть Enter...");

        root.getChildren().addAll(scrollPane, inputField);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        // Create piped input/output streams to communicate with CLI logic
        PipedInputStream pipedIn = new PipedInputStream();
        PipedOutputStream pipedOut = new PipedOutputStream();
        try {
            pipedIn.connect(pipedOut);
        } catch (IOException e) {
            logArea.appendText("Помилка ініціалізації CLI\n");
            return;
        }

        // Redirect CLI output to the GUI TextArea
        PrintStream printOut = new PrintStream(new OutputStream() {
            private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            @Override
            public void write(int b) {
                buffer.write(b);
                if (b == '\n') {
                    String text = buffer.toString(StandardCharsets.UTF_8);
                    Platform.runLater(() -> logArea.appendText(text));
                    buffer.reset();
                }
            }
        }, true, StandardCharsets.UTF_8);

        // Start CLI logic in a separate thread
        new Thread(() -> {
            try {
                Scanner scanner = new Scanner(new InputStreamReader(pipedIn, StandardCharsets.UTF_8));
                ui.AirlineCLI cli = new ui.AirlineCLI(scanner, printOut);
                cli.run();
            } catch (Exception e) {
                Platform.runLater(() -> logArea.appendText("Помилка в CLI: " + e.getMessage() + "\n"));
                e.printStackTrace();
            }
        }, "CLI-Thread").start();

        // Send user input to the CLI when Enter is pressed
        inputField.setOnAction(e -> {
            String cmd = inputField.getText();
            try {
                pipedOut.write((cmd + "\n").getBytes(StandardCharsets.UTF_8));
                pipedOut.flush();
            } catch (IOException ex) {
                logArea.appendText("Помилка відправки команди\n");
            }
            inputField.clear();
        });
    }
}
