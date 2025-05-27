package ui;

import airline.*;
import java.util.*;
import java.util.stream.Collectors;

public class AirlineCLI {
    private final Airline airline = new Airline();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        seedPlanes();
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
                2. Видалити літак
                3. Редагувати літак
                4. Вивести всі літаки
                5. Пошук літака
                6. Сортування літаків
                7. Вихід
                """);
        System.out.print("Ваш вибір: ");
    }

    private void seedPlanes() {
        airline.addPlane(new PassengerPlane("Boeing 737", 160, 18.0, 3000, 2500));
        airline.addPlane(new CargoPlane("Antonov An-124", 150.0, 4800, 12000));
        airline.addPlane(new BusinessJet("Gulfstream G650", 14, 3.5, 12000, 1200));
        airline.addPlane(new LightPlane("Cessna 172", 4, 0.5, 1200, 400));
        airline.addPlane(new Fighter("F-22 Raptor", 2.0, 3000, 5000));
        airline.addPlane(new Bomber("B-2 Spirit", 20.0, 11000, 8000));
        airline.addPlane(new AttackAircraft("Su-25", 2.5, 1850, 2500));
        airline.addPlane(new Interceptor("MiG-25", 3.0, 1450, 7500));
    }

    private void addPlane() {
        try {
            System.out.print("Введіть тип літака (1 - Пасажирський, 2 - Вантажний): ");
            String type = scanner.nextLine();

            System.out.print("Модель: ");
            String model = scanner.nextLine();
            int capacity = 0;

            if (type.equals("1")) {
                System.out.print("Кількість пасажирів: ");
                capacity = Integer.parseInt(scanner.nextLine());
            }

            System.out.print("Вантажопідйомність (тонн): ");
            double cargoCapacity = Double.parseDouble(scanner.nextLine());

            System.out.print("Дальність польоту (км): ");
            int range = Integer.parseInt(scanner.nextLine());

            System.out.print("Споживання пального (л/год): ");
            double fuel = Double.parseDouble(scanner.nextLine());

            Plane plane = type.equals("1")
                    ? new PassengerPlane(model, capacity, cargoCapacity, range, fuel)
                    : new CargoPlane(model, cargoCapacity, range, fuel);

            airline.addPlane(plane);
            System.out.println("✅ Літак додано.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Некоректне числове значення. Спробуйте ще раз.");
        }
    }

    private void removePlane() {
        System.out.print("Введіть модель літака для видалення: ");
        String model = scanner.nextLine();
        boolean success = airline.removePlane(model);
        if (success) System.out.println("✅ Видалено.");
        else System.out.println("❌ Літак не знайдено.");
    }

    private void editPlane() {
        try {
            System.out.print("Введіть модель літака для редагування: ");
            String model = scanner.nextLine();
            Plane plane = airline.findPlaneByModel(model);
            if (plane == null) {
                System.out.println("❌ Літак не знайдено.");
                return;
            }

            System.out.print("Нова вантажопідйомність: ");
            double cargo = Double.parseDouble(scanner.nextLine());

            System.out.print("Нова дальність польоту: ");
            int range = Integer.parseInt(scanner.nextLine());

            System.out.print("Нове споживання пального: ");
            double fuel = Double.parseDouble(scanner.nextLine());

            if (plane instanceof PassengerPlane) {
                System.out.print("Нова місткість пасажирів: ");
                int cap = Integer.parseInt(scanner.nextLine());
                airline.removePlane(model);
                airline.addPlane(new PassengerPlane(model, cap, cargo, range, fuel));
            } else {
                airline.removePlane(model);
                airline.addPlane(new CargoPlane(model, cargo, range, fuel));
            }

            System.out.println("✅ Оновлено.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Некоректне числове значення. Спробуйте ще раз.");
        }
    }

    private void listPlanes() {
        System.out.println("=== Список літаків ===");
        for (Plane p : airline.getPlanes()) {
            System.out.println(p);
        }
    }

    private void searchPlane() {
        System.out.println("""
            1. Пошук за частиною назви
            2. Пошук з фільтрами
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
            found.forEach(System.out::println);
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

            String[] availableTypes = {
                    "Passenger", "Cargo", "Business Jet", "Light Plane",
                    "Fighter", "Bomber", "Attack Aircraft", "Interceptor"
            };

            System.out.println("Виберіть типи літаків (через пробіл, або залиште порожнім для всіх):");
            for (int i = 0; i < availableTypes.length; i++) {
                System.out.printf("%d. %s%n", i + 1, availableTypes[i]);
            }
            System.out.print("Ваш вибір (наприклад: 1 3 6): ");
            String typesInput = scanner.nextLine().trim();

            final Set<String> selectedTypes = new HashSet<>();

            if (!typesInput.isEmpty()) {
                String[] tokens = typesInput.split("\\s+");
                for (String token : tokens) {
                    try {
                        int index = Integer.parseInt(token);
                        if (index >= 1 && index <= availableTypes.length) {
                            selectedTypes.add(availableTypes[index - 1].toLowerCase());
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
                    .filter(p -> selectedTypes.isEmpty() || selectedTypes.contains(p.getType().toLowerCase()))
                    .toList();

            if (results.isEmpty()) {
                System.out.println("❌ Нічого не знайдено.");
            } else {
                System.out.println("🔍 Результати пошуку:");
                results.forEach(System.out::println);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Некоректне числове значення. Спробуйте ще раз.");
        }
    }

    private void sortPlanes() {
        System.out.println("""
            Сортувати за (спаданням):
            1. Дальність польоту
            2. Місткість
            3. Вантажопідйомність
            4. Споживання пального
            """);
        System.out.print("Ваш вибір: ");
        String choice = scanner.nextLine();
        List<Plane> sorted = switch (choice) {
            case "1" -> airline.getPlanes().stream()
                    .sorted(Comparator.comparingInt(Plane::getRange).reversed()).toList();
            case "2" -> airline.getPlanes().stream()
                    .sorted(Comparator.comparingInt(Plane::getCapacity).reversed()).toList();
            case "3" -> airline.getPlanes().stream()
                    .sorted(Comparator.comparingDouble(Plane::getCargoCapacity).reversed()).toList();
            case "4" -> airline.getPlanes().stream()
                    .sorted(Comparator.comparingDouble(Plane::getFuelConsumption).reversed()).toList();
            default -> {
                System.out.println("❌ Невідомий вибір.");
                yield List.of();
            }
        };

        System.out.println("=== Результат сортування ===");
        sorted.forEach(System.out::println);
    }
}
