package airline;

public class BusinessJet extends Plane {
    public BusinessJet(String model, int capacity, double cargoCapacity, int range, double fuelConsumption) {
        super(model, capacity, cargoCapacity, range, fuelConsumption);
    }

    @Override
    public String getType() {
        return "Business Jet";
    }
}
