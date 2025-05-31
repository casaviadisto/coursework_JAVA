package airline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link BusinessJet} class.
 * Covers constructor, all getters and setters, getType, and toString.
 */
class BusinessJetTest {

    private BusinessJet jet;

    /**
     * Initializes a test instance before each test.
     */
    @BeforeEach
    void setUp() {
        jet = new BusinessJet(
                "Gulfstream G650", 18, 5, 12900, 2.5,
                900, 980, 15545
        );
        jet.setId(401);
        jet.setImagePath("images/g650.jpg");
    }

    /**
     * Tests the constructor and all getter methods.
     */
    @Test
    void testConstructorAndGetters() {
        assertEquals("Gulfstream G650", jet.getModel());
        assertEquals(18, jet.getCapacity());
        assertEquals(5, jet.getCargoCapacity());
        assertEquals(12900, jet.getRange());
        assertEquals(2.5, jet.getFuelConsumption());
        assertEquals(900, jet.getCruisingSpeed());
        assertEquals(980, jet.getMaxSpeed());
        assertEquals(15545, jet.getServiceCeiling());
        assertEquals(401, jet.getId());
        assertEquals("images/g650.jpg", jet.getImagePath());
    }

    /**
     * Tests the getType method returns "Business Jet".
     */
    @Test
    void testGetType() {
        assertEquals("Business Jet", jet.getType());
    }

    /**
     * Tests the set and get methods for imagePath.
     */
    @Test
    void testSetImagePath() {
        jet.setImagePath("images/updated_g650.jpg");
        assertEquals("images/updated_g650.jpg", jet.getImagePath());
    }

    /**
     * Tests the setId and getId methods.
     */
    @Test
    void testSetId() {
        jet.setId(777);
        assertEquals(777, jet.getId());
    }

    /**
     * Tests the toString method contains expected information.
     */
    @Test
    void testToString() {
        String str = jet.toString();
        assertNotNull(str);
        assertTrue(str.contains("Gulfstream G650"));
        assertTrue(str.contains("Business Jet"));
        assertTrue(str.contains("18"));
        assertTrue(str.contains("5"));
        assertTrue(str.contains("12900"));
        assertTrue(str.contains("2.50"));
        assertTrue(str.contains("900"));
        assertTrue(str.contains("980"));
        assertTrue(str.contains("15545"));
        assertTrue(str.contains("images/g650.jpg"));
    }
}
