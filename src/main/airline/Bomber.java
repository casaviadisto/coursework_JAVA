package airline;

/**
 * Represents a military bomber aircraft designed for delivering payloads over long distances.
 * Bombers typically have no passenger capacity, so it is set to 0.
 * Inherits from the abstract Plane class and identifies its type as "Bomber".
 */
public class Bomber extends Plane {

    /**
     * Constructs a Bomber aircraft with the specified parameters.
     * Passenger capacity is fixed at 0.
     *
     * @param model           the aircraft model name
     * @param cargoCapacity   the bomb or cargo capacity in tons
     * @param range           the operational range in kilometers
     * @param fuelConsumption the fuel consumption in liters per hour
     * @param cruisingSpeed   the cruising speed in km/h
     * @param maxSpeed        the maximum speed in km/h
     * @param serviceCeiling  the operational ceiling in meters
     */
    public Bomber(String model, double cargoCapacity, int range, double fuelConsumption,
                  double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, 0, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    /**
     * Returns the type of this plane.
     *
     * @return "Bomber"
     */
    @Override
    public String getType() {
        return "Bomber";
    }
}
