package airline;

public class CargoPlane extends Plane {
    public CargoPlane(String model, double cargoCapacity, int range, double fuelConsumption,
                      double cruisingSpeed, double maxSpeed, int serviceCeiling) {
        super(model, 0, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
    }

    @Override
    public String getType() {
        return "Cargo";
    }
}
