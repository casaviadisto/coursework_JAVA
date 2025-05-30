package airline;

import db.DatabaseManager;
import java.util.List;

/**
 * Represents an airline that manages a fleet of planes.
 * Provides high-level methods for interacting with the database layer through {@link DatabaseManager}.
 */
public class Airline {
    private final DatabaseManager dbManager;

    /**
     * Constructs an Airline instance with the provided DatabaseManager.
     *
     * @param dbManager the database manager responsible for data persistence
     */
    public Airline(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Retrieves all planes in the airline.
     *
     * @return a list of all planes
     */
    public List<Plane> getPlanes() {
        return dbManager.getAllPlanes();
    }

    /**
     * Adds a new plane to the airline.
     *
     * @param plane the plane to add
     */
    public void addPlane(Plane plane) {
        dbManager.addPlane(plane);
    }

    /**
     * Updates the details of an existing plane.
     *
     * @param plane the updated plane object
     */
    public void updatePlane(Plane plane) {
        dbManager.updatePlane(plane);
    }

    /**
     * Removes a plane by its ID.
     *
     * @param planeId the ID of the plane to remove
     * @return true if the plane was removed successfully, false otherwise
     */
    public boolean removePlane(int planeId) {
        return dbManager.deletePlane(planeId);
    }

    /**
     * Finds a plane by its model name.
     *
     * @param model the model name to search for
     * @return the matching plane, or null if not found
     */
    public Plane findPlaneByModel(String model) {
        return getPlanes().stream()
                .filter(p -> p.getModel().equalsIgnoreCase(model))
                .findFirst()
                .orElse(null);
    }

    /**
     * Calculates the total passenger capacity of all planes.
     *
     * @return the total number of passengers that can be transported
     */
    public int getTotalCapacity() {
        return getPlanes().stream().mapToInt(Plane::getCapacity).sum();
    }

    /**
     * Calculates the total cargo capacity of all planes.
     *
     * @return the total cargo capacity in tons
     */
    public double getTotalCargoCapacity() {
        return getPlanes().stream().mapToDouble(Plane::getCargoCapacity).sum();
    }

    /**
     * Finds the ID of a plane by its model name.
     *
     * @param model the model name of the plane
     * @return the plane's ID, or null if not found
     */
    public Integer getPlaneIdByModel(String model) {
        return getPlanes().stream()
                .filter(p -> p.getModel().equalsIgnoreCase(model))
                .map(Plane::getId)
                .findFirst()
                .orElse(null);
    }
}
