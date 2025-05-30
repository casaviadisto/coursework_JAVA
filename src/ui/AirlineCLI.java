package ui;

import airline.*;
import airline.util.PlaneFactory;
import db.DatabaseManager;
import java.util.*;
import java.util.stream.Collectors;

public class AirlineCLI {
    private final Airline airline;
    private final Scanner scanner = new Scanner(System.in);

    public AirlineCLI() {
        this.airline = new Airline(new DatabaseManager());
    }

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
                default -> System.out.println("❌ Невідомий вибір. Спробуйте ще.");
            }
        }
    }

    private void printMenu() {
        System.out.println("""
                \n=== Меню ===
                1. Додати літак
                2. Видалити літак (по назві)
                3. Редагувати літак (по назві)
                4. Вивести всі літаки
                5. Пошук літака
                6. Сортування літаків
                7. Вихід
                """);
        System.out.print("Ваш вибір: ");
    }

    /**
     * Вивід даних у вигляді таблиці
     */
    private void printPlaneTable(List<Plane> planes) {
        if (planes == null || planes.isEmpty()) {
            System.out.println("Список літаків порожній.");
            return;
        }

        String format = "| %-3s | %-28s | %-15s | %-9s | %-9s | %-10s | %-10s | %-13s | %-13s | %-7s |\n";
        String line = "+-----+------------------------------+-----------------+-----------+-----------+------------+------------+---------------+---------------+---------+";

        System.out.println(line);
        System.out.printf(format, "ID", "Модель", "Тип", "Пасажири", "Вантаж(т)", "Дальність", "Пальне", "Крейс. швидк.", "Макс. швидк.", "Стеля");
        System.out.println(line);

        for (Plane p : planes) {
            System.out.printf(format,
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
        System.out.println(line);
    }

    private void addPlane() {
        try {
            List<String> availableTypes = PlaneFactory.getAvailableTypes();
            System.out.println("Доступні типи літаків:");
            for (int i = 0; i < availableTypes.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, availableTypes.get(i));
            }
            System.out.print("Виберіть тип літака (введіть номер): ");
            int typeIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (typeIndex < 0 || typeIndex >= availableTypes.size()) {
                System.out.println("❌ Невірний вибір типу.");
                return;
            }
            String type = availableTypes.get(typeIndex);

            System.out.print("Модель: ");
            String model = scanner.nextLine();

            System.out.print("Кількість пасажирів: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            System.out.print("Вантажопідйомність (тонн): ");
            double cargoCapacity = Double.parseDouble(scanner.nextLine());

            System.out.print("Дальність польоту (км): ");
            int range = Integer.parseInt(scanner.nextLine());

            System.out.print("Споживання пального (л/год): ");
            double fuel = Double.parseDouble(scanner.nextLine());

            System.out.print("Крейсерська швидкість (км/год): ");
            double cruisingSpeed = Double.parseDouble(scanner.nextLine());

            System.out.print("Максимальна швидкість (км/год): ");
            double maxSpeed = Double.parseDouble(scanner.nextLine());

            System.out.print("Стеля польоту (м): ");
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
            System.out.println("✅ Літак додано.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Некоректне числове значення. Спробуйте ще раз.");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Помилка при створенні літака: " + e.getMessage());
        }
    }

    private void removePlane() {
        System.out.print("Введіть назву (модель) літака для видалення: ");
        String model = scanner.nextLine().trim();
        Integer id = airline.getPlaneIdByModel(model);
        if (id == null) {
            System.out.println("❌ Літак не знайдено.");
            return;
        }
        Plane found = airline.getPlanes().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        System.out.println("Знайдено:");
        printPlaneTable(Collections.singletonList(found));
        System.out.print("Підтвердити видалення (y/n)? ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            boolean ok = airline.removePlane(id);
            if (ok) System.out.println("✅ Видалено.");
            else System.out.println("❌ Помилка видалення.");
        } else {
            System.out.println("Скасовано.");
        }
    }

    private void editPlane() {
        try {
            System.out.print("Введіть модель літака для редагування: ");
            String model = scanner.nextLine();
            Integer id = airline.getPlaneIdByModel(model);

            if (id == null) {
                System.out.println("❌ Літак не знайдено.");
                return;
            }
            Plane plane = airline.getPlanes().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
            System.out.println("Знайдено:");
            printPlaneTable(Collections.singletonList(plane));

            System.out.println("Введіть нові значення (залишіть порожнім, щоб залишити поточне):");

            System.out.print("Модель [" + plane.getModel() + "]: ");
            String newModel = scanner.nextLine();
            if (!newModel.isEmpty()) {
                plane.setModel(newModel);
            }

            System.out.print("Кількість пасажирів [" + plane.getCapacity() + "]: ");
            String capInput = scanner.nextLine();
            if (!capInput.isEmpty()) {
                plane.setCapacity(Integer.parseInt(capInput));
            }

            System.out.print("Вантажопідйомність (тонн) [" + plane.getCargoCapacity() + "]: ");
            String cargoInput = scanner.nextLine();
            if (!cargoInput.isEmpty()) {
                plane.setCargoCapacity(Double.parseDouble(cargoInput));
            }

            System.out.print("Дальність польоту (км) [" + plane.getRange() + "]: ");
            String rangeInput = scanner.nextLine();
            if (!rangeInput.isEmpty()) {
                plane.setRange(Integer.parseInt(rangeInput));
            }

            System.out.print("Споживання пального (л/год) [" + plane.getFuelConsumption() + "]: ");
            String fuelInput = scanner.nextLine();
            if (!fuelInput.isEmpty()) {
                plane.setFuelConsumption(Double.parseDouble(fuelInput));
            }

            System.out.print("Крейсерська швидкість (км/год) [" + plane.getCruisingSpeed() + "]: ");
            String cruisingInput = scanner.nextLine();
            if (!cruisingInput.isEmpty()) {
                plane.setCruisingSpeed(Double.parseDouble(cruisingInput));
            }

            System.out.print("Максимальна швидкість (км/год) [" + plane.getMaxSpeed() + "]: ");
            String maxSpeedInput = scanner.nextLine();
            if (!maxSpeedInput.isEmpty()) {
                plane.setMaxSpeed(Double.parseDouble(maxSpeedInput));
            }

            System.out.print("Стеля польоту (м) [" + plane.getServiceCeiling() + "]: ");
            String ceilingInput = scanner.nextLine();
            if (!ceilingInput.isEmpty()) {
                plane.setServiceCeiling(Integer.parseInt(ceilingInput));
            }

            airline.updatePlane(plane);
            System.out.println("✅ Оновлено.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Некоректне числове значення. Спробуйте ще раз.");
        }
    }

    private void listPlanes() {
        List<Plane> planes = airline.getPlanes();
        System.out.println("=== Список літаків ===");
        printPlaneTable(planes);
    }

    private void searchPlane() {
        System.out.println("""
            1. Пошук за частиною назви
            2. Розширений пошук з фільтрами
            """);
        System.out.print("Виберіть тип пошуку: ");
        String choice = scanner.nextLine();
        if (choice.equals("1")) {
            simpleSearch();
        } else if (choice.equals("2")) {
            advancedSearch();
        } else {
            System.out.println("❌ Невідомий вибір.");
        }
    }

    private void simpleSearch() {
        System.out.print("Введіть частину назви для пошуку: ");
        String keyword = scanner.nextLine().toLowerCase();

        List<Plane> found = airline.getPlanes().stream()
                .filter(p -> p.getModel().toLowerCase().contains(keyword))
                .toList();

        if (found.isEmpty()) {
            System.out.println("❌ Не знайдено жодного літака.");
        } else {
            System.out.println("🔍 Знайдені літаки:");
            printPlaneTable(found);
        }
    }

    private void advancedSearch() {
        try {
            System.out.println("=== Розширений пошук з фільтрами ===");

            System.out.print("Частина моделі (або залиште порожнім): ");
            String keyword = scanner.nextLine().trim().toLowerCase();

            System.out.print("Мін. пасажирів (або -1): ");
            int minCap = Integer.parseInt(scanner.nextLine());

            System.out.print("Макс. пасажирів (або -1): ");
            int maxCap = Integer.parseInt(scanner.nextLine());

            System.out.print("Мін. вантажопідйомність (або -1): ");
            double minCargo = Double.parseDouble(scanner.nextLine());

            System.out.print("Макс. вантажопідйомність (або -1): ");
            double maxCargo = Double.parseDouble(scanner.nextLine());

            System.out.print("Мін. дальність (або -1): ");
            int minRange = Integer.parseInt(scanner.nextLine());

            System.out.print("Макс. дальність (або -1): ");
            int maxRange = Integer.parseInt(scanner.nextLine());

            System.out.print("Мін. споживання пального (або -1): ");
            double minFuel = Double.parseDouble(scanner.nextLine());

            System.out.print("Макс. споживання пального (або -1): ");
            double maxFuel = Double.parseDouble(scanner.nextLine());

            System.out.print("Мін. крейсерська швидкість (або -1): ");
            double minCruisingSpeed = Double.parseDouble(scanner.nextLine());

            System.out.print("Макс. крейсерська швидкість (або -1): ");
            double maxCruisingSpeed = Double.parseDouble(scanner.nextLine());

            System.out.print("Мін. максимальна швидкість (або -1): ");
            double minMaxSpeed = Double.parseDouble(scanner.nextLine());

            System.out.print("Макс. максимальна швидкість (або -1): ");
            double maxMaxSpeed = Double.parseDouble(scanner.nextLine());

            System.out.print("Мін. стеля (або -1): ");
            int minCeiling = Integer.parseInt(scanner.nextLine());

            System.out.print("Макс. стеля (або -1): ");
            int maxCeiling = Integer.parseInt(scanner.nextLine());

            List<String> availableTypes = PlaneFactory.getAvailableTypes();
            System.out.println("Виберіть типи літаків (через пробіл, або залиште порожнім для всіх):");
            for (int i = 0; i < availableTypes.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, availableTypes.get(i));
            }
            System.out.print("Ваш вибір (наприклад: 1 3 6): ");
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
                            System.out.printf("⚠️ Індекс поза межами: %s (ігнорується)%n", token);
                        }
                    } catch (NumberFormatException e) {
                        System.out.printf("⚠️ Некоректне значення: %s (ігнорується)%n", token);
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
                System.out.println("❌ Нічого не знайдено.");
            } else {
                System.out.println("🔍 Результати пошуку (" + results.size() + "):");
                printPlaneTable(results);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Некоректне числове значення. Спробуйте ще раз.");
        }
    }

    private void sortPlanes() {
        System.out.println("""
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
        System.out.print("Ваш вибір: ");
        String choice = scanner.nextLine();

        System.out.println("Порядок сортування:");
        System.out.println("1. За зростанням");
        System.out.println("2. За спаданням");
        System.out.print("Ваш вибір: ");
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
                System.out.println("❌ Невідомий вибір.");
                yield null;
            }
        };

        if (comparator == null) return;

        if (!ascending) {
            comparator = comparator.reversed();
        }

        sorted.sort(comparator);

        System.out.println("=== Результат сортування ===");
        printPlaneTable(sorted);
    }
}
