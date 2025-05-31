package airline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link LightPlane} class.
 * Covers constructor, all getters and setters, getType, and toString.
 */
class LightPlaneTest {

    private LightPlane lightPlane;

    /**
     * Initializes a test instance before each test.
     */
    @BeforeEach
    void setUp() {
        lightPlane = new LightPlane(
                "Cessna 172", 4, 0.5, 1280, 0.25,
                226, 302, 4100
        );
        lightPlane.setId(801);
        lightPlane.setImagePath("images/cessna172.jpg");
    }

    /**
     * Tests the constructor and all getter methods.
     */
    @Test
    void testConstructorAndGetters() {
        assertEquals("Cessna 172", lightPlane.getModel());
        assertEquals(4, lightPlane.getCapacity());
        assertEquals(0.5, lightPlane.getCargoCapacity());
        assertEquals(1280, lightPlane.getRange());
        assertEquals(0.25, lightPlane.getFuelConsumption());
        assertEquals(226, lightPlane.getCruisingSpeed());
        assertEquals(302, lightPlane.getMaxSpeed());
        assertEquals(4100, lightPlane.getServiceCeiling());
        assertEquals(801, lightPlane.getId());
        assertEquals("images/cessna172.jpg", lightPlane.getImagePath());
    }

    /**
     * Tests the getType method returns "Light Plane".
     */
    @Test
    void testGetType() {
        assertEquals("Light Plane", lightPlane.getType());
    }

    /**
     * Tests the set and get methods for imagePath.
     */
    @Test
    void testSetImagePath() {
        lightPlane.setImagePath("images/updated_cessna172.jpg");
        assertEquals("images/updated_cessna172.jpg", lightPlane.getImagePath());
    }

    /**
     * Tests the setId and getId methods.
     */
    @Test
    void testSetId() {
        lightPlane.setId(909);
        assertEquals(909, lightPlane.getId());
    }

    /**
     * Tests the toString method contains expected information.
     */
    @Test
    void testToString() {
        String str = lightPlane.toString();
        assertNotNull(str);
        assertTrue(str.contains("Cessna 172"));
        assertTrue(str.contains("Light Plane"));
        assertTrue(str.contains("4"));
        assertTrue(str.contains("0.50"));
        assertTrue(str.contains("1280"));
        assertTrue(str.contains("0.25"));
        assertTrue(str.contains("226"));
        assertTrue(str.contains("302"));
        assertTrue(str.contains("4100"));
        assertTrue(str.contains("images/cessna172.jpg"));
    }
}
