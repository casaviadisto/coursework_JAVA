package airline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Bomber} class.
 * Covers constructor, all getters and setters, getType, and toString.
 */
class BomberTest {

    private Bomber bomber;

    /**
     * Initializes a test instance before each test.
     */
    @BeforeEach
    void setUp() {
        bomber = new Bomber(
                "B-52 Stratofortress", 32, 14000, 12,
                845, 1000, 16700
        );
        bomber.setId(301);
        bomber.setImagePath("images/b52.jpg");
    }

    /**
     * Tests the constructor and all getter methods.
     */
    @Test
    void testConstructorAndGetters() {
        assertEquals("B-52 Stratofortress", bomber.getModel());
        assertEquals(0, bomber.getCapacity()); // Always 0 for Bomber
        assertEquals(32, bomber.getCargoCapacity());
        assertEquals(14000, bomber.getRange());
        assertEquals(12, bomber.getFuelConsumption());
        assertEquals(845, bomber.getCruisingSpeed());
        assertEquals(1000, bomber.getMaxSpeed());
        assertEquals(16700, bomber.getServiceCeiling());
        assertEquals(301, bomber.getId());
        assertEquals("images/b52.jpg", bomber.getImagePath());
    }

    /**
     * Tests the getType method returns "Bomber".
     */
    @Test
    void testGetType() {
        assertEquals("Bomber", bomber.getType());
    }

    /**
     * Tests the set and get methods for imagePath.
     */
    @Test
    void testSetImagePath() {
        bomber.setImagePath("images/updated_b52.jpg");
        assertEquals("images/updated_b52.jpg", bomber.getImagePath());
    }

    /**
     * Tests the setId and getId methods.
     */
    @Test
    void testSetId() {
        bomber.setId(999);
        assertEquals(999, bomber.getId());
    }

    /**
     * Tests the toString method contains expected information.
     */
    @Test
    void testToString() {
        String str = bomber.toString();
        assertNotNull(str);
        assertTrue(str.contains("B-52 Stratofortress"));
        assertTrue(str.contains("Bomber"));
        assertTrue(str.contains("32"));
        assertTrue(str.contains("14000"));
        assertTrue(str.contains("12"));
        assertTrue(str.contains("845"));
        assertTrue(str.contains("1000"));
        assertTrue(str.contains("16700"));
        assertTrue(str.contains("images/b52.jpg"));
    }
}
