import org.junit.jupiter.api.Test;

/**
 * Unit test for the {@link Main} class.
 * Ensures that the application's main entry point launches without throwing exceptions.
 */
class MainTest {

    /**
     * Smoke test for the {@link Main#main(String[])} method.
     * Verifies that the method runs without throwing any exceptions.
     */
    @Test
    void testMainMethodDoesNotThrow() {
        // This test simply calls the main method to ensure there are no runtime exceptions.
        // No assertions needed; test will fail if any exception is thrown.
        Main.main(new String[]{});
    }
}
