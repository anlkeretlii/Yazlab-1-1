package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBManager {
    private Connection conn = null;

    // Veritabanına bağlanmak için kullanılır.
    public void connect() {
        try {
            String url = "jdbc:mysql://localhost:3306/yazlab1";
            String user = "root";
            String password = "120494";

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully.");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return conn;
    }

}
