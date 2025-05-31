package airline;

/**
 * Represents a military interceptor aircraft designed for high-speed, high-altitude missions.
 * Unlike passenger planes, interceptors typically have no passenger capacity.
 * Inherits from the abstract Plane class and defines its type as "Interceptor".
 */
public class Interceptor extends Plane {

    /**
     * Constructs an Interceptor aircraft with the given parameters.
     * Passenger capacity is always set to 0.
     *
     * @param model           the aircraft model name
     * @param cargoCapacity   the cargo capacity in tons
     * @param range           the flight range in kilometers
     * @param fuelConsumption the fuel consumption in liters per hour
     * @param cruisingSpeed   the typical cruising speed in km/h
     * @param maxSpeed        the maximum speed in km/h
     * @param serviceCeiling  the service ceiling in meters
     */
    public Interceptor(String model, double cargoCapacity, int range, double fuelConsumption,
                       double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, 0, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    /**
     * Returns the type of this plane.
     *
     * @return "Interceptor"
     */
    @Override
    public String getType() {
        return "Interceptor";
    }
}
