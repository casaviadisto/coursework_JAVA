package airline;

/**
 * Represents a military fighter aircraft designed for air-to-air combat and high-speed performance.
 * Fighter planes typically do not carry passengers, so capacity is set to 0.
 * Inherits from the abstract Plane class and identifies its type as "Fighter".
 */
public class Fighter extends Plane {

    /**
     * Constructs a Fighter aircraft with the given specifications.
     * Passenger capacity is set to 0 by default.
     *
     * @param model           the aircraft model name
     * @param cargoCapacity   the cargo capacity in tons
     * @param range           the flight range in kilometers
     * @param fuelConsumption the fuel consumption in liters per hour
     * @param cruisingSpeed   the typical cruising speed in km/h
     * @param maxSpeed        the maximum speed in km/h
     * @param serviceCeiling  the service ceiling in meters
     */
    public Fighter(String model, double cargoCapacity, int range, double fuelConsumption,
                   double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, 0, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    /**
     * Returns the type of this plane.
     *
     * @return "Fighter"
     */
    @Override
    public String getType() {
        return "Fighter";
    }
}
