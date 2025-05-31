package airline;


/**
 * Represents a cargo aircraft designed for transporting goods rather than passengers.
 * Passenger capacity is always set to 0.
 * Inherits from the abstract Plane class and identifies its type as "Cargo".
 */
public class CargoPlane extends Plane {

    /**
     * Constructs a CargoPlane with the given specifications.
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
    public CargoPlane(String model, double cargoCapacity, int range, double fuelConsumption,
                      double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, 0, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    /**
     * Returns the type of this plane.
     *
     * @return "Cargo"
     */
    @Override
    public String getType() {
        return "Cargo";
    }
}
