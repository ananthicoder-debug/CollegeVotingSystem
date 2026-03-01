package utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility class
 * Manages database connections using singleton pattern
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/college_voting_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "rose16"; // Change this to your MySQL password
    
    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DatabaseConnection() {
    }

    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connection established successfully!");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found!");
                e.printStackTrace();
                throw new SQLException("Driver not found", e);
            } catch (SQLException e) {
                System.err.println("Failed to establish database connection!");
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed successfully!");
            } catch (SQLException e) {
                System.err.println("Error closing database connection!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Test database connection
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
