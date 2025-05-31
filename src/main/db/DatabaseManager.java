package db;

import airline.*;
import airline.util.PlaneFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages SQLite database operations related to the Plane entities.
 * Handles creation, insertion, update, deletion, and retrieval of plane data.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:airline.db";

    /**
     * Constructs a DatabaseManager and ensures the required table exists.
     */
    public DatabaseManager() {
        createTableIfNotExists();
    }

    /**
     * Creates the "planes" table if it does not already exist in the database.
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
        } catch (SQLException e) {
            System.err.println("Помилка створення таблиці: " + e.getMessage());
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
            System.err.println("Драйвер SQLite не знайдено: " + e.getMessage());
        }
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Inserts a new plane into the database.
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
        } catch (SQLException e) {
            System.err.println("Помилка при додаванні літака: " + e.getMessage());
        }
    }

    /**
     * Updates an existing plane's data in the database.
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

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Помилка при оновленні літака: " + e.getMessage());
        }
    }

    /**
     * Deletes a plane from the database using its ID.
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
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Помилка при видаленні літака: " + e.getMessage());
            return false;
        }
    }


    /**
     * Retrieves all planes stored in the database.
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

        } catch (SQLException e) {
            System.err.println("Помилка при зчитуванні літаків: " + e.getMessage());
        }
        return list;
    }
}