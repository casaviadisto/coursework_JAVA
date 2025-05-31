package airline;

/**
 * Abstract base class representing a generic airplane.
 * Contains common properties such as model, capacity, fuel consumption, and speed.
 * Subclasses should represent specific types of aircraft.
 */
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

    /**
     * Constructs a new Plane with the specified parameters.
     *
     * @param model           the aircraft model name
     * @param capacity        the number of passengers
     * @param cargoCapacity   the maximum cargo capacity in tons
     * @param range           the maximum range in kilometers
     * @param fuelConsumption the fuel consumption in liters per hour
     * @param cruisingSpeed   the typical cruising speed in km/h
     * @param maxSpeed        the maximum speed in km/h
     * @param serviceCeiling  the maximum altitude in meters
     */
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

    /** @return the unique ID of the plane */
    public int getId() {
        return id;
    }

    /** @param id the unique ID to set for the plane */
    public void setId(int id) {
        this.id = id;
    }

    /** @param model the model name to set */
    public void setModel(String model) {
        this.model = model;
    }

    /** @param capacity the passenger capacity to set */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /** @param cargoCapacity the cargo capacity (in tons) to set */
    public void setCargoCapacity(double cargoCapacity) {
        this.cargoCapacity = cargoCapacity;
    }

    /** @param range the flight range (in kilometers) to set */
    public void setRange(int range) {
        this.range = range;
    }

    /** @param fuelConsumption the fuel consumption (liters per hour) to set */
    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    /** @return the cruising speed in km/h */
    public double getCruisingSpeed() {
        return cruisingSpeed;
    }

    /** @param cruisingSpeed the cruising speed to set (km/h) */
    public void setCruisingSpeed(double cruisingSpeed) {
        this.cruisingSpeed = cruisingSpeed;
    }

    /** @return the maximum speed in km/h */
    public double getMaxSpeed() {
        return maxSpeed;
    }

    /** @param maxSpeed the maximum speed to set (km/h) */
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /** @return the service ceiling in meters */
    public int getServiceCeiling() {
        return serviceCeiling;
    }

    /** @param serviceCeiling the service ceiling to set (meters) */
    public void setServiceCeiling(int serviceCeiling) {
        this.serviceCeiling = serviceCeiling;
    }

    /** @return the model name of the plane */
    public String getModel() {
        return model;
    }

    /** @return the passenger capacity of the plane */
    public int getCapacity() {
        return capacity;
    }

    /** @return the cargo capacity of the plane (tons) */
    public double getCargoCapacity() {
        return cargoCapacity;
    }

    /** @return the flight range of the plane (kilometers) */
    public int getRange() {
        return range;
    }

    /** @return the fuel consumption of the plane (liters per hour) */
    public double getFuelConsumption() {
        return fuelConsumption;
    }

    /** @return the image path associated with the plane */
    public String getImagePath() {
        return imagePath;
    }

    /** @param imagePath the file path of the image to set */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /** @return the type of the plane */
    public String getType() {
        return type;
    }

    /** @param type the type of the plane (e.g., cargo, passenger) */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns a formatted string representation of the plane and its properties.
     *
     * @return a string describing the plane
     */
    @Override
    public String toString() {
        return String.format(
                "[%s] %s\n" +
                        "Пасажири: %d\n" +
                        "Вантаж: %.2f т\n" +
                        "Дальність: %d км\n" +
                        "Пальне: %.2f л/год\n" +
                        "Крейс. швидк.: %.2f км/год\n" +
                        "Макс. швидк.: %.2f км/год\n" +
                        "Стеля: %d м\n" +
                        "Зображення: %s",
                getType(), model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling, imagePath
        );
    }

}