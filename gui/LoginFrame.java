package gui;

import services.AuthenticationService;
import models.Admin;
import models.Voter;

import javax.swing.*;
import java.awt.*;

/**
 * Full-Screen Login Frame for Voters and Admins
 */
public class LoginFrame extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JComboBox<String> userTypeCombo;
    private AuthenticationService authService;

    public LoginFrame() {
        authService = new AuthenticationService();
        initComponents();
    }

    private void initComponents() {
        setTitle("College Voting System - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // ✅ Full-screen window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // ✅ Custom background panel
        JPanel backgroundPanel = new JPanel() {
            private final Image backgroundImage = new ImageIcon("src/images/Vote.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw image to fill full screen
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        // ===== Title Label =====
        JLabel titleLabel = new JLabel("COLLEGE VOTING SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.black);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int titleWidth = 800;
        int titleHeight = 60;
        int titleX = (screenSize.width - titleWidth) / 2;
        int titleY = 100; // near top center
        titleLabel.setBounds(titleX, titleY, titleWidth, titleHeight);
        backgroundPanel.add(titleLabel);

        // ===== Login Panel =====
        JPanel loginPanel = new JPanel(null);
        loginPanel.setBackground(new Color(255, 255, 255, 220));
        loginPanel.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 3));

        int panelWidth = 450;
        int panelHeight = 320;
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - panelWidth) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - panelHeight) / 2;
        loginPanel.setBounds(x, y, panelWidth, panelHeight);

        // User type
        JLabel userTypeLabel = new JLabel("Login As:");
        userTypeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userTypeLabel.setBounds(50, 40, 120, 30);
        loginPanel.add(userTypeLabel);

        userTypeCombo = new JComboBox<>(new String[]{"Voter", "Admin"});
        userTypeCombo.setBounds(190, 40, 200, 30);
        loginPanel.add(userTypeCombo);

        // User ID
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userIdLabel.setBounds(50, 90, 120, 30);
        loginPanel.add(userIdLabel);

        userIdField = new JTextField();
        userIdField.setBounds(190, 90, 200, 30);
        loginPanel.add(userIdField);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passwordLabel.setBounds(50, 140, 120, 30);
        loginPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(190, 140, 200, 30);
        loginPanel.add(passwordField);

        // Buttons
        loginButton = new JButton("LOGIN");
        loginButton.setBounds(90, 210, 120, 40);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());
        loginPanel.add(loginButton);

        registerButton = new JButton("REGISTER");
        registerButton.setBounds(240, 210, 120, 40);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(52, 152, 219));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> handleRegister());
        loginPanel.add(registerButton);

        backgroundPanel.add(loginPanel);
    }

    private void handleLogin() {
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        String userType = (String) userTypeCombo.getSelectedItem();

        if (userId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both User ID and Password!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userType.equals("Voter")) {
            Voter voter = authService.authenticateVoter(userId, password);
            if (voter != null) {
                JOptionPane.showMessageDialog(this, "Welcome, " + voter.getFullName());
                new VoterDashboardFrame(voter).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Admin admin = authService.authenticateAdmin(userId, password);
            if (admin != null) {
                JOptionPane.showMessageDialog(this, "Welcome, " + admin.getFullName());
                new AdminDashboardFrame(admin).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleRegister() {
        String userType = (String) userTypeCombo.getSelectedItem();
        if (userType.equals("Voter")) {
            new VoterRegistrationFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Admin registration restricted.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
