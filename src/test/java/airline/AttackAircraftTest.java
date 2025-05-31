package airline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link AttackAircraft} class.
 * Covers constructor, all getters, and the getType method.
 */
class AttackAircraftTest {

    private AttackAircraft attackAircraft;

    /**
     * Initializes a test instance before each test.
     */
    @BeforeEach
    void setUp() {
        attackAircraft = new AttackAircraft(
                "A-10 Thunderbolt", 7.3, 1240, 2.5,
                676, 706, 13700
        );
        attackAircraft.setId(101);
        attackAircraft.setImagePath("images/a10.jpg");
    }

    /**
     * Tests the constructor and all getter methods.
     */
    @Test
    void testConstructorAndGetters() {
        assertEquals("A-10 Thunderbolt", attackAircraft.getModel());
        assertEquals(0, attackAircraft.getCapacity()); // Always 0 for AttackAircraft
        assertEquals(7.3, attackAircraft.getCargoCapacity());
        assertEquals(1240, attackAircraft.getRange());
        assertEquals(2.5, attackAircraft.getFuelConsumption());
        assertEquals(676, attackAircraft.getCruisingSpeed());
        assertEquals(706, attackAircraft.getMaxSpeed());
        assertEquals(13700, attackAircraft.getServiceCeiling());
        assertEquals(101, attackAircraft.getId());
        assertEquals("images/a10.jpg", attackAircraft.getImagePath());
    }

    /**
     * Tests the getType method returns "Attack Aircraft".
     */
    @Test
    void testGetType() {
        assertEquals("Attack Aircraft", attackAircraft.getType());
    }

    /**
     * Tests the set and get methods for imagePath.
     */
    @Test
    void testSetImagePath() {
        attackAircraft.setImagePath("images/updated_a10.jpg");
        assertEquals("images/updated_a10.jpg", attackAircraft.getImagePath());
    }

    /**
     * Tests the setId and getId methods.
     */
    @Test
    void testSetId() {
        attackAircraft.setId(202);
        assertEquals(202, attackAircraft.getId());
    }

    /**
     * Tests the toString method contains expected information.
     */
    @Test
    void testToString() {
        String str = attackAircraft.toString();
        assertNotNull(str);
        assertTrue(str.contains("A-10 Thunderbolt"));
        assertTrue(str.contains("Attack Aircraft"));
        assertTrue(str.contains("7.30"));
        assertTrue(str.contains("1240"));
        assertTrue(str.contains("2.50"));
        assertTrue(str.contains("676"));
        assertTrue(str.contains("706"));
        assertTrue(str.contains("13700"));
        assertTrue(str.contains("images/a10.jpg"));
    }
}
