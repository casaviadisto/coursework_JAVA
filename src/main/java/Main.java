import java.io.PrintStream;
import java.util.Scanner;

/**
 * Entry point for the Airline application.
 * <p>
 * Depending on the command-line arguments, this class either launches
 * the JavaFX-based graphical user interface (GUI) or starts the
 * command-line interface (CLI) for managing the airline’s fleet.
 * </p>
 */
public class Main {

    /**
     * Main method that determines which interface to launch.
     * <p>
     * If the first argument is {@code "cli"} (case-insensitive), the CLI
     * will be started. Otherwise, the JavaFX GUI {@link ui.AirlineAppGUI}
     * is launched.
     * </p>
     *
     * @param args an array of command-line arguments.
     *             <ul>
     *               <li>If {@code args.length > 0} and {@code args[0].equalsIgnoreCase("cli")},
     *                   the CLI is started.</li>
     *               <li>All other cases launch the GUI.</li>
     *             </ul>
     */
    public static void main(String[] args) {
        // If the first argument is "cli", start the text-based interface.
        if (args.length > 0 && args[0].equalsIgnoreCase("cli")) {
            runCLI();
        } else {
            // Otherwise, launch the JavaFX GUI.
            ui.AirlineAppGUI.main(args);
        }
    }

    /**
     * Initializes and runs the command-line interface for the Airline application.
     * <p>
     * This method creates a {@link Scanner} tied to {@code System.in} for
     * user input and a {@link PrintStream} tied to {@code System.out} for output.
     * It then constructs an instance of {@link ui.AirlineCLI} and invokes its
     * {@code run()} method to start processing user commands. Finally, the
     * scanner is closed when the CLI session ends.
     * </p>
     */
    private static void runCLI() {
        Scanner scanner = new Scanner(System.in);
        PrintStream out = System.out;

        ui.AirlineCLI cli = new ui.AirlineCLI(scanner, out);
        cli.run();

        scanner.close();
    }
}
