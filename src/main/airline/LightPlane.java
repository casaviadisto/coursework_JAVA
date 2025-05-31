package airline;

/**
 * Represents a light aircraft typically used for short-range flights or personal use.
 * Inherits all properties from the abstract Plane class and identifies itself as a "Light Plane".
 */
public class LightPlane extends Plane {

    /**
     * Constructs a LightPlane with the given specifications.
     *
     * @param model           the aircraft model name
     * @param capacity        the number of passengers
     * @param cargoCapacity   the cargo capacity in tons
     * @param range           the flight range in kilometers
     * @param fuelConsumption the fuel consumption in liters per hour
     * @param cruisingSpeed   the typical cruising speed in km/h
     * @param maxSpeed        the maximum speed in km/h
     * @param serviceCeiling  the service ceiling in meters
     */
    public LightPlane(String model, int capacity, double cargoCapacity, int range, double fuelConsumption,
                      double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    /**
     * Returns the type of this plane.
     *
     * @return "Light Plane"
     */
    @Override
    public String getType() {
        return "Light Plane";
    }
}
