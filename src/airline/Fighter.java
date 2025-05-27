package airline;

public class Fighter extends Plane {
    public Fighter(String model, double cargoCapacity, int range, double fuelConsumption) {
        super(model, 1, cargoCapacity, range, fuelConsumption);
    }

    @Override
    public String getType() {
        return "Fighter";
    }
}
