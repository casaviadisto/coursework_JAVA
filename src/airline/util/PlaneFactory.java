package airline.util;

import airline.*;

import java.util.ArrayList;
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
            int range, double fuelConsumption
    ) {
        Plane plane = switch (type.toLowerCase()) {
            case "passenger" -> new PassengerPlane(model, capacity, cargoCapacity, range, fuelConsumption);
            case "cargo" -> new CargoPlane(model, cargoCapacity, range, fuelConsumption);
            case "business jet" -> new BusinessJet(model, capacity, cargoCapacity, range, fuelConsumption);
            case "light plane" -> new LightPlane(model, capacity, cargoCapacity, range, fuelConsumption);
            case "fighter" -> new Fighter(model, cargoCapacity, range, fuelConsumption);
            case "bomber" -> new Bomber(model, cargoCapacity, range, fuelConsumption);
            case "attack aircraft" -> new AttackAircraft(model, cargoCapacity, range, fuelConsumption);
            case "interceptor" -> new Interceptor(model, cargoCapacity, range, fuelConsumption);
            default -> throw new IllegalArgumentException("Невідомий тип літака: " + type);
        };

        // Задаємо шлях до зображення (відносний)
        String formatted = model.replace(" ", "_").replace("-", "_");
        plane.setImagePath("images/" + formatted + ".jpg");

        return plane;
    }

    public static List<String> getAvailableTypes() {
        return Collections.unmodifiableList(new ArrayList<>(availableTypes));
    }
}
