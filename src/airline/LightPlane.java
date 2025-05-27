package airline;

public class LightPlane extends Plane {
    public LightPlane(String model, int capacity, double cargoCapacity, int range, double fuelConsumption) {
        super(model, capacity, cargoCapacity, range, fuelConsumption);
    }

    @Override
    public String getType() {
        return "Light Plane";
    }
}
