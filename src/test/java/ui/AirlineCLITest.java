package ui;

import airline.Airline;
import airline.PassengerPlane;
import airline.Plane;
import db.DatabaseManager;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit test for {@link AirlineCLI}.
 * Covers all core methods, lines, and branches via simulated user input and output.
 * Uses a temporary SQLite database for isolation.
 */
class AirlineCLITest {

    private static final String TEST_DB_PATH = "test_cli_airline.db";
    private Airline airline;
    private DatabaseManager dbManager;

    @BeforeEach
    void setup() {
        File dbFile = new File(TEST_DB_PATH);
        if (dbFile.exists()) dbFile.delete();
        dbManager = new DatabaseManager("jdbc:sqlite:" + TEST_DB_PATH);
        airline = new Airline(dbManager);
    }

    @AfterEach
    void teardown() {
        File dbFile = new File(TEST_DB_PATH);
        if (dbFile.exists()) dbFile.delete();
    }

    /**
     * Full-coverage test that exercises all CLI commands, branches, and edge cases.
     */
    @Test
    void testFullCoverage() {
        // Compose simulated user input
        String userInput = String.join("\n",
                // 1. Add plane (Passenger)
                "1", "1", "TestModel", "100", "10.5", "5000", "3.5", "700", "950", "12000",
                // 2. Add plane (Cargo)
                "1", "2", "CargoModel", "0", "50.0", "3000", "8.2", "600", "800", "8500",
                // 3. Try add with wrong type (invalid index)
                "1", "99",
                // 4. List all
                "4",
                // 5. Search (simple, success)
                "5", "1", "Cargo",
                // 6. Search (simple, not found)
                "5", "1", "NONEXISTENT",
                // 7. Search (advanced, skip types, one match)
                "5", "2", "", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "",
                // 8. Search (advanced, pick only 'Passenger')
                "5", "2", "", "50", "150", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "1",
                // 9. Edit first plane: change name and passenger count, skip others
                "3", "TestModel", "EditedModel", "111", "", "", "", "", "", "", "",
                // 10. Edit not existing plane (should not fail)
                "3", "NotExist",
                // 11. Sort planes by model ascending
                "6", "1", "1",
                // 12. Sort planes by cargo capacity descending
                "6", "3", "2",
                // 13. Remove plane (CargoModel), cancel deletion
                "2", "CargoModel", "n",
                // 14. Remove plane (CargoModel), confirm deletion
                "2", "CargoModel", "y",
                // 15. Remove plane (not found)
                "2", "NotExist",
                // 16. Unknown menu option
                "999",
                // 17. Exit
                "7"
        ) + "\n";

        ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);

        AirlineCLI cli = new AirlineCLI(airline, new Scanner(in), printStream);
        cli.run();

        // Print the CLI output (for debug; you can remove after checking)
        System.out.println(out);

        // Check planes list (after edit & delete)
        List<Plane> planes = airline.getPlanes();
        assertEquals(1, planes.size());
        assertTrue(planes.stream().anyMatch(p -> p.getModel().equals("EditedModel") && p.getCapacity() == 111));
        assertFalse(planes.stream().anyMatch(p -> p.getModel().equals("CargoModel")));

        String output = out.toString();

        // Main branch and error handling checks
        assertTrue(output.contains("❌ Невірний вибір типу."));
        assertTrue(output.contains("=== Список літаків ==="));
        assertTrue(output.contains("CargoModel")); // було на етапі list
        assertTrue(output.contains("🔍 Знайдені літаки:"));
        assertTrue(output.contains("❌ Не знайдено жодного літака."));
        assertTrue(output.contains("🔍 Результати пошуку"));
        assertTrue(output.contains("Passenger"));
        assertTrue(output.contains("✅ Оновлено."));
        assertTrue(output.contains("❌ Літак не знайдено."));
        assertTrue(output.contains("=== Результат сортування ==="));
        assertTrue(output.contains("Скасовано."));
        assertTrue(output.contains("✅ Видалено."));
        assertTrue(output.contains("❌ Літак не знайдено.")); // remove not found
        assertTrue(output.contains("❌ Невідомий вибір. Спробуйте ще."));
        assertTrue(output.contains("Ваш вибір: "));
    }
}
