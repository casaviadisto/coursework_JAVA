package airline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link CargoPlane} class.
 * Covers constructor, all getters and setters, getType, and toString.
 */
class CargoPlaneTest {

    private CargoPlane cargoPlane;

    /**
     * Initializes a test instance before each test.
     */
    @BeforeEach
    void setUp() {
        cargoPlane = new CargoPlane(
                "Antonov AN-124", 120, 4800, 9.8,
                750, 850, 12000
        );
        cargoPlane.setId(501);
        cargoPlane.setImagePath("images/an124.jpg");
    }

    /**
     * Tests the constructor and all getter methods.
     */
    @Test
    void testConstructorAndGetters() {
        assertEquals("Antonov AN-124", cargoPlane.getModel());
        assertEquals(0, cargoPlane.getCapacity()); // Always 0 for CargoPlane
        assertEquals(120, cargoPlane.getCargoCapacity());
        assertEquals(4800, cargoPlane.getRange());
        assertEquals(9.8, cargoPlane.getFuelConsumption());
        assertEquals(750, cargoPlane.getCruisingSpeed());
        assertEquals(850, cargoPlane.getMaxSpeed());
        assertEquals(12000, cargoPlane.getServiceCeiling());
        assertEquals(501, cargoPlane.getId());
        assertEquals("images/an124.jpg", cargoPlane.getImagePath());
    }

    /**
     * Tests the getType method returns "Cargo".
     */
    @Test
    void testGetType() {
        assertEquals("Cargo", cargoPlane.getType());
    }

    /**
     * Tests the set and get methods for imagePath.
     */
    @Test
    void testSetImagePath() {
        cargoPlane.setImagePath("images/updated_an124.jpg");
        assertEquals("images/updated_an124.jpg", cargoPlane.getImagePath());
    }

    /**
     * Tests the setId and getId methods.
     */
    @Test
    void testSetId() {
        cargoPlane.setId(777);
        assertEquals(777, cargoPlane.getId());
    }

    /**
     * Tests the toString method contains expected information.
     */
    @Test
    void testToString() {
        String str = cargoPlane.toString();
        assertNotNull(str);
        assertTrue(str.contains("Antonov AN-124"));
        assertTrue(str.contains("Cargo"));
        assertTrue(str.contains("120"));
        assertTrue(str.contains("4800"));
        assertTrue(str.contains("9"));
        assertTrue(str.contains("750.00"));
        assertTrue(str.contains("850"));
        assertTrue(str.contains("12000"));
        assertTrue(str.contains("images/an124.jpg"));
    }
}
