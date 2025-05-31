package airline.util;

import airline.*;

import java.util.Collections;
import java.util.List;

/**
 * Factory class for creating {@link Plane} instances of different types.
 * Provides methods to instantiate planes based on a type string and retrieve the list of supported plane types.
 */
public class PlaneFactory {

    /**
     * List of available plane types supported by the factory.
     */
    private static final List<String> availableTypes = List.of(
            "Passenger", "Cargo", "Business Jet", "Light Plane",
            "Fighter", "Bomber", "Attack Aircraft", "Interceptor"
    );


    /**
     * Creates a new {@link Plane} object of the specified type with the provided parameters.
     *
     * @param type            the type of plane (e.g., "Passenger", "Cargo", etc.)
     * @param model           the model name of the plane
     * @param capacity        the number of passengers (if applicable)
     * @param cargoCapacity   the cargo capacity in tons
     * @param range           the flight range in kilometers
     * @param fuelConsumption the fuel consumption in liters per hour
     * @param cruisingSpeed   the typical cruising speed in km/h
     * @param maxSpeed        the maximum speed in km/h
     * @param serviceCeiling  the service ceiling in meters
     * @return a new instance of {@link Plane} corresponding to the specified type
     * @throws IllegalArgumentException if the type is unknown
     */
    public static Plane createPlane(
            String type, String model,
            int capacity, double cargoCapacity,
            int range, double fuelConsumption,
            double cruisingSpeed, double maxSpeed, int serviceCeiling
    ) {
        Plane plane = switch (type.toLowerCase()) {
            case "passenger" ->
                    new PassengerPlane(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "cargo" ->
                    new CargoPlane(model, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "business jet" ->
                    new BusinessJet(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "light plane" ->
                    new LightPlane(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "fighter" ->
                    new Fighter(model, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "bomber" ->
                    new Bomber(model, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "attack aircraft" ->
                    new AttackAircraft(model, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            case "interceptor" ->
                    new Interceptor(model, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
            default -> throw new IllegalArgumentException("Невідомий тип літака: " + type);
        };

        plane.setType(type);
        return plane;
    }


    /**
     * Returns an unmodifiable list of available plane types.
     *
     * @return list of supported plane type names
     */
    public static List<String> getAvailableTypes() {
        return Collections.unmodifiableList(availableTypes);
    }
}