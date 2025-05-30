package airline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Airline {
    private List<Plane> planes = new ArrayList<>();

    public void addPlane(Plane plane) {
        planes.add(plane);
    }

    public int getTotalCapacity() {
        return planes.stream().mapToInt(Plane::getCapacity).sum();
    }

    public double getTotalCargoCapacity() {
        return planes.stream().mapToDouble(Plane::getCargoCapacity).sum();
    }

    public List<Plane> getPlanesSortedByRange() {
        return planes.stream()
                .sorted(Comparator.comparingInt(Plane::getRange))
                .collect(Collectors.toList());
    }

    public List<Plane> findPlanesByFuelConsumption(double min, double max) {
        return planes.stream()
                .filter(p -> p.getFuelConsumption() >= min && p.getFuelConsumption() <= max)
                .collect(Collectors.toList());
    }

    public List<Plane> getPlanes() {
        return planes;
    }

    public boolean removePlane(Integer planeId) {
        return planes.removeIf(p -> p.getId() == planeId);
    }

    public Plane findPlaneByModel(String model) {
        return planes.stream()
                .filter(p -> p.getModel().equalsIgnoreCase(model))
                .findFirst()
                .orElse(null);
    }
}
