package db;

import airline.Plane;
import airline.util.PlaneFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages SQLite database operations related to Plane entities.
 * <p>
 * On first initialization, if a file named "airline.db" does not exist
 * next to the running JAR, this class will attempt to copy the
 * embedded resource "/airline.db" from inside the JAR into the working
 * directory, so that all JDBC calls operate on a file in the file system.
 * Thereafter, CRUD operations (create, read, update, delete) on the
 * "planes" table use the external "airline.db" file.
 * All major actions and exceptions are logged via SLF4J.
 * </p>
 */
public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    /**
     * The external (file-system) database file name. We expect to find
     * "airline.db" alongside the JAR once it is extracted or created.
     */
    private static final String EXTERNAL_DB_FILENAME = "airline.db";

    /**
     * JDBC URL pointing to the external SQLite file "airline.db" in the working directory.
     */
    private static final String DEFAULT_DB_URL = "jdbc:sqlite:" + EXTERNAL_DB_FILENAME;

    private final String dbUrl;

    /**
     * Constructs a DatabaseManager using the default URL ("jdbc:sqlite:airline.db").
     * <p>
     * If "airline.db" does not yet exist in the working directory, this constructor
     * attempts to copy it from the embedded resource "/airline.db" inside the JAR.
     * After ensuring the file exists on disk, it calls {@link #createTableIfNotExists()}
     * to initialize the "planes" table if necessary.
     * </p>
     */
    public DatabaseManager() {
        // Ensure external file exists (possibly copy from JAR resource):
        extractDatabaseFromJarIfNeeded();
        this.dbUrl = DEFAULT_DB_URL;
        logger.info("DatabaseManager initialized with dbUrl = {}", dbUrl);
        createTableIfNotExists();
    }

    /**
     * Constructs a DatabaseManager with a custom JDBC URL (for testing or in-memory operations).
     * <p>
     * Does not attempt to copy from a JAR resource in this overload; it uses the provided URL directly.
     * </p>
     *
     * @param dbUrl JDBC SQLite URL (e.g., "jdbc:sqlite:::memory:" or a file path)
     */
    public DatabaseManager(String dbUrl) {
        this.dbUrl = dbUrl;
        logger.info("DatabaseManager initialized with custom dbUrl = {}", dbUrl);
        createTableIfNotExists();
    }

    /**
     * If the external file "airline.db" does not exist in the working directory,
     * attempts to copy it from the embedded resource "/airline.db" inside the JAR.
     * <p>
     * If the resource is not found inside the JAR, an empty file "airline.db"
     * is created on disk. This ensures that subsequent JDBC operations can open
     * a file-based SQLite database.
     * </p>
     */
    private void extractDatabaseFromJarIfNeeded() {
        try {
            File externalFile = new File(EXTERNAL_DB_FILENAME);
            if (!externalFile.exists()) {
                // Look for resource "/airline.db" inside JAR
                try (InputStream is = getClass().getResourceAsStream("/airline.db")) {
                    if (is != null) {
                        // Copy resource stream to file "airline.db"
                        try (FileOutputStream fos = new FileOutputStream(externalFile)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, bytesRead);
                            }
                        }
                        logger.info("Copied embedded 'airline.db' resource into working directory");
                    } else {
                        // No embedded resource found; create an empty file
                        Files.createFile(Path.of(EXTERNAL_DB_FILENAME));
                        logger.info("Created new empty 'airline.db' in working directory");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to extract or create external 'airline.db': {}", e.getMessage(), e);
        }
    }

    /**
     * Creates the "planes" table in the database if it does not already exist.
     * <p>
     * The table schema:
     * <pre>
     * CREATE TABLE IF NOT EXISTS planes (
     *   id INTEGER PRIMARY KEY AUTOINCREMENT,
     *   type TEXT NOT NULL,
     *   model TEXT NOT NULL,
     *   capacity INTEGER,
     *   cargo_capacity REAL,
     *   range_km INTEGER,
     *   fuel_consumption REAL,
     *   cruising_speed REAL,
     *   max_speed REAL,
     *   service_ceiling INTEGER,
     *   image_path TEXT
     * );
     * </pre>
     * </p>
     */
    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS planes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT NOT NULL,
                model TEXT NOT NULL,
                capacity INTEGER,
                cargo_capacity REAL,
                range_km INTEGER,
                fuel_consumption REAL,
                cruising_speed REAL,
                max_speed REAL,
                service_ceiling INTEGER,
                image_path TEXT
            );
        """;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Ensured 'planes' table exists in DB: {}", dbUrl);
        } catch (SQLException e) {
            logger.error("Error creating 'planes' table in DB: {}", dbUrl, e);
        }
    }

    /**
     * Establishes a connection to the SQLite database specified by {@link #dbUrl}.
     *
     * @return a valid {@link Connection} instance
     * @throws SQLException if a database access error occurs
     */
    private Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found", e);
        }
        Connection conn = DriverManager.getConnection(this.dbUrl);
        logger.debug("Opened SQLite connection to {}", dbUrl);
        return conn;
    }

    /**
     * Inserts a new Plane into the "planes" table.
     *
     * @param plane the Plane object to add; its {@link Plane#getImagePath()} must be a string
     *              such as "/images/A-10.jpg" or null if no image.
     */
    public void addPlane(Plane plane) {
        String sql = """
            INSERT INTO planes (
                type, model, capacity, cargo_capacity, range_km,
                fuel_consumption, cruising_speed, max_speed,
                service_ceiling, image_path
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plane.getType());
            pstmt.setString(2, plane.getModel());
            pstmt.setInt(3, plane.getCapacity());
            pstmt.setDouble(4, plane.getCargoCapacity());
            pstmt.setInt(5, plane.getRange());
            pstmt.setDouble(6, plane.getFuelConsumption());
            pstmt.setDouble(7, plane.getCruisingSpeed());
            pstmt.setDouble(8, plane.getMaxSpeed());
            pstmt.setInt(9, plane.getServiceCeiling());
            pstmt.setString(10, plane.getImagePath()); // e.g. "/images/A-10.jpg"

            pstmt.executeUpdate();
            logger.info("Added plane '{}' of type '{}' to DB", plane.getModel(), plane.getType());
        } catch (SQLException e) {
            logger.error("Error adding plane '{}' to DB", plane.getModel(), e);
        }
    }

    /**
     * Updates an existing Plane's record in the "planes" table.
     *
     * @param plane the Plane with updated data; its {@link Plane#getId()} must match an existing row.
     */
    public void updatePlane(Plane plane) {
        String sql = """
            UPDATE planes SET
                type = ?, model = ?, capacity = ?, cargo_capacity = ?, range_km = ?,
                fuel_consumption = ?, cruising_speed = ?, max_speed = ?,
                service_ceiling = ?, image_path = ?
            WHERE id = ?;
        """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plane.getType());
            pstmt.setString(2, plane.getModel());
            pstmt.setInt(3, plane.getCapacity());
            pstmt.setDouble(4, plane.getCargoCapacity());
            pstmt.setInt(5, plane.getRange());
            pstmt.setDouble(6, plane.getFuelConsumption());
            pstmt.setDouble(7, plane.getCruisingSpeed());
            pstmt.setDouble(8, plane.getMaxSpeed());
            pstmt.setInt(9, plane.getServiceCeiling());
            pstmt.setString(10, plane.getImagePath());
            pstmt.setInt(11, plane.getId());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                logger.info("Updated plane '{}' (ID: {}) in DB", plane.getModel(), plane.getId());
            } else {
                logger.warn("No plane with ID {} found to update", plane.getId());
            }
        } catch (SQLException e) {
            logger.error("Error updating plane '{}' (ID: {}) in DB", plane.getModel(), plane.getId(), e);
        }
    }

    /**
     * Deletes a Plane from the "planes" table by its ID.
     *
     * @param id the unique ID of the plane to delete
     * @return true if a row was deleted, false otherwise
     */
    public boolean deletePlane(int id) {
        String sql = "DELETE FROM planes WHERE id = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                logger.info("Deleted plane with ID {} from DB", id);
                return true;
            } else {
                logger.warn("No plane with ID {} found to delete", id);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting plane ID {} from DB", id, e);
            return false;
        }
    }

    /**
     * Retrieves all Plane records from the "planes" table.
     *
     * @return a List of Plane instances representing all rows in the database
     */
    public List<Plane> getAllPlanes() {
        List<Plane> list = new ArrayList<>();
        String sql = "SELECT * FROM planes;";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                String model = rs.getString("model");
                int capacity = rs.getInt("capacity");
                double cargo = rs.getDouble("cargo_capacity");
                int range = rs.getInt("range_km");
                double fuel = rs.getDouble("fuel_consumption");
                double cruisingSpeed = rs.getDouble("cruising_speed");
                double maxSpeed = rs.getDouble("max_speed");
                int serviceCeiling = rs.getInt("service_ceiling");
                String imagePath = rs.getString("image_path"); // e.g. "/images/A-10.jpg"

                Plane plane = PlaneFactory.createPlane(
                        type,
                        model,
                        capacity,
                        cargo,
                        range,
                        fuel,
                        cruisingSpeed,
                        maxSpeed,
                        serviceCeiling
                );
                plane.setId(id);
                plane.setImagePath(imagePath);
                list.add(plane);
            }
            logger.info("Loaded {} plane(s) from DB", list.size());
        } catch (SQLException e) {
            logger.error("Error reading planes from DB", e);
        }
        return list;
    }
}
