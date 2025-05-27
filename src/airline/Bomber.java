package airline;

public class Bomber extends Plane {
    public Bomber(String model, double cargoCapacity, int range, double fuelConsumption) {
        super(model, 2, cargoCapacity, range, fuelConsumption);
    }

    @Override
    public String getType() {
        return "Bomber";
    }
}
