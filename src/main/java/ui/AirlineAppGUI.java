package ui;

import airline.Airline;
import airline.Plane;
import airline.util.PlaneFactory;
import db.DatabaseManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * The main GUI application for managing an airline's fleet of planes.
 * <p>
 * This application allows users to view, filter, sort, add, edit, and delete planes.
 * Each plane can have an associated image path stored in the database that may be:
 *   - "/images/A-10.jpg"     (resource inside the JAR)
 *   - "images/A-10.jpg"      (file in local "images" folder next to JAR)
 *   - "src/main/resources/images/A-10.jpg" (legacy developer path)
 * <br>
 * The method {@link #loadImageView(String)} handles normalization:
 * it first attempts to load the image from inside the JAR via classpath;
 * if not found, it tries to read from the local file system relative to the working directory.
 * </p>
 */
public class AirlineAppGUI extends Application {

    private static final Logger logger = LoggerFactory.getLogger(AirlineAppGUI.class);

    private final Airline airline = new Airline(new DatabaseManager());
    private final FlowPane planeTiles = new FlowPane(10, 10);
    private final VBox filtersBox = new VBox(10);

    private final HBox summaryBar = new HBox(20);
    private final Label totalPlanesLabel = new Label();
    private final Label totalCargoLabel = new Label();
    private final Label totalPassengersLabel = new Label();

    private TextField searchField;
    private TextField minCapField, maxCapField;
    private TextField minCargoField, maxCargoField;
    private TextField minRangeField, maxRangeField;
    private TextField minFuelField, maxFuelField;
    private TextField minCruisingSpeedField, maxCruisingSpeedField;
    private TextField minMaxSpeedField, maxMaxSpeedField;
    private TextField minCeilingField, maxCeilingField;
    private final ObservableList<CheckBox> typeCheckboxes = FXCollections.observableArrayList();
    private ComboBox<String> sortParam;
    private ComboBox<String> sortOrder;

    private boolean updatingMinMax = false;

    private int filteredTotalPlanes = 0;
    private int filteredTotalPassengers = 0;
    private double filteredTotalCargo = 0.0;

    /**
     * The main entry point for the JavaFX application.
     * <p>
     * Initializes the application, loads planes from the database, and sets up the UI.
     * </p>
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        setupFilterPanel();
        setupSummaryBar();
        updatePlaneTiles();

        planeTiles.setPadding(new Insets(10));
        filtersBox.setPadding(new Insets(10));
        filtersBox.setPrefWidth(300);

        ScrollPane filtersScroll = new ScrollPane(filtersBox);
        filtersScroll.setFitToWidth(true);
        filtersScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        ScrollPane centerScroll = new ScrollPane(planeTiles);
        centerScroll.setFitToWidth(true);

        BorderPane root = new BorderPane();
        root.setTop(summaryBar);
        root.setCenter(centerScroll);
        root.setRight(filtersScroll);

        Scene scene = new Scene(root, 1300, 650);
        primaryStage.setTitle("Airline Management — GUI");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            planeTiles.setPrefWrapLength(width - filtersBox.getPrefWidth() - 50);
        });
    }

    /**
     * Sets up the summary bar at the top of the window,
     * displaying total counts for planes, passengers, and cargo capacity.
     */
    private void setupSummaryBar() {
        summaryBar.setPadding(new Insets(10));
        summaryBar.setStyle("-fx-border-width: 0 0 1 0;");
        summaryBar.setAlignment(Pos.CENTER);
        summaryBar.setMinHeight(40);

        String labelStyle = "-fx-font-weight: bold; -fx-font-size: 14;";
        totalPlanesLabel.setStyle(labelStyle);
        totalCargoLabel.setStyle(labelStyle);
        totalPassengersLabel.setStyle(labelStyle);

        summaryBar.getChildren().addAll(totalPlanesLabel, totalCargoLabel, totalPassengersLabel);
        updateSummaryBar();
    }

    /**
     * Updates the summary bar with current totals of filtered planes,
     * their passenger capacity, and cargo capacity.
     */
    private void updateSummaryBar() {
        totalPlanesLabel.setText("Planes: " + filteredTotalPlanes);
        totalPassengersLabel.setText("Passengers: " + filteredTotalPassengers);
        totalCargoLabel.setText("Cargo Capacity: " + String.format("%.1f t", filteredTotalCargo));
    }

    /**
     * Recomputes min/max filter field suggestions based on currently selected plane types.
     */
    private void updateMinMaxFields() {
        updatingMinMax = true;

        List<String> selectedTypes = typeCheckboxes.stream()
                .filter(CheckBox::isSelected)
                .map(cb -> cb.getText().toLowerCase())
                .toList();

        List<Plane> filtered = airline.getPlanes().stream()
                .filter(p -> selectedTypes.isEmpty() || selectedTypes.contains(p.getType().toLowerCase()))
                .toList();

        int minCap = filtered.stream().mapToInt(Plane::getCapacity).min().orElse(0);
        int maxCap = filtered.stream().mapToInt(Plane::getCapacity).max().orElse(0);
        double minCargo = filtered.stream().mapToDouble(Plane::getCargoCapacity).min().orElse(0.0);
        double maxCargo = filtered.stream().mapToDouble(Plane::getCargoCapacity).max().orElse(0.0);
        int minRange = filtered.stream().mapToInt(Plane::getRange).min().orElse(0);
        int maxRange = filtered.stream().mapToInt(Plane::getRange).max().orElse(0);
        double minFuel = filtered.stream().mapToDouble(Plane::getFuelConsumption).min().orElse(0.0);
        double maxFuel = filtered.stream().mapToDouble(Plane::getFuelConsumption).max().orElse(0.0);
        double minCruisingSpeed = filtered.stream().mapToDouble(Plane::getCruisingSpeed).min().orElse(0.0);
        double maxCruisingSpeed = filtered.stream().mapToDouble(Plane::getCruisingSpeed).max().orElse(0.0);
        double minMaxSpeed = filtered.stream().mapToDouble(Plane::getMaxSpeed).min().orElse(0.0);
        double maxMaxSpeed = filtered.stream().mapToDouble(Plane::getMaxSpeed).max().orElse(0.0);
        int minCeiling = filtered.stream().mapToInt(Plane::getServiceCeiling).min().orElse(0);
        int maxCeiling = filtered.stream().mapToInt(Plane::getServiceCeiling).max().orElse(0);

        minCapField.setText(String.valueOf(minCap));
        maxCapField.setText(String.valueOf(maxCap));
        minCargoField.setText(String.format("%.1f", minCargo));
        maxCargoField.setText(String.format("%.1f", maxCargo));
        minRangeField.setText(String.valueOf(minRange));
        maxRangeField.setText(String.valueOf(maxRange));
        minFuelField.setText(String.format("%.1f", minFuel));
        maxFuelField.setText(String.format("%.1f", maxFuel));
        minCruisingSpeedField.setText(String.format("%.1f", minCruisingSpeed));
        maxCruisingSpeedField.setText(String.format("%.1f", maxCruisingSpeed));
        minMaxSpeedField.setText(String.format("%.1f", minMaxSpeed));
        maxMaxSpeedField.setText(String.format("%.1f", maxMaxSpeed));
        minCeilingField.setText(String.valueOf(minCeiling));
        maxCeilingField.setText(String.valueOf(maxCeiling));

        updatingMinMax = false;
    }

    /**
     * Creates a TextFormatter filter that allows only valid double numbers.
     *
     * @return a UnaryOperator that filters changes to valid double input
     */
    private UnaryOperator<TextFormatter.Change> createDoubleFilter() {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?\\d*\\.?\\d*")) {
                return change;
            }
            return null;
        };
    }

    /**
     * Creates a TextFormatter filter that allows only valid integer numbers.
     *
     * @return a UnaryOperator that filters changes to valid integer input
     */
    private UnaryOperator<TextFormatter.Change> createIntegerFilter() {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };
    }

    /**
     * Opens the CLI window on top of the GUI.
     *
     * @param owner the owner Stage for the CLI window
     */
    private void openCLIWindow(Stage owner) {
        CLIWindow.show(owner);
    }

    /**
     * Sets up the filter panel with all filter controls: search, numeric range filters,
     * type checkboxes, and sorting options.
     */
    private void setupFilterPanel() {
        filtersBox.getChildren().clear();

        // Search by name
        searchField = new TextField();
        searchField.setPromptText("Search by model");
        searchField.setPrefWidth(200);

        Button cliButton = new Button("CLI");
        cliButton.setOnAction(e -> openCLIWindow((Stage) cliButton.getScene().getWindow()));

        HBox searchRow = new HBox(5, searchField, cliButton);
        filtersBox.getChildren().add(searchRow);

        // Passengers filter
        filtersBox.getChildren().add(new Label("Passengers:"));
        HBox capBox = new HBox(5,
                new Label("Min:"),
                minCapField = new TextField(),
                new Label("Max:"),
                maxCapField = new TextField()
        );
        minCapField.setTextFormatter(new TextFormatter<>(createIntegerFilter()));
        maxCapField.setTextFormatter(new TextFormatter<>(createIntegerFilter()));
        minCapField.setPrefWidth(80);
        maxCapField.setPrefWidth(80);
        filtersBox.getChildren().add(capBox);

        // Cargo filter
        filtersBox.getChildren().add(new Label("Cargo (t):"));
        HBox cargoBox = new HBox(5,
                new Label("Min:"),
                minCargoField = new TextField(),
                new Label("Max:"),
                maxCargoField = new TextField()
        );
        minCargoField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        maxCargoField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        minCargoField.setPrefWidth(80);
        maxCargoField.setPrefWidth(80);
        filtersBox.getChildren().add(cargoBox);

        // Range filter
        filtersBox.getChildren().add(new Label("Range (km):"));
        HBox rangeBox = new HBox(5,
                new Label("Min:"),
                minRangeField = new TextField(),
                new Label("Max:"),
                maxRangeField = new TextField()
        );
        minRangeField.setTextFormatter(new TextFormatter<>(createIntegerFilter()));
        maxRangeField.setTextFormatter(new TextFormatter<>(createIntegerFilter()));
        minRangeField.setPrefWidth(80);
        maxRangeField.setPrefWidth(80);
        filtersBox.getChildren().add(rangeBox);

        // Fuel consumption filter
        filtersBox.getChildren().add(new Label("Fuel (l/hr):"));
        HBox fuelBox = new HBox(5,
                new Label("Min:"),
                minFuelField = new TextField(),
                new Label("Max:"),
                maxFuelField = new TextField()
        );
        minFuelField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        maxFuelField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        minFuelField.setPrefWidth(80);
        maxFuelField.setPrefWidth(80);
        filtersBox.getChildren().add(fuelBox);

        // Cruising speed filter
        filtersBox.getChildren().add(new Label("Cruising Speed (km/h):"));
        HBox cruisingSpeedBox = new HBox(5,
                new Label("Min:"),
                minCruisingSpeedField = new TextField(),
                new Label("Max:"),
                maxCruisingSpeedField = new TextField()
        );
        minCruisingSpeedField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        maxCruisingSpeedField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        minCruisingSpeedField.setPrefWidth(80);
        maxCruisingSpeedField.setPrefWidth(80);
        filtersBox.getChildren().add(cruisingSpeedBox);

        // Max speed filter
        filtersBox.getChildren().add(new Label("Max Speed (km/h):"));
        HBox maxSpeedBox = new HBox(5,
                new Label("Min:"),
                minMaxSpeedField = new TextField(),
                new Label("Max:"),
                maxMaxSpeedField = new TextField()
        );
        minMaxSpeedField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        maxMaxSpeedField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        minMaxSpeedField.setPrefWidth(80);
        maxMaxSpeedField.setPrefWidth(80);
        filtersBox.getChildren().add(maxSpeedBox);

        // Service ceiling filter
        filtersBox.getChildren().add(new Label("Service Ceiling (m):"));
        HBox ceilingBox = new HBox(5,
                new Label("Min:"),
                minCeilingField = new TextField(),
                new Label("Max:"),
                maxCeilingField = new TextField()
        );
        minCeilingField.setTextFormatter(new TextFormatter<>(createIntegerFilter()));
        maxCeilingField.setTextFormatter(new TextFormatter<>(createIntegerFilter()));
        minCeilingField.setPrefWidth(80);
        maxCeilingField.setPrefWidth(80);
        filtersBox.getChildren().add(ceilingBox);

        // Plane types
        filtersBox.getChildren().add(new Label("Plane Types:"));
        VBox typeBox = new VBox(5);
        for (String type : PlaneFactory.getAvailableTypes()) {
            CheckBox cb = new CheckBox(type);
            cb.setSelected(true);
            typeCheckboxes.add(cb);
            typeBox.getChildren().add(cb);
        }
        filtersBox.getChildren().add(typeBox);

        // Sorting
        filtersBox.getChildren().add(new Label("Sort by:"));
        sortParam = new ComboBox<>(FXCollections.observableArrayList(
                "Model", "Passengers", "Cargo", "Range", "Fuel",
                "Cruising Speed", "Max Speed", "Service Ceiling"
        ));
        sortParam.getSelectionModel().selectFirst();
        sortOrder = new ComboBox<>(FXCollections.observableArrayList(
                "Ascending", "Descending"
        ));
        sortOrder.getSelectionModel().selectFirst();
        filtersBox.getChildren().addAll(sortParam, sortOrder);

        // Initialize min-max suggestions
        updateMinMaxFields();

        Runnable filterAction = this::updatePlaneTiles;
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterAction.run());

        // Listeners for numeric fields
        minCapField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        maxCapField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        minCargoField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        maxCargoField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        minRangeField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        maxRangeField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        minFuelField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        maxFuelField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        minCruisingSpeedField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        maxCruisingSpeedField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        minMaxSpeedField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        maxMaxSpeedField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        minCeilingField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });
        maxCeilingField.textProperty().addListener((obs, o, n) -> { if (!updatingMinMax) filterAction.run(); });

        sortParam.valueProperty().addListener((obs, o, n) -> filterAction.run());
        sortOrder.valueProperty().addListener((obs, o, n) -> filterAction.run());

        typeCheckboxes.forEach(cb -> cb.selectedProperty().addListener((obs, o, n) -> {
            updateMinMaxFields();
            filterAction.run();
        }));
    }

    /**
     * Updates the display of plane tiles based on current filters and sorting criteria.
     * <p>
     * Each tile includes a thumbnail loaded via {@link #loadImageView(String)}, which
     * attempts to load from the JAR's "/images/" resource first; if not found there,
     * it will load from the local file system under "images/" next to the JAR.
     * Clicking the thumbnail opens a larger, full-size view.
     * </p>
     */
    private void updatePlaneTiles() {
        planeTiles.getChildren().clear();

        String search = searchField.getText().trim().toLowerCase();

        int minCap = parseField(minCapField, Integer.MIN_VALUE);
        int maxCap = parseField(maxCapField, Integer.MAX_VALUE);
        double minCargo = parseField(minCargoField, -Double.MAX_VALUE);
        double maxCargo = parseField(maxCargoField, Double.MAX_VALUE);
        int minRange = parseField(minRangeField, Integer.MIN_VALUE);
        int maxRange = parseField(maxRangeField, Integer.MAX_VALUE);
        double minFuel = parseField(minFuelField, -Double.MAX_VALUE);
        double maxFuel = parseField(maxFuelField, Double.MAX_VALUE);
        double minCruisingSpeed = parseField(minCruisingSpeedField, -Double.MAX_VALUE);
        double maxCruisingSpeed = parseField(maxCruisingSpeedField, Double.MAX_VALUE);
        double minMaxSpeed = parseField(minMaxSpeedField, -Double.MAX_VALUE);
        double maxMaxSpeed = parseField(maxMaxSpeedField, Double.MAX_VALUE);
        int minCeiling = parseField(minCeilingField, Integer.MIN_VALUE);
        int maxCeiling = parseField(maxCeilingField, Integer.MAX_VALUE);

        List<String> selectedTypes = typeCheckboxes.stream()
                .filter(CheckBox::isSelected)
                .map(cb -> cb.getText().toLowerCase())
                .toList();

        List<Plane> planes = airline.getPlanes().stream()
                .filter(p -> p.getModel().toLowerCase().contains(search))
                .filter(p -> p.getCapacity() >= minCap && p.getCapacity() <= maxCap)
                .filter(p -> p.getCargoCapacity() >= minCargo && p.getCargoCapacity() <= maxCargo)
                .filter(p -> p.getRange() >= minRange && p.getRange() <= maxRange)
                .filter(p -> p.getFuelConsumption() >= minFuel && p.getFuelConsumption() <= maxFuel)
                .filter(p -> p.getCruisingSpeed() >= minCruisingSpeed && p.getCruisingSpeed() <= maxCruisingSpeed)
                .filter(p -> p.getMaxSpeed() >= minMaxSpeed && p.getMaxSpeed() <= maxMaxSpeed)
                .filter(p -> p.getServiceCeiling() >= minCeiling && p.getServiceCeiling() <= maxCeiling)
                .filter(p -> selectedTypes.isEmpty() || selectedTypes.contains(p.getType().toLowerCase()))
                .sorted((a, b) -> {
                    int order = sortOrder.getValue().equals("Ascending") ? 1 : -1;
                    return switch (sortParam.getValue()) {
                        case "Model" -> a.getModel().compareToIgnoreCase(b.getModel()) * order;
                        case "Passengers" -> Integer.compare(a.getCapacity(), b.getCapacity()) * order;
                        case "Cargo" -> Double.compare(a.getCargoCapacity(), b.getCargoCapacity()) * order;
                        case "Range" -> Integer.compare(a.getRange(), b.getRange()) * order;
                        case "Fuel" -> Double.compare(a.getFuelConsumption(), b.getFuelConsumption()) * order;
                        case "Cruising Speed" -> Double.compare(a.getCruisingSpeed(), b.getCruisingSpeed()) * order;
                        case "Max Speed" -> Double.compare(a.getMaxSpeed(), b.getMaxSpeed()) * order;
                        case "Service Ceiling" -> Integer.compare(a.getServiceCeiling(), b.getServiceCeiling()) * order;
                        default -> 0;
                    };
                })
                .toList();

        for (Plane plane : planes) {
            VBox card = new VBox(5);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-border-color: gray; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

            if (plane.getImagePath() != null && !plane.getImagePath().isBlank()) {
                ImageView thumbnail = loadImageView(plane.getImagePath());
                thumbnail.setFitWidth(180);
                thumbnail.setPreserveRatio(true);

                thumbnail.setOnMouseClicked(e -> {
                    Stage imageStage = new Stage();
                    ImageView fullImage = loadImageView(plane.getImagePath());
                    fullImage.setPreserveRatio(true);
                    fullImage.setFitWidth(800);
                    StackPane pane = new StackPane(fullImage);
                    pane.setPadding(new Insets(10));
                    imageStage.setScene(new Scene(pane));
                    imageStage.setTitle(plane.getModel());
                    imageStage.show();
                });

                card.getChildren().add(thumbnail);
            }

            card.getChildren().addAll(
                    new Label(plane.getModel()),
                    new Label("Type: " + plane.getType()),
                    new Label("Passengers: " + plane.getCapacity()),
                    new Label("Cargo: " + plane.getCargoCapacity() + " t"),
                    new Label("Range: " + plane.getRange() + " km"),
                    new Label("Fuel: " + plane.getFuelConsumption() + " l/hr"),
                    new Label("Cruising Speed: " + plane.getCruisingSpeed() + " km/h"),
                    new Label("Max Speed: " + plane.getMaxSpeed() + " km/h"),
                    new Label("Service Ceiling: " + plane.getServiceCeiling() + " m")
            );

            Button editBtn = new Button("✏ Edit");
            Button deleteBtn = new Button("🗑 Delete");
            editBtn.setOnAction(e -> showEditDialog(plane));
            deleteBtn.setOnAction(e -> {
                if (airline.removePlane(plane.getId())) {
                    updateMinMaxFields();
                    updatePlaneTiles();
                } else {
                    showError("Failed to delete plane.");
                }
            });

            card.getChildren().add(new HBox(5, editBtn, deleteBtn));
            planeTiles.getChildren().add(card);
        }

        Button addButton = new Button("+ Add Plane");
        addButton.setMinSize(140, 100);
        addButton.setOnAction(e -> showAddDialog());
        planeTiles.getChildren().add(addButton);

        filteredTotalPlanes = planes.size();
        filteredTotalPassengers = planes.stream().mapToInt(Plane::getCapacity).sum();
        filteredTotalCargo = planes.stream().mapToDouble(Plane::getCargoCapacity).sum();
        updateSummaryBar();
    }

    /**
     * Parses the text from a TextField into Integer or Double.
     * Returns defaultValue if the field is empty or parsing fails.
     *
     * @param field        the TextField containing numeric text
     * @param defaultValue the default value to return if parsing fails or empty
     * @param <T>          either Integer or Double
     * @return parsed value or defaultValue
     */
    @SuppressWarnings("unchecked")
    private <T> T parseField(TextField field, T defaultValue) {
        try {
            if (field.getText().isEmpty()) return defaultValue;
            if (defaultValue instanceof Integer) {
                return (T) Integer.valueOf(field.getText());
            } else if (defaultValue instanceof Double) {
                return (T) Double.valueOf(field.getText());
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + field.getText());
        }
        return defaultValue;
    }

    /**
     * Attempts to load an ImageView from the given imagePath.
     * <p>
     * First normalizes the path to extract the filename after "images/".
     * Then it tries to load from inside the JAR via getResourceAsStream("/images/<filename>").
     * If not found in JAR, it treats "images/<filename>" as a local file path next to the JAR
     * and attempts to load from the file system.
     * If neither approach succeeds, returns an empty ImageView.
     * </p>
     *
     * @param imagePath the potentially varied path (e.g., "/images/A-10.jpg", "images/A-10.jpg",
     *                  or "src/main/resources/images/A-10.jpg")
     * @return ImageView containing the image, or an empty ImageView if loading fails
     */
    private ImageView loadImageView(String imagePath) {
        // Normalize to extract "images/filename.ext"
        String normalized = imagePath;
        if (normalized.contains("images/")) {
            normalized = normalized.substring(normalized.indexOf("images/"));
        }
        // Ensure leading slash for resource lookup
        String resourcePath = normalized.startsWith("/") ? normalized : "/" + normalized;

        // 1) Try loading from JAR resource (inside classpath)
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is != null) {
                Image img = new Image(is);
                return new ImageView(img);
            }
        } catch (Exception ignored) {
            // If anything goes wrong, we'll try file system next
        }

        // 2) If not found in JAR, attempt to load as local file: "images/filename.ext" (no leading slash)
        try {
            File localFile = new File(normalized);
            if (localFile.exists()) {
                Image img = new Image(new FileInputStream(localFile));
                return new ImageView(img);
            }
        } catch (Exception e) {
            System.err.println("Error loading local image: " + e.getMessage());
        }

        // 3) If still not found, return empty ImageView
        System.err.println("Image not found (JAR or FS): " + imagePath);
        return new ImageView();
    }

    /**
     * Shows the dialog for adding a new plane.
     */
    private void showAddDialog() {
        showPlaneDialog(null);
    }

    /**
     * Shows the dialog for editing an existing plane.
     *
     * @param editable the plane to edit
     */
    private void showEditDialog(Plane editable) {
        showPlaneDialog(editable);
    }

    /**
     * Displays a dialog to add or edit a plane. If editable is null, this dialog
     * creates a new plane; otherwise, it updates the existing plane.
     * <p>
     * Users can select an image file from the local file system, which will be copied
     * into a folder named "images" next to the running JAR, and the stored imagePath
     * is set to "/images/<filename>". On subsequent loads, that path will refer to the
     * resource inside JAR if packaged, or to the local file if JAR does not contain it.
     * </p>
     *
     * @param editable the existing plane to edit, or null to create a new plane
     */
    private void showPlaneDialog(Plane editable) {
        Dialog<Plane> dialog = new Dialog<>();
        dialog.setTitle(editable == null ? "Add Plane" : "Edit Plane");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField modelField = new TextField();
        TextField capacityField = new TextField();
        TextField cargoField = new TextField();
        TextField rangeField = new TextField();
        TextField fuelField = new TextField();
        TextField cruisingSpeedField = new TextField();
        TextField maxSpeedField = new TextField();
        TextField serviceCeilingField = new TextField();
        TextField imagePathField = new TextField();
        Button browseImageBtn = new Button("Browse...");
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(PlaneFactory.getAvailableTypes()));
        typeBox.getSelectionModel().selectFirst();

        if (editable != null) {
            modelField.setText(editable.getModel());
            capacityField.setText(String.valueOf(editable.getCapacity()));
            cargoField.setText(String.valueOf(editable.getCargoCapacity()));
            rangeField.setText(String.valueOf(editable.getRange()));
            fuelField.setText(String.valueOf(editable.getFuelConsumption()));
            cruisingSpeedField.setText(String.valueOf(editable.getCruisingSpeed()));
            maxSpeedField.setText(String.valueOf(editable.getMaxSpeed()));
            serviceCeilingField.setText(String.valueOf(editable.getServiceCeiling()));
            imagePathField.setText(editable.getImagePath());
            typeBox.getSelectionModel().select(editable.getType());
        }

        browseImageBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Image");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
            if (selectedFile != null) {
                try {
                    // Ensure an "images" folder exists next to the JAR at runtime
                    File imagesDir = new File("images");
                    if (!imagesDir.exists()) imagesDir.mkdirs();

                    String fileName = selectedFile.getName();
                    File destFile = new File(imagesDir, fileName);
                    if (!destFile.exists()) {
                        Files.copy(selectedFile.toPath(), destFile.toPath());
                    }

                    // Store with leading slash for classpath loading: "/images/<filename>"
                    imagePathField.setText("/images/" + fileName);

                } catch (Exception ex) {
                    showError("Error copying image: " + ex.getMessage());
                }
            }
        });

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("Model:"), 0, 1);
        grid.add(modelField, 1, 1);
        grid.add(new Label("Passengers:"), 0, 2);
        grid.add(capacityField, 1, 2);
        grid.add(new Label("Cargo (t):"), 0, 3);
        grid.add(cargoField, 1, 3);
        grid.add(new Label("Range (km):"), 0, 4);
        grid.add(rangeField, 1, 4);
        grid.add(new Label("Fuel (l/hr):"), 0, 5);
        grid.add(fuelField, 1, 5);
        grid.add(new Label("Cruising Speed (km/h):"), 0, 6);
        grid.add(cruisingSpeedField, 1, 6);
        grid.add(new Label("Max Speed (km/h):"), 0, 7);
        grid.add(maxSpeedField, 1, 7);
        grid.add(new Label("Service Ceiling (m):"), 0, 8);
        grid.add(serviceCeilingField, 1, 8);
        grid.add(new Label("Image Path:"), 0, 9);
        grid.add(new HBox(5, imagePathField, browseImageBtn), 1, 9);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    String type = typeBox.getValue();
                    String model = modelField.getText();
                    int capacity = Integer.parseInt(capacityField.getText());
                    double cargo = Double.parseDouble(cargoField.getText());
                    int range = Integer.parseInt(rangeField.getText());
                    double fuel = Double.parseDouble(fuelField.getText());
                    double cruisingSpeed = Double.parseDouble(cruisingSpeedField.getText());
                    double maxSpeed = Double.parseDouble(maxSpeedField.getText());
                    int serviceCeiling = Integer.parseInt(serviceCeilingField.getText());

                    Plane plane = PlaneFactory.createPlane(
                            type.toLowerCase(),
                            model,
                            capacity,
                            cargo,
                            range,
                            fuel,
                            cruisingSpeed,
                            maxSpeed,
                            serviceCeiling
                    );
                    plane.setImagePath(imagePathField.getText().trim());

                    if (editable != null) {
                        plane.setId(editable.getId());
                    }

                    return plane;
                } catch (Exception e2) {
                    showError("Input error: " + e2.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(plane -> {
            if (editable != null) {
                airline.updatePlane(plane);
            } else {
                airline.addPlane(plane);
            }
            updateMinMaxFields();
            updatePlaneTiles();
        });
    }

    /**
     * Shows an error dialog with the specified message.
     *
     * @param msg the error message to display
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * The main method to launch the application.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        logger.info("==== Launching AirlineAppGUI ====");
        launch(args);
    }
}
