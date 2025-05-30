package airline;

public class Interceptor extends Plane {
    public Interceptor(String model, double cargoCapacity, int range, double fuelConsumption,
                       double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, 0, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    @Override
    public String getType() {
        return "Interceptor";
    }
}
