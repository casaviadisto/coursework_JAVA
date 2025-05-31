package airline;

import db.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link Airline} class.
 * Uses Mockito to mock the DatabaseManager and isolate Airline logic.
 */
class AirlineTest {

    private Airline airline;
    private DatabaseManager dbManager;

    private Plane plane1;
    private Plane plane2;

    /**
     * Sets up Airline and mock data before each test.
     */
    @BeforeEach
    void setUp() {
        dbManager = Mockito.mock(DatabaseManager.class);
        airline = new Airline(dbManager);

        // Using a simple concrete subclass for Plane
        plane1 = new Plane("ModelA", 100, 10.0, 2000, 3.5, 700, 800, 12000) {};
        plane1.setId(1);
        plane1.setType("Passenger");

        plane2 = new Plane("ModelB", 150, 20.0, 2500, 4.0, 750, 850, 13000) {};
        plane2.setId(2);
        plane2.setType("Cargo");
    }

    /**
     * Tests getPlanes method.
     */
    @Test
    void testGetPlanes() {
        when(dbManager.getAllPlanes()).thenReturn(Arrays.asList(plane1, plane2));
        List<Plane> planes = airline.getPlanes();
        assertEquals(2, planes.size());
        assertTrue(planes.contains(plane1));
        assertTrue(planes.contains(plane2));
    }

    /**
     * Tests addPlane method.
     */
    @Test
    void testAddPlane() {
        airline.addPlane(plane1);
        verify(dbManager, times(1)).addPlane(plane1);
    }

    /**
     * Tests updatePlane method.
     */
    @Test
    void testUpdatePlane() {
        airline.updatePlane(plane1);
        verify(dbManager, times(1)).updatePlane(plane1);
    }

    /**
     * Tests removePlane method.
     */
    @Test
    void testRemovePlane() {
        when(dbManager.deletePlane(1)).thenReturn(true);
        boolean result = airline.removePlane(1);
        assertTrue(result);
        verify(dbManager, times(1)).deletePlane(1);

        when(dbManager.deletePlane(99)).thenReturn(false);
        assertFalse(airline.removePlane(99));
    }

    /**
     * Tests findPlaneByModel method (case-insensitive).
     */
    @Test
    void testFindPlaneByModel() {
        when(dbManager.getAllPlanes()).thenReturn(Arrays.asList(plane1, plane2));
        Plane found = airline.findPlaneByModel("modela");
        assertNotNull(found);
        assertEquals("ModelA", found.getModel());

        Plane notFound = airline.findPlaneByModel("DoesNotExist");
        assertNull(notFound);
    }

    /**
     * Tests getTotalCapacity method.
     */
    @Test
    void testGetTotalCapacity() {
        when(dbManager.getAllPlanes()).thenReturn(Arrays.asList(plane1, plane2));
        assertEquals(250, airline.getTotalCapacity());

        when(dbManager.getAllPlanes()).thenReturn(Collections.emptyList());
        assertEquals(0, airline.getTotalCapacity());
    }

    /**
     * Tests getTotalCargoCapacity method.
     */
    @Test
    void testGetTotalCargoCapacity() {
        when(dbManager.getAllPlanes()).thenReturn(Arrays.asList(plane1, plane2));
        assertEquals(30.0, airline.getTotalCargoCapacity(), 0.0001);

        when(dbManager.getAllPlanes()).thenReturn(Collections.emptyList());
        assertEquals(0.0, airline.getTotalCargoCapacity(), 0.0001);
    }

    /**
     * Tests getPlaneIdByModel method.
     */
    @Test
    void testGetPlaneIdByModel() {
        when(dbManager.getAllPlanes()).thenReturn(Arrays.asList(plane1, plane2));
        Integer id = airline.getPlaneIdByModel("ModelA");
        assertEquals(1, id);

        Integer notFound = airline.getPlaneIdByModel("Unknown");
        assertNull(notFound);
    }
}
