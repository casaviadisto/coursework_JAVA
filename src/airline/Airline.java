package airline;

import db.DatabaseManager;
import java.util.List;

public class Airline {
    private final DatabaseManager dbManager;

    public Airline(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<Plane> getPlanes() {
        return dbManager.getAllPlanes();
    }

    public void addPlane(Plane plane) {
        dbManager.addPlane(plane);
    }

    public void updatePlane(Plane plane) {
        dbManager.updatePlane(plane);
    }

    public boolean removePlane(int planeId) {
        return dbManager.deletePlane(planeId);
    }

    public Plane findPlaneByModel(String model) {
        return getPlanes().stream()
                .filter(p -> p.getModel().equalsIgnoreCase(model))
                .findFirst()
                .orElse(null);
    }

    public int getTotalCapacity() {
        return getPlanes().stream().mapToInt(Plane::getCapacity).sum();
    }

    public double getTotalCargoCapacity() {
        return getPlanes().stream().mapToDouble(Plane::getCargoCapacity).sum();
    }

    /**
     * Отримує назву і повертає id
     */
    public Integer getPlaneIdByModel(String model) {
        return getPlanes().stream()
                .filter(p -> p.getModel().equalsIgnoreCase(model))
                .map(Plane::getId)
                .findFirst()
                .orElse(null);
    }
}
