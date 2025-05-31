package airline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Interceptor} class.
 * Covers constructor, all getters and setters, getType, and toString.
 */
class InterceptorTest {

    private Interceptor interceptor;

    /**
     * Initializes a test instance before each test.
     */
    @BeforeEach
    void setUp() {
        interceptor = new Interceptor(
                "MiG-31 Foxhound", 5, 1250, 3.1,
                1500, 3000, 20600
        );
        interceptor.setId(701);
        interceptor.setImagePath("images/mig31.jpg");
    }

    /**
     * Tests the constructor and all getter methods.
     */
    @Test
    void testConstructorAndGetters() {
        assertEquals("MiG-31 Foxhound", interceptor.getModel());
        assertEquals(0, interceptor.getCapacity()); // Always 0 for Interceptor
        assertEquals(5, interceptor.getCargoCapacity());
        assertEquals(1250, interceptor.getRange());
        assertEquals(3.1, interceptor.getFuelConsumption());
        assertEquals(1500, interceptor.getCruisingSpeed());
        assertEquals(3000, interceptor.getMaxSpeed());
        assertEquals(20600, interceptor.getServiceCeiling());
        assertEquals(701, interceptor.getId());
        assertEquals("images/mig31.jpg", interceptor.getImagePath());
    }

    /**
     * Tests the getType method returns "Interceptor".
     */
    @Test
    void testGetType() {
        assertEquals("Interceptor", interceptor.getType());
    }

    /**
     * Tests the set and get methods for imagePath.
     */
    @Test
    void testSetImagePath() {
        interceptor.setImagePath("images/updated_mig31.jpg");
        assertEquals("images/updated_mig31.jpg", interceptor.getImagePath());
    }

    /**
     * Tests the setId and getId methods.
     */
    @Test
    void testSetId() {
        interceptor.setId(808);
        assertEquals(808, interceptor.getId());
    }

    /**
     * Tests the toString method contains expected information.
     */
    @Test
    void testToString() {
        String str = interceptor.toString();
        assertNotNull(str);
        assertTrue(str.contains("MiG-31 Foxhound"));
        assertTrue(str.contains("Interceptor"));
        assertTrue(str.contains("5"));
        assertTrue(str.contains("1250"));
        assertTrue(str.contains("3.10"));
        assertTrue(str.contains("1500"));
        assertTrue(str.contains("3000"));
        assertTrue(str.contains("20600"));
        assertTrue(str.contains("images/mig31.jpg"));
    }
}
