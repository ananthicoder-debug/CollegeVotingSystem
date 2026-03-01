import gui.LoginFrame;
import utils.DatabaseConnection;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Main application class for College Voting System
 * 
 * @author NinjaTech AI Team
 * @version 1.0
 */
public class VotingSystemApp {
    
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Test database connection
        System.out.println("===========================================");
        System.out.println("College Voting System - Starting...");
        System.out.println("===========================================");
        
        try {
            if (DatabaseConnection.testConnection()) {
                System.out.println("✓ Database connection successful!");
                System.out.println("✓ System ready to use");
                System.out.println("===========================================\n");
                
                // Launch the application
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        LoginFrame loginFrame = new LoginFrame();
                        loginFrame.setVisible(true);
                    }
                });
            } else {
                System.err.println("✗ Database connection failed!");
                System.err.println("✗ Please check your database configuration");
                System.err.println("===========================================\n");
                
                JOptionPane.showMessageDialog(null, 
                    "Failed to connect to database!\n\n" +
                    "Please ensure:\n" +
                    "1. MySQL server is running\n" +
                    "2. Database 'college_voting_system' exists\n" +
                    "3. Username and password are correct in DatabaseConnection.java\n" +
                    "4. MySQL JDBC driver is in classpath",
                    "Database Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
                
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("✗ Error starting application: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(null, 
                "Error starting application:\n" + e.getMessage(),
                "Application Error", 
                JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
        }
    }
}

