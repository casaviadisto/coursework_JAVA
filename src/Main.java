//import airline.*;
//
//public class Main {
//    public static void main(String[] args) {
//        Airline airline = new Airline();
//
//        airline.addPlane(new PassengerPlane("Boeing 737", 160, 18.0, 3000, 2500));
//        airline.addPlane(new PassengerPlane("Airbus A320", 180, 20.0, 3200, 2400));
//        airline.addPlane(new CargoPlane("Antonov An-124", 150.0, 4800, 12000));
//        airline.addPlane(new CargoPlane("C-130 Hercules", 20.0, 2500, 3000));
//
//        System.out.println("=== Усі літаки ===");
//        for (Plane p : airline.getPlanes()) {
//            System.out.println(p);
//        }
//
//        System.out.println("\nЗагальна місткість пасажирів: " + airline.getTotalCapacity());
//        System.out.println("Загальна вантажопідйомність: " + airline.getTotalCargoCapacity() + " тонн");
//
//        System.out.println("\n=== Сортування за дальністю польоту ===");
//        for (Plane p : airline.getPlanesSortedByRange()) {
//            System.out.println(p);
//        }
//
//        System.out.println("\n=== Пошук літаків з витратою пального від 2000 до 5000 л/год ===");
//        for (Plane p : airline.findPlanesByFuelConsumption(2000, 5000)) {
//            System.out.println(p);
//        }
//    }
//}

//import ui.AirlineCLI;
import ui.AirlineAppGUI;

public class Main {
    public static void main(String[] args) {
//        new AirlineCLI().run();
        new AirlineAppGUI();
    }
}
