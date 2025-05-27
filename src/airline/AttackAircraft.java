package airline;

public class AttackAircraft extends Plane {
    public AttackAircraft(String model, double cargoCapacity, int range, double fuelConsumption) {
        super(model, 1, cargoCapacity, range, fuelConsumption);
    }

    @Override
    public String getType() {
        return "Attack Aircraft";
    }
}
