package org.example.javafx_jasperreports;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:" + Paths.get("database", "chinook.db").toAbsolutePath();
    private static Connection connection = null;
    private DatabaseConnection() {}

    // Metodo para obtener la conexión a la base de datos
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL);
                System.out.println("Conexión a SQLite establecida.");
            } catch (SQLException e) {
                System.err.println("Error al conectar con la base de datos: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Metodo para cerrar la conexión a la base de datos
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Conexión a SQLite cerrada.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
