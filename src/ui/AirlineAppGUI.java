package ui;

import airline.*;
import airline.util.PlaneFactory;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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

public class AirlineAppGUI extends Application {

    private final Airline airline = new Airline();
    private final FlowPane planeTiles = new FlowPane(10, 10);
    private final VBox filtersBox = new VBox(10);

    private TextField searchField;
    private Slider minCapSlider, maxCapSlider;
    private Slider minCargoSlider, maxCargoSlider;
    private Slider minRangeSlider, maxRangeSlider;
    private Slider minFuelSlider, maxFuelSlider;
    private final ObservableList<CheckBox> typeCheckboxes = FXCollections.observableArrayList();
    private ComboBox<String> sortParam;
    private ComboBox<String> sortOrder;

    @Override
    public void start(Stage primaryStage) {
        seedPlanes();

        planeTiles.setPadding(new Insets(10));
        filtersBox.setPadding(new Insets(10));
        filtersBox.setPrefWidth(500);
        setupFilterPanel();

        ScrollPane filtersScroll = new ScrollPane(filtersBox);
        filtersScroll.setFitToWidth(true);
        filtersScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        ScrollPane centerScroll = new ScrollPane(planeTiles);
        centerScroll.setFitToWidth(true);

        BorderPane root = new BorderPane();
        root.setCenter(centerScroll);
        root.setRight(filtersScroll);

        Scene scene = new Scene(root, 1300, 600);
        primaryStage.setTitle("Авіакомпанія — GUI");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            planeTiles.setPrefWrapLength(width - filtersBox.getPrefWidth() - 50);
        });

        updatePlaneTiles();
    }

    private void seedPlanes() {
        airline.addPlane(PlaneFactory.createPlane("passenger", "Boeing 737", 160, 18.0, 3000, 2500));
        airline.addPlane(PlaneFactory.createPlane("cargo", "Antonov An-124", 0, 150.0, 4800, 12000));
        airline.addPlane(PlaneFactory.createPlane("business jet", "Gulfstream G650", 14, 3.5, 12000, 1200));
        airline.addPlane(PlaneFactory.createPlane("light plane", "Cessna 172", 4, 0.5, 1200, 400));
        airline.addPlane(PlaneFactory.createPlane("fighter", "F-22 Raptor", 1, 2.0, 3000, 5000));
        airline.addPlane(PlaneFactory.createPlane("bomber", "B-2 Spirit", 2, 20.0, 11000, 8000));
        airline.addPlane(PlaneFactory.createPlane("attack aircraft", "Su-25", 1, 2.5, 1850, 2500));
        airline.addPlane(PlaneFactory.createPlane("interceptor", "MiG-25", 1, 3.0, 1450, 7500));
    }

    private void setupFilterPanel() {
        filtersBox.getChildren().clear();

        // Пошук по назві
        searchField = new TextField();
        searchField.setPromptText("Пошук по назві");
        filtersBox.getChildren().add(searchField);

        // Параметри діапазонів
        int minCap = airline.getPlanes().stream().mapToInt(Plane::getCapacity).min().orElse(0);
        int maxCap = airline.getPlanes().stream().mapToInt(Plane::getCapacity).max().orElse(300);
        double minCargo = airline.getPlanes().stream().mapToDouble(Plane::getCargoCapacity).min().orElse(0);
        double maxCargo = airline.getPlanes().stream().mapToDouble(Plane::getCargoCapacity).max().orElse(200);
        int minRange = airline.getPlanes().stream().mapToInt(Plane::getRange).min().orElse(0);
        int maxRange = airline.getPlanes().stream().mapToInt(Plane::getRange).max().orElse(15000);
        double minFuel = airline.getPlanes().stream().mapToDouble(Plane::getFuelConsumption).min().orElse(0);
        double maxFuel = airline.getPlanes().stream().mapToDouble(Plane::getFuelConsumption).max().orElse(20000);

        // Пасажири
        filtersBox.getChildren().add(new Label("Пасажири:"));
        HBox capBox = new HBox(5,
                new Label("Мін:"),
                minCapSlider = new Slider(minCap, maxCap, minCap),
                new Label(String.valueOf(minCap)),
                new Label("Макс:"),
                maxCapSlider = new Slider(minCap, maxCap, maxCap),
                new Label(String.valueOf(maxCap))
        );
        // оновлення підписів
        Label capMinVal = (Label) capBox.getChildren().get(2);
        Label capMaxVal = (Label) capBox.getChildren().get(5);
        minCapSlider.valueProperty().addListener((obs, o, n) -> capMinVal.setText(String.valueOf(n.intValue())));
        maxCapSlider.valueProperty().addListener((obs, o, n) -> capMaxVal.setText(String.valueOf(n.intValue())));
        filtersBox.getChildren().add(capBox);

        // Вантаж
        filtersBox.getChildren().add(new Label("Вантаж (т):"));
        HBox cargoBox = new HBox(5,
                new Label("Мін:"),
                minCargoSlider = new Slider(minCargo, maxCargo, minCargo),
                new Label(String.format("%.1f", minCargo)),
                new Label("Макс:"),
                maxCargoSlider = new Slider(minCargo, maxCargo, maxCargo),
                new Label(String.format("%.1f", maxCargo))
        );
        Label cargoMinVal = (Label) cargoBox.getChildren().get(2);
        Label cargoMaxVal = (Label) cargoBox.getChildren().get(5);
        minCargoSlider.valueProperty().addListener((obs, o, n) -> cargoMinVal.setText(String.format("%.1f", n.doubleValue())));
        maxCargoSlider.valueProperty().addListener((obs, o, n) -> cargoMaxVal.setText(String.format("%.1f", n.doubleValue())));
        filtersBox.getChildren().add(cargoBox);

        // Дальність
        filtersBox.getChildren().add(new Label("Дальність (км):"));
        HBox rangeBox = new HBox(5,
                new Label("Мін:"),
                minRangeSlider = new Slider(minRange, maxRange, minRange),
                new Label(String.valueOf(minRange)),
                new Label("Макс:"),
                maxRangeSlider = new Slider(minRange, maxRange, maxRange),
                new Label(String.valueOf(maxRange))
        );
        Label rangeMinVal = (Label) rangeBox.getChildren().get(2);
        Label rangeMaxVal = (Label) rangeBox.getChildren().get(5);
        minRangeSlider.valueProperty().addListener((obs, o, n) -> rangeMinVal.setText(String.valueOf(n.intValue())));
        maxRangeSlider.valueProperty().addListener((obs, o, n) -> rangeMaxVal.setText(String.valueOf(n.intValue())));
        filtersBox.getChildren().add(rangeBox);

        // Пальне
        filtersBox.getChildren().add(new Label("Пальне (л/год):"));
        HBox fuelBox = new HBox(5,
                new Label("Мін:"),
                minFuelSlider = new Slider(minFuel, maxFuel, minFuel),
                new Label(String.format("%.1f", minFuel)),
                new Label("Макс:"),
                maxFuelSlider = new Slider(minFuel, maxFuel, maxFuel),
                new Label(String.format("%.1f", maxFuel))
        );
        Label fuelMinVal = (Label) fuelBox.getChildren().get(2);
        Label fuelMaxVal = (Label) fuelBox.getChildren().get(5);
        minFuelSlider.valueProperty().addListener((obs, o, n) -> fuelMinVal.setText(String.format("%.1f", n.doubleValue())));
        maxFuelSlider.valueProperty().addListener((obs, o, n) -> fuelMaxVal.setText(String.format("%.1f", n.doubleValue())));
        filtersBox.getChildren().add(fuelBox);

        // Типи літаків
        filtersBox.getChildren().add(new Label("Типи літаків:"));
        VBox typeBox = new VBox(5);
        for (String type : PlaneFactory.getAvailableTypes()) {
            CheckBox cb = new CheckBox(type);
            typeCheckboxes.add(cb);
            typeBox.getChildren().add(cb);
        }
        filtersBox.getChildren().add(typeBox);

        // Сортування
        filtersBox.getChildren().add(new Label("Сортувати за:"));
        sortParam = new ComboBox<>(FXCollections.observableArrayList(
                "Модель", "Пасажири", "Вантаж", "Дальність", "Пальне"
        ));
        sortParam.getSelectionModel().selectFirst();
        sortOrder = new ComboBox<>(FXCollections.observableArrayList(
                "За зростанням", "За спаданням"
        ));
        sortOrder.getSelectionModel().selectFirst();
        filtersBox.getChildren().addAll(sortParam, sortOrder);

        // Слухачі фільтра і сортування
        Runnable filter = this::updatePlaneTiles;
        searchField.textProperty().addListener((obs, o, n) -> filter.run());
        minCapSlider.valueProperty().addListener((obs, o, n) -> filter.run());
        maxCapSlider.valueProperty().addListener((obs, o, n) -> filter.run());
        minCargoSlider.valueProperty().addListener((obs, o, n) -> filter.run());
        maxCargoSlider.valueProperty().addListener((obs, o, n) -> filter.run());
        minRangeSlider.valueProperty().addListener((obs, o, n) -> filter.run());
        maxRangeSlider.valueProperty().addListener((obs, o, n) -> filter.run());
        minFuelSlider.valueProperty().addListener((obs, o, n) -> filter.run());
        maxFuelSlider.valueProperty().addListener((obs, o, n) -> filter.run());
        sortParam.valueProperty().addListener((obs, o, n) -> filter.run());
        sortOrder.valueProperty().addListener((obs, o, n) -> filter.run());
        typeCheckboxes.forEach(cb -> cb.selectedProperty().addListener((obs, o, n) -> filter.run()));
    }

    private void updatePlaneTiles() {
        planeTiles.getChildren().clear();

        String search = searchField.getText().trim().toLowerCase();
        int minCap = (int) minCapSlider.getValue();
        int maxCap = (int) maxCapSlider.getValue();
        double minCargo = minCargoSlider.getValue();
        double maxCargo = maxCargoSlider.getValue();
        int minRange = (int) minRangeSlider.getValue();
        int maxRange = (int) maxRangeSlider.getValue();
        double minFuel = minFuelSlider.getValue();
        double maxFuel = maxFuelSlider.getValue();
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
                .filter(p -> selectedTypes.isEmpty() || selectedTypes.contains(p.getType().toLowerCase()))
                .sorted((a, b) -> {
                    int order = sortOrder.getValue().equals("За зростанням") ? 1 : -1;
                    return switch (sortParam.getValue()) {
                        case "Модель" -> a.getModel().compareToIgnoreCase(b.getModel()) * order;
                        case "Пасажири" -> Integer.compare(a.getCapacity(), b.getCapacity()) * order;
                        case "Вантаж" -> Double.compare(a.getCargoCapacity(), b.getCargoCapacity()) * order;
                        case "Дальність" -> Integer.compare(a.getRange(), b.getRange()) * order;
                        case "Пальне" -> Double.compare(a.getFuelConsumption(), b.getFuelConsumption()) * order;
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
                    new Label("Пальне: " + plane.getFuelConsumption() + " л/год")
            );

            Button editBtn = new Button("✏ Редагувати");
            Button deleteBtn = new Button("🗑 Видалити");
            editBtn.setOnAction(e -> showEditDialog(plane));
            deleteBtn.setOnAction(e -> {
                airline.removePlane(plane.getModel());
                updatePlaneTiles();
            });

            card.getChildren().add(new HBox(5, editBtn, deleteBtn));
            planeTiles.getChildren().add(card);
        }

        Button addButton = new Button("+ Додати літак");
        addButton.setMinSize(140, 100);
        addButton.setOnAction(e -> showAddDialog());
        planeTiles.getChildren().add(addButton);
    }

    private void showAddDialog() {
        showPlaneDialog(null);
    }

    private void showEditDialog(Plane editable) {
        showPlaneDialog(editable);
    }

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
        grid.add(new Label("Зображення:"), 0, 6);
        grid.add(new HBox(5, imagePathField, browseImageBtn), 1, 6);

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

                    Plane plane = PlaneFactory.createPlane(type.toLowerCase(), model, capacity, cargo, range, fuel);
                    plane.setImagePath(imagePathField.getText().trim());
                    return plane;

                } catch (Exception e) {
                    showError("Помилка введення: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(plane -> {
            if (editable != null) {
                editable.setImagePath(plane.getImagePath());
                editable.setModel(plane.getModel());
                editable.setCargoCapacity(plane.getCargoCapacity());
                editable.setCapacity(plane.getCapacity());
                editable.setRange(plane.getRange());
                editable.setFuelConsumption(plane.getFuelConsumption());
            } else {
                airline.addPlane(plane);
            }
            setupFilterPanel();
            updatePlaneTiles();

        });
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

