package airline;

public class Fighter extends Plane {
    public Fighter(String model, double cargoCapacity, int range, double fuelConsumption,
                   double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, 0, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    @Override
    public String getType() {
        return "Fighter";
    }
}
