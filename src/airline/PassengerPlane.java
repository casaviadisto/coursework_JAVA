package airline;

public class PassengerPlane extends Plane {
    public PassengerPlane(String model, int capacity, double cargoCapacity, int range, double fuelConsumption,
                          double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    @Override
    public String getType() {
        return "Passenger";
    }
}
