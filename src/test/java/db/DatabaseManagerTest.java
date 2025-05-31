package db;

import airline.PassengerPlane;
import airline.Plane;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link DatabaseManager} class.
 * Uses a temporary SQLite database file for full coverage of DB operations.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseManagerTest {

    private static final String TEMP_DB_PATH = "test_airline.db";
    private DatabaseManager dbManager;

    @BeforeAll
    void setUpDatabase() {
        // Delete the temp file if it already exists
        File dbFile = new File(TEMP_DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        dbManager = new DatabaseManager("jdbc:sqlite:" + TEMP_DB_PATH);
    }

    @AfterAll
    void tearDownDatabase() {
        File dbFile = new File(TEMP_DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    /**
     * Tests addPlane and getAllPlanes methods.
     */
    @Test
    void testAddAndGetAllPlanes() {
        Plane plane = new PassengerPlane("TestJet", 100, 12, 2000, 3.5, 700, 900, 12000);
        plane.setImagePath("images/testjet.jpg");
        dbManager.addPlane(plane);

        List<Plane> planes = dbManager.getAllPlanes();
        assertFalse(planes.isEmpty());
        Plane result = planes.get(0);
        assertEquals("TestJet", result.getModel());
        assertEquals(100, result.getCapacity());
        assertEquals(12, result.getCargoCapacity());
        assertEquals("images/testjet.jpg", result.getImagePath());
    }

    /**
     * Tests updatePlane method.
     */
    @Test
    void testUpdatePlane() {
        List<Plane> planes = dbManager.getAllPlanes();
        assertFalse(planes.isEmpty());
        Plane plane = planes.get(0);

        plane.setModel("UpdatedJet");
        plane.setCapacity(150);
        plane.setCargoCapacity(18);
        plane.setImagePath("images/updated.jpg");
        dbManager.updatePlane(plane);

        List<Plane> updatedPlanes = dbManager.getAllPlanes();
        assertEquals(1, updatedPlanes.size());
        Plane updated = updatedPlanes.get(0);
        assertEquals("UpdatedJet", updated.getModel());
        assertEquals(150, updated.getCapacity());
        assertEquals(18, updated.getCargoCapacity());
        assertEquals("images/updated.jpg", updated.getImagePath());
    }

    /**
     * Tests deletePlane method.
     */
    @Test
    void testDeletePlane() {
        // Додаємо літак
        Plane plane = new PassengerPlane("TestJet", 100, 12, 2000, 3.5, 700, 900, 12000);
        plane.setImagePath("images/testjet.jpg");
        dbManager.addPlane(plane);

        // Витягуємо всі літаки, беремо id першого
        List<Plane> planes = dbManager.getAllPlanes();
        assertFalse(planes.isEmpty());
        int id = planes.get(0).getId();

        // Видаляємо — має повернути true
        boolean deleted = dbManager.deletePlane(id);
        assertTrue(deleted);

        // Повторне видалення — має повернути false
        boolean deletedAgain = dbManager.deletePlane(id);
        assertFalse(deletedAgain);

        // Після видалення не має бути жодного літака
        List<Plane> afterDelete = dbManager.getAllPlanes();
        assertTrue(afterDelete.isEmpty());
    }


    /**
     * Tests getAllPlanes returns an empty list for a new DB.
     */
    @Test
    void testGetAllPlanesEmpty() {
        DatabaseManager tempDb = new DatabaseManager("jdbc:sqlite::memory:");
        List<Plane> planes = tempDb.getAllPlanes();
        assertNotNull(planes);
        assertTrue(planes.isEmpty());
    }
}
