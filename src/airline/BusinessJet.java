package airline;

public class BusinessJet extends Plane {
    public BusinessJet(String model, int capacity, double cargoCapacity, int range, double fuelConsumption,
                       double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    @Override
    public String getType() {
        return "Business Jet";
    }
}
