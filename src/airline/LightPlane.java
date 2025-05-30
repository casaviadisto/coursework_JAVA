package airline;

public class LightPlane extends Plane {
    public LightPlane(String model, int capacity, double cargoCapacity, int range, double fuelConsumption,
                      double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    @Override
    public String getType() {
        return "Light Plane";
    }
}
