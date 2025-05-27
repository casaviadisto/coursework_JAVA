package airline;

public class PassengerPlane extends Plane {
    public PassengerPlane(String model, int capacity, double cargoCapacity, int range, double fuelConsumption) {
        super(model, capacity, cargoCapacity, range, fuelConsumption);
    }

    @Override
    public String getType() {
        return "Passenger";
    }
}
