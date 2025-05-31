package airline;

/**
 * Represents a business jet aircraft typically used for private or corporate travel.
 * Offers both passenger seating and cargo capacity, with a focus on comfort and speed.
 * Inherits from the abstract Plane class and identifies its type as "Business Jet".
 */
public class BusinessJet extends Plane {

    /**
     * Constructs a BusinessJet with the given specifications.
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
    public BusinessJet(String model, int capacity, double cargoCapacity, int range, double fuelConsumption,
                       double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    /**
     * Returns the type of this plane.
     *
     * @return "Business Jet"
     */
    @Override
    public String getType() {
        return "Business Jet";
    }
}
