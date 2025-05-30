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

public class CLIWindow {

    public static void show(Stage owner) {
        Stage stage = new Stage();
        stage.setTitle("Консольний режим (CLI)");
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setPrefSize(1100, 700);

        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(600);
        logArea.setStyle("-fx-font-family: 'DejaVu Sans Mono', 'Liberation Mono', 'Courier New', monospace; -fx-font-size: 16px;");


        // Прокручування:
        logArea.textProperty().addListener((obs, oldText, newText) -> {
            logArea.setScrollTop(Double.MAX_VALUE);
        });

        ScrollPane scrollPane = new ScrollPane(logArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(600);

        TextField inputField = new TextField();
        inputField.setPromptText("Введіть команду та натисніть Enter...");

        root.getChildren().addAll(scrollPane, inputField);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        // Коректне кодування:
        PipedInputStream pipedIn = new PipedInputStream();
        PipedOutputStream pipedOut = new PipedOutputStream();
        try {
            pipedIn.connect(pipedOut);
        } catch (IOException e) {
            logArea.appendText("❌ Помилка ініціалізації CLI\n");
            return;
        }

        PrintStream printOut = new PrintStream(new OutputStream() {
            private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            @Override
            public void write(int b) {
                buffer.write(b);
                // Якщо кінець рядка — додаємо до TextArea:
                if (b == '\n') {
                    String text = buffer.toString(StandardCharsets.UTF_8);
                    Platform.runLater(() -> logArea.appendText(text));
                    buffer.reset();
                }
            }
        }, true, StandardCharsets.UTF_8);

        // Стартуємо CLI в окремому потоці, щоб не блокувати GUI:
        new Thread(() -> {
            try {
                Scanner scanner = new Scanner(new InputStreamReader(pipedIn, StandardCharsets.UTF_8));
                ui.AirlineCLI cli = new ui.AirlineCLI(scanner, printOut);
                cli.run();
            } catch (Exception e) {
                Platform.runLater(() -> logArea.appendText("❌ Помилка в CLI: " + e.getMessage() + "\n"));
                e.printStackTrace();
            }
        }, "CLI-Thread").start();

        // Ввід
        inputField.setOnAction(e -> {
            String cmd = inputField.getText();
            try {
                pipedOut.write((cmd + "\n").getBytes(StandardCharsets.UTF_8));
                pipedOut.flush();
            } catch (IOException ex) {
                logArea.appendText("❌ Помилка відправки команди\n");
            }
            inputField.clear();
        });
    }
}
