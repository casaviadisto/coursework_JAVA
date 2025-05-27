package airline;

public class Interceptor extends Plane {
    public Interceptor(String model, double cargoCapacity, int range, double fuelConsumption) {
        super(model, 1, cargoCapacity, range, fuelConsumption);
    }

    @Override
    public String getType() {
        return "Interceptor";
    }
}
