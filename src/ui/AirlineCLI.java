package ui;

import airline.*;
import airline.util.PlaneFactory;
import db.DatabaseManager;

import java.io.PrintStream;
import java.util.*;

/**
 * Command-line interface for managing an airline's fleet of planes.
 * Provides functionality for adding, editing, removing, listing, sorting, and searching planes.
 */
public class AirlineCLI {
    private final Airline airline;
    private final Scanner scanner;
    private final PrintStream printOut;

    /**
     * Constructs the CLI with input and output streams.
     *
     * @param scanner  the input scanner for reading user input
     * @param printOut the output stream for writing output
     */
    public AirlineCLI(Scanner scanner, PrintStream printOut) {
        this.airline = new Airline(new DatabaseManager());
        this.scanner = scanner;
        this.printOut = printOut;
    }

    /**
     * Runs the main CLI loop, handling user input and executing commands.
     */
    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String input = scanner.nextLine();
            switch (input) {
                case "1" -> addPlane();
                case "2" -> removePlane();
                case "3" -> editPlane();
                case "4" -> listPlanes();
                case "5" -> searchPlane();
                case "6" -> sortPlanes();
                case "7" -> running = false;
                default -> printOut.println("❌ Невідомий вибір. Спробуйте ще.");
            }
        }
    }

    /**
     * Prints the main menu with available options.
     */
    private void printMenu() {
        printOut.println("""
                \n=== Меню ===
                1. Додати літак
                2. Видалити літак
                3. Редагувати літак
                4. Вивести всі літаки
                5. Пошук літака
                6. Сортування літаків
                7. Вихід
                """);
        printOut.print("Ваш вибір: ");
    }

    /**
     * Displays a formatted table of planes.
     *
     * @param planes the list of planes to display
     */
    private void printPlaneTable(List<Plane> planes) {
        if (planes == null || planes.isEmpty()) {
            printOut.println("Список літаків порожній.");
            return;
        }

        String format = "| %-3s | %-28s | %-15s | %-9s | %-9s | %-10s | %-10s | %-13s | %-13s | %-7s |\n";
        String line = "+-----+------------------------------+-----------------+-----------+-----------+------------+------------+---------------+---------------+---------+";

        printOut.println(line);
        printOut.printf(format, "ID", "Модель", "Тип", "Пасажири", "Вантаж(т)", "Дальність", "Пальне", "Крейс. швидк.", "Макс. швидк.", "Стеля");
        printOut.println(line);

        for (Plane p : planes) {
            printOut.printf(format,
                    p.getId(),
                    p.getModel(),
                    p.getType(),
                    p.getCapacity(),
                    String.format("%.1f", p.getCargoCapacity()),
                    p.getRange(),
                    String.format("%.1f", p.getFuelConsumption()),
                    String.format("%.1f", p.getCruisingSpeed()),
                    String.format("%.1f", p.getMaxSpeed()),
                    p.getServiceCeiling()
            );
        }
        printOut.println(line);
    }

    /**
     * Adds a new plane to the airline based on user input.
     */
    private void addPlane() {
        try {
            List<String> availableTypes = PlaneFactory.getAvailableTypes();
            printOut.println("Доступні типи літаків:");
            for (int i = 0; i < availableTypes.size(); i++) {
                printOut.printf("%d. %s%n", i + 1, availableTypes.get(i));
            }
            printOut.print("Виберіть тип літака (введіть номер): ");
            int typeIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (typeIndex < 0 || typeIndex >= availableTypes.size()) {
                printOut.println("❌ Невірний вибір типу.");
                return;
            }
            String type = availableTypes.get(typeIndex);

            printOut.print("Модель: ");
            String model = scanner.nextLine();

            printOut.print("Кількість пасажирів: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            printOut.print("Вантажопідйомність (тонн): ");
            double cargoCapacity = Double.parseDouble(scanner.nextLine());

            printOut.print("Дальність польоту (км): ");
            int range = Integer.parseInt(scanner.nextLine());

            printOut.print("Споживання пального (л/год): ");
            double fuel = Double.parseDouble(scanner.nextLine());

            printOut.print("Крейсерська швидкість (км/год): ");
            double cruisingSpeed = Double.parseDouble(scanner.nextLine());

            printOut.print("Максимальна швидкість (км/год): ");
            double maxSpeed = Double.parseDouble(scanner.nextLine());

            printOut.print("Стеля польоту (м): ");
            int serviceCeiling = Integer.parseInt(scanner.nextLine());

            Plane plane = PlaneFactory.createPlane(
                    type.toLowerCase(),
                    model,
                    capacity,
                    cargoCapacity,
                    range,
                    fuel,
                    cruisingSpeed,
                    maxSpeed,
                    serviceCeiling
            );

            airline.addPlane(plane);
            printOut.println("Літак додано.");
        } catch (NumberFormatException e) {
            printOut.println("Некоректне числове значення. Спробуйте ще раз.");
        } catch (IllegalArgumentException e) {
            printOut.println("Помилка при створенні літака: " + e.getMessage());
        }
    }

    /**
     * Removes a plane from the airline by model name.
     */
    private void removePlane() {
        printOut.print("Введіть назву (модель) літака для видалення: ");
        String model = scanner.nextLine().trim();
        Integer id = airline.getPlaneIdByModel(model);
        if (id == null) {
            printOut.println("❌ Літак не знайдено.");
            return;
        }
        Plane found = airline.getPlanes().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        printOut.println("Знайдено:");
        printPlaneTable(Collections.singletonList(found));
        printOut.print("Підтвердити видалення (y/n)? ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            boolean ok = airline.removePlane(id);
            if (ok) printOut.println("✅ Видалено.");
            else printOut.println("❌ Помилка видалення.");
        } else {
            printOut.println("Скасовано.");
        }
    }

    /**
     * Edits the attributes of an existing plane.
     * Users can leave fields empty to keep current values.
     */
    private void editPlane() {
        try {
            printOut.print("Введіть модель літака для редагування: ");
            String model = scanner.nextLine();
            Integer id = airline.getPlaneIdByModel(model);

            if (id == null) {
                printOut.println("❌ Літак не знайдено.");
                return;
            }
            Plane plane = airline.getPlanes().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
            printOut.println("Знайдено:");
            printPlaneTable(Collections.singletonList(plane));

            printOut.println("Введіть нові значення (залишіть порожнім, щоб залишити поточне):");

            printOut.print("Модель [" + plane.getModel() + "]: ");
            String newModel = scanner.nextLine();
            if (!newModel.isEmpty()) {
                plane.setModel(newModel);
            }

            printOut.print("Кількість пасажирів [" + plane.getCapacity() + "]: ");
            String capInput = scanner.nextLine();
            if (!capInput.isEmpty()) {
                plane.setCapacity(Integer.parseInt(capInput));
            }

            printOut.print("Вантажопідйомність (тонн) [" + plane.getCargoCapacity() + "]: ");
            String cargoInput = scanner.nextLine();
            if (!cargoInput.isEmpty()) {
                plane.setCargoCapacity(Double.parseDouble(cargoInput));
            }

            printOut.print("Дальність польоту (км) [" + plane.getRange() + "]: ");
            String rangeInput = scanner.nextLine();
            if (!rangeInput.isEmpty()) {
                plane.setRange(Integer.parseInt(rangeInput));
            }

            printOut.print("Споживання пального (л/год) [" + plane.getFuelConsumption() + "]: ");
            String fuelInput = scanner.nextLine();
            if (!fuelInput.isEmpty()) {
                plane.setFuelConsumption(Double.parseDouble(fuelInput));
            }

            printOut.print("Крейсерська швидкість (км/год) [" + plane.getCruisingSpeed() + "]: ");
            String cruisingInput = scanner.nextLine();
            if (!cruisingInput.isEmpty()) {
                plane.setCruisingSpeed(Double.parseDouble(cruisingInput));
            }

            printOut.print("Максимальна швидкість (км/год) [" + plane.getMaxSpeed() + "]: ");
            String maxSpeedInput = scanner.nextLine();
            if (!maxSpeedInput.isEmpty()) {
                plane.setMaxSpeed(Double.parseDouble(maxSpeedInput));
            }

            printOut.print("Стеля польоту (м) [" + plane.getServiceCeiling() + "]: ");
            String ceilingInput = scanner.nextLine();
            if (!ceilingInput.isEmpty()) {
                plane.setServiceCeiling(Integer.parseInt(ceilingInput));
            }

            airline.updatePlane(plane);
            printOut.println("✅ Оновлено.");
        } catch (NumberFormatException e) {
            printOut.println("❌ Некоректне числове значення. Спробуйте ще раз.");
        }
    }

    /**
     * Lists all planes in the airline.
     */
    private void listPlanes() {
        List<Plane> planes = airline.getPlanes();
        printOut.println("=== Список літаків ===");
        printPlaneTable(planes);
    }

    /**
     * Prompts the user to choose a search method: simple or advanced.
     */
    private void searchPlane() {
        printOut.println("""
                1. Пошук за частиною назви
                2. Розширений пошук з фільтрами
                """);
        printOut.print("Виберіть тип пошуку: ");
        String choice = scanner.nextLine();
        if (choice.equals("1")) {
            simpleSearch();
        } else if (choice.equals("2")) {
            advancedSearch();
        } else {
            printOut.println("❌ Невідомий вибір.");
        }
    }

    /**
     * Performs a simple search of planes by part of the model name.
     */
    private void simpleSearch() {
        printOut.print("Введіть частину назви для пошуку: ");
        String keyword = scanner.nextLine().toLowerCase();

        List<Plane> found = airline.getPlanes().stream()
                .filter(p -> p.getModel().toLowerCase().contains(keyword))
                .toList();

        if (found.isEmpty()) {
            printOut.println("❌ Не знайдено жодного літака.");
        } else {
            printOut.println("🔍 Знайдені літаки:");
            printPlaneTable(found);
        }
    }


    /**
     * Performs an advanced search of planes using multiple filters and criteria.
     */
    private void advancedSearch() {
        try {
            printOut.println("=== Розширений пошук з фільтрами ===");

            printOut.print("Частина моделі (або залиште порожнім): ");
            String keyword = scanner.nextLine().trim().toLowerCase();

            printOut.print("Мін. пасажирів (або -1): ");
            int minCap = Integer.parseInt(scanner.nextLine());

            printOut.print("Макс. пасажирів (або -1): ");
            int maxCap = Integer.parseInt(scanner.nextLine());

            printOut.print("Мін. вантажопідйомність (або -1): ");
            double minCargo = Double.parseDouble(scanner.nextLine());

            printOut.print("Макс. вантажопідйомність (або -1): ");
            double maxCargo = Double.parseDouble(scanner.nextLine());

            printOut.print("Мін. дальність (або -1): ");
            int minRange = Integer.parseInt(scanner.nextLine());

            printOut.print("Макс. дальність (або -1): ");
            int maxRange = Integer.parseInt(scanner.nextLine());

            printOut.print("Мін. споживання пального (або -1): ");
            double minFuel = Double.parseDouble(scanner.nextLine());

            printOut.print("Макс. споживання пального (або -1): ");
            double maxFuel = Double.parseDouble(scanner.nextLine());

            printOut.print("Мін. крейсерська швидкість (або -1): ");
            double minCruisingSpeed = Double.parseDouble(scanner.nextLine());

            printOut.print("Макс. крейсерська швидкість (або -1): ");
            double maxCruisingSpeed = Double.parseDouble(scanner.nextLine());

            printOut.print("Мін. максимальна швидкість (або -1): ");
            double minMaxSpeed = Double.parseDouble(scanner.nextLine());

            printOut.print("Макс. максимальна швидкість (або -1): ");
            double maxMaxSpeed = Double.parseDouble(scanner.nextLine());

            printOut.print("Мін. стеля (або -1): ");
            int minCeiling = Integer.parseInt(scanner.nextLine());

            printOut.print("Макс. стеля (або -1): ");
            int maxCeiling = Integer.parseInt(scanner.nextLine());

            List<String> availableTypes = PlaneFactory.getAvailableTypes();
            printOut.println("Виберіть типи літаків (через пробіл, або залиште порожнім для всіх):");
            for (int i = 0; i < availableTypes.size(); i++) {
                printOut.printf("%d. %s%n", i + 1, availableTypes.get(i));
            }
            printOut.print("Ваш вибір (наприклад: 1 3 6): ");
            String typesInput = scanner.nextLine().trim();

            final Set<String> selectedTypes = new HashSet<>();

            if (!typesInput.isEmpty()) {
                String[] tokens = typesInput.split("\\s+");
                for (String token : tokens) {
                    try {
                        int index = Integer.parseInt(token);
                        if (index >= 1 && index <= availableTypes.size()) {
                            selectedTypes.add(availableTypes.get(index - 1).toLowerCase());
                        } else {
                            printOut.printf("Індекс поза межами: %s (ігнорується)%n", token);
                        }
                    } catch (NumberFormatException e) {
                        printOut.printf("Некоректне значення: %s (ігнорується)%n", token);
                    }
                }
            }

            List<Plane> results = airline.getPlanes().stream()
                    .filter(p -> keyword.isEmpty() || p.getModel().toLowerCase().contains(keyword))
                    .filter(p -> minCap < 0 || p.getCapacity() >= minCap)
                    .filter(p -> maxCap < 0 || p.getCapacity() <= maxCap)
                    .filter(p -> minCargo < 0 || p.getCargoCapacity() >= minCargo)
                    .filter(p -> maxCargo < 0 || p.getCargoCapacity() <= maxCargo)
                    .filter(p -> minRange < 0 || p.getRange() >= minRange)
                    .filter(p -> maxRange < 0 || p.getRange() <= maxRange)
                    .filter(p -> minFuel < 0 || p.getFuelConsumption() >= minFuel)
                    .filter(p -> maxFuel < 0 || p.getFuelConsumption() <= maxFuel)
                    .filter(p -> minCruisingSpeed < 0 || p.getCruisingSpeed() >= minCruisingSpeed)
                    .filter(p -> maxCruisingSpeed < 0 || p.getCruisingSpeed() <= maxCruisingSpeed)
                    .filter(p -> minMaxSpeed < 0 || p.getMaxSpeed() >= minMaxSpeed)
                    .filter(p -> maxMaxSpeed < 0 || p.getMaxSpeed() <= maxMaxSpeed)
                    .filter(p -> minCeiling < 0 || p.getServiceCeiling() >= minCeiling)
                    .filter(p -> maxCeiling < 0 || p.getServiceCeiling() <= maxCeiling)
                    .filter(p -> selectedTypes.isEmpty() || selectedTypes.contains(p.getType().toLowerCase()))
                    .toList();

            if (results.isEmpty()) {
                printOut.println("❌ Нічого не знайдено.");
            } else {
                printOut.println("🔍 Результати пошуку (" + results.size() + "):");
                printPlaneTable(results);
            }
        } catch (NumberFormatException e) {
            printOut.println("❌ Некоректне числове значення. Спробуйте ще раз.");
        }
    }

    /**
     * Sorts planes based on a chosen attribute and order (ascending or descending).
     */
    private void sortPlanes() {
        printOut.println("""
                Сортувати за:
                1. Модель
                2. Пасажири
                3. Вантажопідйомність
                4. Дальність польоту
                5. Споживання пального
                6. Крейсерська швидкість
                7. Максимальна швидкість
                8. Стеля
                """);
        printOut.print("Ваш вибір: ");
        String choice = scanner.nextLine();

        printOut.println("Порядок сортування:");
        printOut.println("1. За зростанням");
        printOut.println("2. За спаданням");
        printOut.print("Ваш вибір: ");
        String orderChoice = scanner.nextLine();
        boolean ascending = orderChoice.equals("1");

        List<Plane> sorted = new ArrayList<>(airline.getPlanes());
        Comparator<Plane> comparator = switch (choice) {
            case "1" -> Comparator.comparing(Plane::getModel, String.CASE_INSENSITIVE_ORDER);
            case "2" -> Comparator.comparingInt(Plane::getCapacity);
            case "3" -> Comparator.comparingDouble(Plane::getCargoCapacity);
            case "4" -> Comparator.comparingInt(Plane::getRange);
            case "5" -> Comparator.comparingDouble(Plane::getFuelConsumption);
            case "6" -> Comparator.comparingDouble(Plane::getCruisingSpeed);
            case "7" -> Comparator.comparingDouble(Plane::getMaxSpeed);
            case "8" -> Comparator.comparingInt(Plane::getServiceCeiling);
            default -> {
                printOut.println("❌ Невідомий вибір.");
                yield null;
            }
        };

        if (comparator == null) return;

        if (!ascending) {
            comparator = comparator.reversed();
        }

        sorted.sort(comparator);

        printOut.println("=== Результат сортування ===");
        printPlaneTable(sorted);
    }
}
