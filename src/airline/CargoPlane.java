package airline;

public class CargoPlane extends Plane {
    public CargoPlane(String model, double cargoCapacity, int range, double fuelConsumption) {
        super(model, 0, cargoCapacity, range, fuelConsumption);
    }

    @Override
    public String getType() {
        return "Cargo";
    }

}
