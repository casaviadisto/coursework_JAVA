package db;

import airline.*;
import airline.util.PlaneFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages SQLite database operations related to the Plane entities.
 * Handles creation, insertion, update, deletion, and retrieval of plane data.
 * All major actions and exceptions are logged using SLF4J.
 */
public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    // Default database URL (for main app)
    private static final String DEFAULT_DB_URL = "jdbc:sqlite:src/main/resources/airline.db";
    private final String dbUrl;

    /**
     * Constructs a DatabaseManager and ensures the required table exists, using default DB URL.
     */
    public DatabaseManager() {
        this.dbUrl = DEFAULT_DB_URL;
        logger.info("DatabaseManager CONSTRUCTOR LOG: initialization with dbUrl = {}", dbUrl);
        createTableIfNotExists();
    }


    /**
     * Constructs a DatabaseManager with a custom database URL (e.g., for testing).
     * @param dbUrl JDBC SQLite URL (e.g., "jdbc:sqlite:memory:" or file path)
     */
    public DatabaseManager(String dbUrl) {
        this.dbUrl = dbUrl;
        createTableIfNotExists();
    }

    /**
     * Creates the "planes" table if it does not already exist in the database.
     * Logs any errors during table creation.
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
            logger.info("Checked/created 'planes' table in DB: {}", dbUrl);
        } catch (SQLException e) {
            logger.error("Error creating planes table in DB: {}", dbUrl, e);
        }
    }

    /**
     * Establishes a connection to the SQLite database.
     *
     * @return the established SQL Connection
     * @throws SQLException if connection fails
     */
    private Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found.", e);
        }
        Connection conn = DriverManager.getConnection(this.dbUrl);
        logger.debug("Established DB connection: {}", dbUrl);
        return conn;
    }

    /**
     * Inserts a new plane into the database and logs the operation.
     *
     * @param plane the plane to be added
     */
    public void addPlane(Plane plane) {
        String sql = """
            INSERT INTO planes (type, model, capacity, cargo_capacity, range_km, 
                                fuel_consumption, cruising_speed, max_speed, 
                                service_ceiling, image_path)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
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

            pstmt.executeUpdate();
            logger.info("Plane '{}' added to DB (type: {})", plane.getModel(), plane.getType());
        } catch (SQLException e) {
            logger.error("Error adding plane '{}' to DB", plane.getModel(), e);
        }
    }

    /**
     * Updates an existing plane's data in the database and logs the operation.
     *
     * @param plane the plane with updated information
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
                logger.info("Plane '{}' (ID: {}) updated in DB", plane.getModel(), plane.getId());
            } else {
                logger.warn("Update attempted for non-existent plane ID: {}", plane.getId());
            }
        } catch (SQLException e) {
            logger.error("Error updating plane '{}' (ID: {}) in DB", plane.getModel(), plane.getId(), e);
        }
    }

    /**
     * Deletes a plane from the database using its ID and logs the operation.
     *
     * @param id the unique ID of the plane
     * @return true if deletion was successful, false otherwise
     */
    public boolean deletePlane(int id) {
        String sql = "DELETE FROM planes WHERE id = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                logger.info("Plane with ID {} deleted from DB", id);
                return true;
            } else {
                logger.warn("Delete attempted for non-existent plane ID: {}", id);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting plane with ID {} from DB", id, e);
            return false;
        }
    }

    /**
     * Retrieves all planes stored in the database and logs the action.
     *
     * @return a list of all Plane objects from the database
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
                String image = rs.getString("image_path");

                Plane p = PlaneFactory.createPlane(
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
                p.setId(id);
                p.setImagePath(image);
                list.add(p);
            }
            logger.info("Loaded {} planes from DB", list.size());
        } catch (SQLException e) {
            logger.error("Error reading planes from DB", e);
        }
        return list;
    }
}
