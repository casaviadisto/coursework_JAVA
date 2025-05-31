package airline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Fighter} class.
 * Covers constructor, all getters and setters, getType, and toString.
 */
class FighterTest {

    private Fighter fighter;

    /**
     * Initializes a test instance before each test.
     */
    @BeforeEach
    void setUp() {
        fighter = new Fighter(
                "F-22 Raptor", 8.7, 4200, 3.9,
                1500, 2000, 15240
        );
        fighter.setId(601);
        fighter.setImagePath("images/f22.jpg");
    }

    /**
     * Tests the constructor and all getter methods.
     */
    @Test
    void testConstructorAndGetters() {
        assertEquals("F-22 Raptor", fighter.getModel());
        assertEquals(0, fighter.getCapacity()); // Always 0 for Fighter
        assertEquals(8.7, fighter.getCargoCapacity());
        assertEquals(4200, fighter.getRange());
        assertEquals(3.9, fighter.getFuelConsumption());
        assertEquals(1500, fighter.getCruisingSpeed());
        assertEquals(2000, fighter.getMaxSpeed());
        assertEquals(15240, fighter.getServiceCeiling());
        assertEquals(601, fighter.getId());
        assertEquals("images/f22.jpg", fighter.getImagePath());
    }

    /**
     * Tests the getType method returns "Fighter".
     */
    @Test
    void testGetType() {
        assertEquals("Fighter", fighter.getType());
    }

    /**
     * Tests the set and get methods for imagePath.
     */
    @Test
    void testSetImagePath() {
        fighter.setImagePath("images/updated_f22.jpg");
        assertEquals("images/updated_f22.jpg", fighter.getImagePath());
    }

    /**
     * Tests the setId and getId methods.
     */
    @Test
    void testSetId() {
        fighter.setId(999);
        assertEquals(999, fighter.getId());
    }

    /**
     * Tests the toString method contains expected information.
     */
    @Test
    void testToString() {
        String str = fighter.toString();
        assertNotNull(str);
        assertTrue(str.contains("F-22 Raptor"));
        assertTrue(str.contains("Fighter"));
        assertTrue(str.contains("8.70"));
        assertTrue(str.contains("4200"));
        assertTrue(str.contains("3.90"));
        assertTrue(str.contains("1500"));
        assertTrue(str.contains("2000"));
        assertTrue(str.contains("15240"));
        assertTrue(str.contains("images/f22.jpg"));
    }
}
