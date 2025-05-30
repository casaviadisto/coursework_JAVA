// Файл: airline/util/PlaneFactory.java
package airline.util;

import airline.*;

import java.util.Collections;
import java.util.List;

public class PlaneFactory {

    private static final List<String> availableTypes = List.of(
            "Passenger", "Cargo", "Business Jet", "Light Plane",
            "Fighter", "Bomber", "Attack Aircraft", "Interceptor"
    );

    public static Plane createPlane(
            String type, String model,
            int capacity, double cargoCapacity,
            int range, double fuelConsumption,
            double cruisingSpeed, double maxSpeed, int serviceCeiling
    ) {
        Plane plane = switch (type.toLowerCase()) {
            case "passenger" -> new PassengerPlane(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "cargo" -> new CargoPlane(model, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "business jet" -> new BusinessJet(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "light plane" -> new LightPlane(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "fighter" -> new Fighter(model, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "bomber" -> new Bomber(model, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "attack aircraft" -> new AttackAircraft(model, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "interceptor" -> new Interceptor(model, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            default -> throw new IllegalArgumentException("Невідомий тип літака: " + type);
        };

        plane.setType(type);
        return plane;
    }

    public static List<String> getAvailableTypes() {
        return Collections.unmodifiableList(availableTypes);
    }
}