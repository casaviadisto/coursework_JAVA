package airline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PassengerPlane} class.
 * Covers constructor, all getters and setters, getType, and toString.
 */
class PassengerPlaneTest {

    private PassengerPlane passengerPlane;

    /**
     * Initializes a test instance before each test.
     */
    @BeforeEach
    void setUp() {
        passengerPlane = new PassengerPlane(
                "Airbus A320", 180, 20, 6150, 2.7,
                840, 890, 12000
        );
        passengerPlane.setId(901);
        passengerPlane.setImagePath("images/a320.jpg");
    }

    /**
     * Tests the constructor and all getter methods.
     */
    @Test
    void testConstructorAndGetters() {
        assertEquals("Airbus A320", passengerPlane.getModel());
        assertEquals(180, passengerPlane.getCapacity());
        assertEquals(20, passengerPlane.getCargoCapacity());
        assertEquals(6150, passengerPlane.getRange());
        assertEquals(2.7, passengerPlane.getFuelConsumption());
        assertEquals(840, passengerPlane.getCruisingSpeed());
        assertEquals(890, passengerPlane.getMaxSpeed());
        assertEquals(12000, passengerPlane.getServiceCeiling());
        assertEquals(901, passengerPlane.getId());
        assertEquals("images/a320.jpg", passengerPlane.getImagePath());
    }

    /**
     * Tests the getType method returns "Passenger".
     */
    @Test
    void testGetType() {
        assertEquals("Passenger", passengerPlane.getType());
    }

    /**
     * Tests the set and get methods for imagePath.
     */
    @Test
    void testSetImagePath() {
        passengerPlane.setImagePath("images/updated_a320.jpg");
        assertEquals("images/updated_a320.jpg", passengerPlane.getImagePath());
    }

    /**
     * Tests the setId and getId methods.
     */
    @Test
    void testSetId() {
        passengerPlane.setId(1002);
        assertEquals(1002, passengerPlane.getId());
    }

    /**
     * Tests the toString method contains expected information.
     */
    @Test
    void testToString() {
        String str = passengerPlane.toString();
        assertNotNull(str);
        assertTrue(str.contains("Airbus A320"));
        assertTrue(str.contains("Passenger"));
        assertTrue(str.contains("180"));
        assertTrue(str.contains("20"));
        assertTrue(str.contains("6150"));
        assertTrue(str.contains("2.70"));
        assertTrue(str.contains("840"));
        assertTrue(str.contains("890"));
        assertTrue(str.contains("12000"));
        assertTrue(str.contains("images/a320.jpg"));
    }
}
