// Файл: airline/Plane.java
package airline;

public abstract class Plane {
    private int id;
    protected String model;
    protected int capacity;
    protected double cargoCapacity;
    protected int range;
    protected double fuelConsumption;
    protected double cruisingSpeed;
    protected double maxSpeed;
    protected int serviceCeiling;
    protected String imagePath;
    protected String type;

    public Plane(String model, int capacity, double cargoCapacity, int range,
                 double fuelConsumption, double cruisingSpeed, double maxSpeed,
                 int serviceCeiling) {
        this.model = model;
        this.capacity = capacity;
        this.cargoCapacity = cargoCapacity;
        this.range = range;
        this.fuelConsumption = fuelConsumption;
        this.cruisingSpeed = cruisingSpeed;
        this.maxSpeed = maxSpeed;
        this.serviceCeiling = serviceCeiling;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public double getCruisingSpeed() {
        return cruisingSpeed;
    }

    public void setCruisingSpeed(double cruisingSpeed) {
        this.cruisingSpeed = cruisingSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getServiceCeiling() {
        return serviceCeiling;
    }

    public void setServiceCeiling(int serviceCeiling) {
        this.serviceCeiling = serviceCeiling;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "[" + getType() + "] " + model +
                " | Пасажири: " + capacity +
                ", Вантаж: " + cargoCapacity + " т" +
                ", Дальність: " + range + " км" +
                ", Пальне: " + fuelConsumption + " л/год" +
                ", Крейс. швидк.: " + cruisingSpeed + " км/год" +
                ", Макс. швидк.: " + maxSpeed + " км/год" +
                ", Стеля: " + serviceCeiling + " м" +
                ", Зображення: " + imagePath;
    }
}