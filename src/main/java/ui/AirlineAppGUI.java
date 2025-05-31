package ui;

import airline.*;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main GUI application for managing an airline's fleet of planes.
 * This application allows users to view, filter, sort, add, edit, and delete planes.
 * It also provides image viewing capabilities for each plane.
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
     * Initializes the application, loads planes from the database, and sets up the UI.
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

        Scene scene = new Scene(root, 1300, 650); // Increased height to 650
        primaryStage.setTitle("Авіакомпанія — GUI");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            planeTiles.setPrefWrapLength(width - filtersBox.getPrefWidth() - 50);
        });
    }

    /**
     * Sets up the summary bar at the top of the window.
     */
    private void setupSummaryBar() {
        summaryBar.setPadding(new Insets(10));
        summaryBar.setStyle("-fx-border-width: 0 0 1 0;");
        summaryBar.setAlignment(Pos.CENTER);
        summaryBar.setMinHeight(40); // Ensure consistent height

        // Style the labels
        String labelStyle = "-fx-font-weight: bold; -fx-font-size: 14;";
        totalPlanesLabel.setStyle(labelStyle);
        totalCargoLabel.setStyle(labelStyle);
        totalPassengersLabel.setStyle(labelStyle);

        summaryBar.getChildren().addAll(totalPlanesLabel, totalCargoLabel, totalPassengersLabel);
        updateSummaryBar();
    }

    /**
     * Updates the summary bar with current totals.
     */
    private void updateSummaryBar() {
        totalPlanesLabel.setText("Літаків: " + filteredTotalPlanes);
        totalPassengersLabel.setText("Пасажирів: " + filteredTotalPassengers);
        totalCargoLabel.setText("Вантажопідйомність: " + String.format("%.1f т", filteredTotalCargo));
    }

    /**
     * Updates the min and max value fields based on the currently selected plane types.
     */
    private void updateMinMaxFields() {
        updatingMinMax = true;

        // Get selected types
        List<String> selectedTypes = typeCheckboxes.stream()
                .filter(CheckBox::isSelected)
                .map(cb -> cb.getText().toLowerCase())
                .toList();

        // Filter planes by selected types
        List<Plane> filtered = airline.getPlanes().stream()
                .filter(p -> selectedTypes.isEmpty() || selectedTypes.contains(p.getType().toLowerCase()))
                .toList();

        // Calculate min-max values
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

        // Update fields
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

    private UnaryOperator<TextFormatter.Change> createDoubleFilter() {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?\\d*\\.?\\d*")) {
                return change;
            }
            return null;
        };
    }

    private UnaryOperator<TextFormatter.Change> createIntegerFilter() {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };
    }

    private void openCLIWindow(Stage owner) {
        CLIWindow.show(owner);
    }

    /**
     * Sets up the filter panel with all filter controls.
     */
    private void setupFilterPanel() {
        filtersBox.getChildren().clear();

        // Search by name
        searchField = new TextField();
        searchField.setPromptText("Пошук по назві");
        searchField.setPrefWidth(200);

        Button cliButton = new Button("CLI");
        cliButton.setOnAction(e -> openCLIWindow((Stage) cliButton.getScene().getWindow()));

        HBox searchRow = new HBox(5, searchField, cliButton);
        filtersBox.getChildren().add(searchRow);

        // Passengers filter
        filtersBox.getChildren().add(new Label("Пасажири:"));
        HBox capBox = new HBox(5,
                new Label("Мін:"),
                minCapField = new TextField(),
                new Label("Макс:"),
                maxCapField = new TextField()
        );
        minCapField.setTextFormatter(new TextFormatter<>(createIntegerFilter()));
        maxCapField.setTextFormatter(new TextFormatter<>(createIntegerFilter()));
        minCapField.setPrefWidth(80);
        maxCapField.setPrefWidth(80);
        filtersBox.getChildren().add(capBox);

        // Cargo filter
        filtersBox.getChildren().add(new Label("Вантаж (т):"));
        HBox cargoBox = new HBox(5,
                new Label("Мін:"),
                minCargoField = new TextField(),
                new Label("Макс:"),
                maxCargoField = new TextField()
        );
        minCargoField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        maxCargoField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        minCargoField.setPrefWidth(80);
        maxCargoField.setPrefWidth(80);
        filtersBox.getChildren().add(cargoBox);

        // Range filter
        filtersBox.getChildren().add(new Label("Дальність (км):"));
        HBox rangeBox = new HBox(5,
                new Label("Мін:"),
                minRangeField = new TextField(),
                new Label("Макс:"),
                maxRangeField = new TextField()
        );
        minRangeField.setTextFormatter(new TextFormatter<>(createIntegerFilter()));
        maxRangeField.setTextFormatter(new TextFormatter<>(createIntegerFilter()));
        minRangeField.setPrefWidth(80);
        maxRangeField.setPrefWidth(80);
        filtersBox.getChildren().add(rangeBox);

        // Fuel consumption filter
        filtersBox.getChildren().add(new Label("Пальне (л/год):"));
        HBox fuelBox = new HBox(5,
                new Label("Мін:"),
                minFuelField = new TextField(),
                new Label("Макс:"),
                maxFuelField = new TextField()
        );
        minFuelField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        maxFuelField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        minFuelField.setPrefWidth(80);
        maxFuelField.setPrefWidth(80);
        filtersBox.getChildren().add(fuelBox);

        // Cruising speed filter
        filtersBox.getChildren().add(new Label("Крейс. швидкість (км/год):"));
        HBox cruisingSpeedBox = new HBox(5,
                new Label("Мін:"),
                minCruisingSpeedField = new TextField(),
                new Label("Макс:"),
                maxCruisingSpeedField = new TextField()
        );
        minCruisingSpeedField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        maxCruisingSpeedField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        minCruisingSpeedField.setPrefWidth(80);
        maxCruisingSpeedField.setPrefWidth(80);
        filtersBox.getChildren().add(cruisingSpeedBox);

        // Max speed filter
        filtersBox.getChildren().add(new Label("Макс. швидкість (км/год):"));
        HBox maxSpeedBox = new HBox(5,
                new Label("Мін:"),
                minMaxSpeedField = new TextField(),
                new Label("Макс:"),
                maxMaxSpeedField = new TextField()
        );
        minMaxSpeedField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        maxMaxSpeedField.setTextFormatter(new TextFormatter<>(createDoubleFilter()));
        minMaxSpeedField.setPrefWidth(80);
        maxMaxSpeedField.setPrefWidth(80);
        filtersBox.getChildren().add(maxSpeedBox);

        // Service ceiling filter
        filtersBox.getChildren().add(new Label("Стеля (м):"));
        HBox ceilingBox = new HBox(5,
                new Label("Мін:"),
                minCeilingField = new TextField(),
                new Label("Макс:"),
                maxCeilingField = new TextField()
        );
        minCeilingField.setTextFormatter(new TextFormatter<>(createIntegerFilter()));
        maxCeilingField.setTextFormatter(new TextFormatter<>(createIntegerFilter()));
        minCeilingField.setPrefWidth(80);
        maxCeilingField.setPrefWidth(80);
        filtersBox.getChildren().add(ceilingBox);

        // Plane types
        filtersBox.getChildren().add(new Label("Типи літаків:"));
        VBox typeBox = new VBox(5);
        for (String type : PlaneFactory.getAvailableTypes()) {
            CheckBox cb = new CheckBox(type);
            cb.setSelected(true);
            typeCheckboxes.add(cb);
            typeBox.getChildren().add(cb);
        }
        filtersBox.getChildren().add(typeBox);

        // Sorting
        filtersBox.getChildren().add(new Label("Сортувати за:"));
        sortParam = new ComboBox<>(FXCollections.observableArrayList(
                "Модель", "Пасажири", "Вантаж", "Дальність", "Пальне",
                "Крейс. швидкість", "Макс. швидкість", "Стеля"
        ));
        sortParam.getSelectionModel().selectFirst();
        sortOrder = new ComboBox<>(FXCollections.observableArrayList(
                "За зростанням", "За спаданням"
        ));
        sortOrder.getSelectionModel().selectFirst();
        filtersBox.getChildren().addAll(sortParam, sortOrder);

        // Update min-max values
        updateMinMaxFields();

        // Filter and sort listeners
        Runnable filter = this::updatePlaneTiles;
        searchField.textProperty().addListener((obs, o, n) -> filter.run());

        // Numeric fields listeners
        minCapField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        maxCapField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        minCargoField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        maxCargoField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        minRangeField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        maxRangeField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        minFuelField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        maxFuelField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        minCruisingSpeedField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        maxCruisingSpeedField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        minMaxSpeedField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        maxMaxSpeedField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        minCeilingField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });
        maxCeilingField.textProperty().addListener((obs, o, n) -> {
            if (!updatingMinMax) filter.run();
        });

        sortParam.valueProperty().addListener((obs, o, n) -> filter.run());
        sortOrder.valueProperty().addListener((obs, o, n) -> filter.run());

        // Type checkboxes listeners
        typeCheckboxes.forEach(cb -> cb.selectedProperty().addListener((obs, o, n) -> {
            updateMinMaxFields();
            filter.run();
        }));
    }

    /**
     * Updates the display of plane tiles based on current filters and sorting.
     */
    private void updatePlaneTiles() {
        planeTiles.getChildren().clear();

        String search = searchField.getText().trim().toLowerCase();

        // Parse filter values with error handling
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
                    int order = sortOrder.getValue().equals("За зростанням") ? 1 : -1;
                    return switch (sortParam.getValue()) {
                        case "Модель" -> a.getModel().compareToIgnoreCase(b.getModel()) * order;
                        case "Пасажири" -> Integer.compare(a.getCapacity(), b.getCapacity()) * order;
                        case "Вантаж" -> Double.compare(a.getCargoCapacity(), b.getCargoCapacity()) * order;
                        case "Дальність" -> Integer.compare(a.getRange(), b.getRange()) * order;
                        case "Пальне" -> Double.compare(a.getFuelConsumption(), b.getFuelConsumption()) * order;
                        case "Крейс. швидкість" -> Double.compare(a.getCruisingSpeed(), b.getCruisingSpeed()) * order;
                        case "Макс. швидкість" -> Double.compare(a.getMaxSpeed(), b.getMaxSpeed()) * order;
                        case "Стеля" -> Integer.compare(a.getServiceCeiling(), b.getServiceCeiling()) * order;
                        default -> 0;
                    };
                })
                .toList();

        for (Plane plane : planes) {
            VBox card = new VBox(5);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-border-color: gray; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

            if (plane.getImagePath() != null) {
                try {
                    Image image = new Image(new FileInputStream(plane.getImagePath()));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(180);
                    imageView.setPreserveRatio(true);

                    imageView.setOnMouseClicked(e -> {
                        try {
                            Stage imageStage = new Stage();
                            ImageView fullImage = new ImageView(new Image(new FileInputStream(plane.getImagePath())));
                            fullImage.setPreserveRatio(true);
                            fullImage.setFitWidth(800);
                            StackPane pane = new StackPane(fullImage);
                            pane.setPadding(new Insets(10));
                            imageStage.setScene(new Scene(pane));
                            imageStage.setTitle(plane.getModel());
                            imageStage.show();
                        } catch (FileNotFoundException ex) {
                            showError("Не вдалося відкрити зображення.");
                        }
                    });

                    card.getChildren().add(imageView);
                } catch (FileNotFoundException e) {
                    System.out.println("Зображення не знайдено: " + plane.getImagePath());
                }
            }

            card.getChildren().addAll(
                    new Label(plane.getModel()),
                    new Label("Тип: " + plane.getType()),
                    new Label("Пасажири: " + plane.getCapacity()),
                    new Label("Вантаж: " + plane.getCargoCapacity() + " т"),
                    new Label("Дальність: " + plane.getRange() + " км"),
                    new Label("Пальне: " + plane.getFuelConsumption() + " л/год"),
                    new Label("Крейс. швидк.: " + plane.getCruisingSpeed() + " км/год"),
                    new Label("Макс. швидк.: " + plane.getMaxSpeed() + " км/год"),
                    new Label("Стеля: " + plane.getServiceCeiling() + " м")
            );

            Button editBtn = new Button("✏ Редагувати");
            Button deleteBtn = new Button("🗑 Видалити");
            editBtn.setOnAction(e -> showEditDialog(plane));
            deleteBtn.setOnAction(e -> {
                if (airline.removePlane(plane.getId())) {
                    updateMinMaxFields();
                    updatePlaneTiles();
                } else {
                    showError("Не вдалося видалити літак.");
                }
            });

            card.getChildren().add(new HBox(5, editBtn, deleteBtn));
            planeTiles.getChildren().add(card);
        }

        Button addButton = new Button("+ Додати літак");
        addButton.setMinSize(140, 100);
        addButton.setOnAction(e -> showAddDialog());
        planeTiles.getChildren().add(addButton);

        filteredTotalPlanes = planes.size();
        filteredTotalPassengers = planes.stream().mapToInt(Plane::getCapacity).sum();
        filteredTotalCargo = planes.stream().mapToDouble(Plane::getCargoCapacity).sum();
        updateSummaryBar();
    }

    /**
     * Parses a text field value to the specified type.
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
            System.err.println("Невірний формат числа: " + field.getText());
        }
        return defaultValue;
    }

    /**
     * Shows the dialog for adding a new plane.
     */
    private void showAddDialog() {
        showPlaneDialog(null);
    }

    /**
     * Shows the dialog for editing an existing plane.
     */
    private void showEditDialog(Plane editable) {
        showPlaneDialog(editable);
    }

    /**
     * Shows the plane dialog for adding or editing a plane.
     */
    private void showPlaneDialog(Plane editable) {
        Dialog<Plane> dialog = new Dialog<>();
        dialog.setTitle(editable == null ? "Додати літак" : "Редагувати літак");

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
        Button browseImageBtn = new Button("Огляд...");
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
            fileChooser.setTitle("Вибір зображення");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Зображення", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
            if (selectedFile != null) {
                try {
                    File imagesDir = new File("images");
                    if (!imagesDir.exists()) imagesDir.mkdirs();

                    String fileName = selectedFile.getName();
                    File destFile = new File(imagesDir, fileName);
                    if (!destFile.exists()) {
                        Files.copy(selectedFile.toPath(), destFile.toPath());
                    }

                    imagePathField.setText("images/" + fileName);

                } catch (Exception ex) {
                    showError("Помилка при копіюванні зображення: " + ex.getMessage());
                }
            }
        });

        grid.add(new Label("Тип:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("Модель:"), 0, 1);
        grid.add(modelField, 1, 1);
        grid.add(new Label("Пасажирів:"), 0, 2);
        grid.add(capacityField, 1, 2);
        grid.add(new Label("Вантаж (т):"), 0, 3);
        grid.add(cargoField, 1, 3);
        grid.add(new Label("Дальність (км):"), 0, 4);
        grid.add(rangeField, 1, 4);
        grid.add(new Label("Пальне (л/год):"), 0, 5);
        grid.add(fuelField, 1, 5);
        grid.add(new Label("Крейс. швидк. (км/год):"), 0, 6);
        grid.add(cruisingSpeedField, 1, 6);
        grid.add(new Label("Макс. швидк. (км/год):"), 0, 7);
        grid.add(maxSpeedField, 1, 7);
        grid.add(new Label("Стеля (м):"), 0, 8);
        grid.add(serviceCeilingField, 1, 8);
        grid.add(new Label("Зображення:"), 0, 9);
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

                } catch (Exception e) {
                    showError("Помилка введення: " + e.getMessage());
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
     * Shows an error message dialog.
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * The main method to launch the application.
     */
    public static void main(String[] args) {
        logger.info("==== Запуск AirlineAppGUI ====");
        launch(args);
    }
}