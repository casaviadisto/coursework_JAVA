package db;

import airline.*;
import airline.util.PlaneFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:airline.db";

    public DatabaseManager() {
        createTableIfNotExists();
    }

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

    private Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Драйвер SQLite не знайдено: " + e.getMessage());
        }
        return DriverManager.getConnection(DB_URL);
    }


    public void addPlane(Plane plane) {
        String sql = """
            INSERT INTO planes (type, model, capacity, cargo_capacity, range_km, fuel_consumption, image_path)
            VALUES (?, ?, ?, ?, ?, ?, ?);
        """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plane.getType());
            pstmt.setString(2, plane.getModel());
            pstmt.setInt(3, plane.getCapacity());
            pstmt.setDouble(4, plane.getCargoCapacity());
            pstmt.setInt(5, plane.getRange());
            pstmt.setDouble(6, plane.getFuelConsumption());
            pstmt.setString(7, plane.getImagePath());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Помилка при додаванні літака: " + e.getMessage());
        }
    }

    public void updatePlane(Plane plane) {
        String sql = """
            UPDATE planes SET
                type = ?, capacity = ?, cargo_capacity = ?, range_km = ?,
                fuel_consumption = ?, image_path = ?
            WHERE model = ?;
        """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plane.getType());
            pstmt.setInt(2, plane.getCapacity());
            pstmt.setDouble(3, plane.getCargoCapacity());
            pstmt.setInt(4, plane.getRange());
            pstmt.setDouble(5, plane.getFuelConsumption());
            pstmt.setString(6, plane.getImagePath());
            pstmt.setString(7, plane.getModel());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Помилка при оновленні літака: " + e.getMessage());
        }
    }

    public void deletePlane(String model) {
        String sql = "DELETE FROM planes WHERE model = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, model);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Помилка при видаленні літака: " + e.getMessage());
        }
    }

    public List<Plane> getAllPlanes() {
        List<Plane> list = new ArrayList<>();
        String sql = "SELECT * FROM planes;";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String type = rs.getString("type");
                String model = rs.getString("model");
                int capacity = rs.getInt("capacity");
                double cargo = rs.getDouble("cargo_capacity");
                int range = rs.getInt("range_km");
                double fuel = rs.getDouble("fuel_consumption");
                String image = rs.getString("image_path");

                System.out.println("Зчитано з бази:");
                System.out.println(model + " | " + type + " | " + capacity + " пасажирів");

                Plane p = PlaneFactory.createPlane(type, model, capacity, cargo, range, fuel);
                p.setImagePath(image);
                list.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Помилка при зчитуванні літаків: " + e.getMessage());
        }
        return list;
    }
}
