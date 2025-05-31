package airline;

/**
 * Represents a military attack aircraft designed for close air support and ground attack missions.
 * Attack aircraft typically do not carry passengers, so capacity is set to 0.
 * Inherits from the abstract Plane class and identifies its type as "Attack Aircraft".
 */
public class AttackAircraft extends Plane {

    /**
     * Constructs an AttackAircraft with the given specifications.
     * Passenger capacity is fixed at 0.
     *
     * @param model           the aircraft model name
     * @param cargoCapacity   the weapon or cargo capacity in tons
     * @param range           the flight range in kilometers
     * @param fuelConsumption the fuel consumption in liters per hour
     * @param cruisingSpeed   the cruising speed in km/h
     * @param maxSpeed        the maximum speed in km/h
     * @param serviceCeiling  the service ceiling in meters
     */
    public AttackAircraft(String model, double cargoCapacity, int range, double fuelConsumption,
                          double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, 0, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    /**
     * Returns the type of this plane.
     *
     * @return "Attack Aircraft"
     */
    @Override
    public String getType() {
        return "Attack Aircraft";
    }
}
