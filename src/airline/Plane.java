package airline;

public abstract class Plane {
    protected String model;
    protected int capacity;
    protected double cargoCapacity;
    protected int range;
    protected double fuelConsumption;
    protected String imagePath;

    public void setModel(String model) {
        this.model = model;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setCargoCapacity(double cargoCapacity) {
        this.cargoCapacity = cargoCapacity;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public Plane(String model, int capacity, double cargoCapacity, int range, double fuelConsumption) {
        this.model = model;
        this.capacity = capacity;
        this.cargoCapacity = cargoCapacity;
        this.range = range;
        this.fuelConsumption = fuelConsumption;
    }

    public String getModel() {
        return model;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getCargoCapacity() {
        return cargoCapacity;
    }

    public int getRange() {
        return range;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public abstract String getType();

    @Override
    public String toString() {
        return "[" + getType() + "] " + model +
                " | Passengers: " + capacity +
                ", Cargo: " + cargoCapacity + " tons" +
                ", Range: " + range + " km" +
                ", Fuel: " + fuelConsumption + " l/h" +
                ", Image: " + imagePath;
    }
}
