package airline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the abstract class {@link Plane}.
 * All getters, setters, and methods are individually tested for full coverage.
 */
class PlaneTest {

    /**
     * Simple concrete subclass of Plane for testing purposes.
     */
    static class TestPlane extends Plane {
        public TestPlane(String model, int capacity, double cargoCapacity, int range,
                         double fuelConsumption, double cruisingSpeed, double maxSpeed, int serviceCeiling) {
            super(model, capacity, cargoCapacity, range, fuelConsumption, cruisingSpeed, maxSpeed, serviceCeiling);
        }
    }

    private TestPlane plane;

    /**
     * Sets up a fresh instance before each test.
     */
    @BeforeEach
    void setUp() {
        plane = new TestPlane("TestModel", 100, 10.5, 3000, 2.2, 800, 950, 12000);
    }

    /**
     * Tests getId and setId methods.
     */
    @Test
    void testId() {
        plane.setId(42);
        assertEquals(42, plane.getId());
    }

    /**
     * Tests getModel and setModel methods.
     */
    @Test
    void testModel() {
        plane.setModel("Boeing 737");
        assertEquals("Boeing 737", plane.getModel());
    }

    /**
     * Tests getCapacity and setCapacity methods.
     */
    @Test
    void testCapacity() {
        plane.setCapacity(180);
        assertEquals(180, plane.getCapacity());
    }

    /**
     * Tests getCargoCapacity and setCargoCapacity methods.
     */
    @Test
    void testCargoCapacity() {
        plane.setCargoCapacity(22.7);
        assertEquals(22.7, plane.getCargoCapacity());
    }

    /**
     * Tests getRange and setRange methods.
     */
    @Test
    void testRange() {
        plane.setRange(4000);
        assertEquals(4000, plane.getRange());
    }

    /**
     * Tests getFuelConsumption and setFuelConsumption methods.
     */
    @Test
    void testFuelConsumption() {
        plane.setFuelConsumption(5.5);
        assertEquals(5.5, plane.getFuelConsumption());
    }

    /**
     * Tests getCruisingSpeed and setCruisingSpeed methods.
     */
    @Test
    void testCruisingSpeed() {
        plane.setCruisingSpeed(850);
        assertEquals(850, plane.getCruisingSpeed());
    }

    /**
     * Tests getMaxSpeed and setMaxSpeed methods.
     */
    @Test
    void testMaxSpeed() {
        plane.setMaxSpeed(980);
        assertEquals(980, plane.getMaxSpeed());
    }

    /**
     * Tests getServiceCeiling and setServiceCeiling methods.
     */
    @Test
    void testServiceCeiling() {
        plane.setServiceCeiling(14000);
        assertEquals(14000, plane.getServiceCeiling());
    }

    /**
     * Tests getImagePath and setImagePath methods.
     */
    @Test
    void testImagePath() {
        plane.setImagePath("images/sample.jpg");
        assertEquals("images/sample.jpg", plane.getImagePath());
    }

    /**
     * Tests getType and setType methods.
     */
    @Test
    void testType() {
        plane.setType("Cargo");
        assertEquals("Cargo", plane.getType());
    }

    /**
     * Tests the toString method for expected format and content.
     */
    @Test
    void testToString() {
        plane.setType("Passenger");
        plane.setImagePath("images/test.jpg");
        String str = plane.toString();
        assertNotNull(str);
        assertTrue(str.contains("TestModel"));
        assertTrue(str.contains("Passenger"));
        assertTrue(str.contains("100"));
        assertTrue(str.contains("10.50"));
        assertTrue(str.contains("3000"));
        assertTrue(str.contains("2"));
        assertTrue(str.contains("800"));
        assertTrue(str.contains("950"));
        assertTrue(str.contains("12000"));
        assertTrue(str.contains("images/test.jpg"));
    }
}
