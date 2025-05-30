package airline;

/**
 * Represents a passenger plane with seating capacity and cargo space.
 * Inherits all properties from the abstract Plane class and specifies the type as "Passenger".
 */
public class PassengerPlane extends Plane {

    /**
     * Constructs a PassengerPlane with the given parameters.
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
    public PassengerPlane(String model, int capacity, double cargoCapacity, int range, double fuelConsumption,
                          double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    /**
     * Returns the type of this plane.
     *
     * @return "Passenger"
     */
    @Override
    public String getType() {
        return "Passenger";
    }
}
