
package gui;

import services.AuthenticationService;
import models.Voter;

import javax.swing.*;
import java.awt.*;

/**
 * Responsive Voter Registration Frame (FULL + HALF Screen Support)
 */
public class VoterRegistrationFrame extends JFrame {

    private JTextField rollNumberField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JComboBox<String> departmentCombo;
    private JComboBox<String> yearCombo;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton cancelButton;

    private AuthenticationService authService;

    public VoterRegistrationFrame() {
        authService = new AuthenticationService();
        initUI();
    }

    private void initUI() {

        // Auto fullscreen for big displays
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (screen.width > 1400) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);  // FULL SCREEN
        } else {
            setSize(700, 800);                         // HALF / NORMAL WINDOW
        }

        setTitle("Voter Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(236, 240, 241));
        GridBagConstraints gbc = new GridBagConstraints();

        // Helper to add components
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // HEADER
        JLabel title = new JLabel("VOTER REGISTRATION", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        addItem(panel, title, gbc, 0, 0, 2);

        // ROLL NUMBER
        addItem(panel, new JLabel("Roll Number:"), gbc, 0, 1, 1);
        rollNumberField = new JTextField();
        addItem(panel, rollNumberField, gbc, 1, 1, 1);

        // FULL NAME
        addItem(panel, new JLabel("Full Name:"), gbc, 0, 2, 1);
        fullNameField = new JTextField();
        addItem(panel, fullNameField, gbc, 1, 2, 1);

        // EMAIL
        addItem(panel, new JLabel("Email:"), gbc, 0, 3, 1);
        emailField = new JTextField();
        addItem(panel, emailField, gbc, 1, 3, 1);

        // DEPARTMENT
        addItem(panel, new JLabel("Department:"), gbc, 0, 4, 1);
        String[] departments = {
                "Computer Science", "Electronics", "Mechanical", "Civil",
                "Electrical","Artificial Intelligence","BioMedical","Bio-Tech"
        };
        departmentCombo = new JComboBox<>(departments);
        addItem(panel, departmentCombo, gbc, 1, 4, 1);

        // YEAR
        addItem(panel, new JLabel("Year of Study:"), gbc, 0, 5, 1);
        yearCombo = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        addItem(panel, yearCombo, gbc, 1, 5, 1);

        // PASSWORD
        addItem(panel, new JLabel("Password:"), gbc, 0, 6, 1);
        passwordField = new JPasswordField();
        addItem(panel, passwordField, gbc, 1, 6, 1);

        // CONFIRM PASSWORD
        addItem(panel, new JLabel("Confirm Password:"), gbc, 0, 7, 1);
        confirmPasswordField = new JPasswordField();
        addItem(panel, confirmPasswordField, gbc, 1, 7, 1);

        // INFO LABEL
        JLabel info = new JLabel(
                "<html>Your Voter ID will be generated automatically: <b>V + Roll Number</b></html>"
        );
        addItem(panel, info, gbc, 0, 8, 2);

        // REGISTER BUTTON
        registerButton = new JButton("REGISTER");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.BLACK);
        registerButton.addActionListener(e -> handleRegistration());
        addItem(panel, registerButton, gbc, 0, 9, 2);

        // CANCEL BUTTON
        cancelButton = new JButton("CANCEL");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 16));
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.addActionListener(e -> dispose());
        addItem(panel, cancelButton, gbc, 0, 10, 2);

        add(panel);
    }

    private void addItem(JPanel panel, Component comp, GridBagConstraints gbc,
                         int x, int y, int width) {

        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        panel.add(comp, gbc);
    }

    private void handleRegistration() {
        String rollNumber = rollNumberField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String department = (String) departmentCombo.getSelectedItem();
        String year = (String) yearCombo.getSelectedItem();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (rollNumber.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String voterId = "V" + rollNumber;

        Voter voter = new Voter();
        voter.setVoterId(voterId);
        voter.setRollNumber(rollNumber);
        voter.setFullName(fullName);
        voter.setEmail(email);
        voter.setDepartment(department);
        voter.setYearOfStudy(year);
        voter.setPassword(password);

        boolean success = authService.registerVoter(voter);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Registration Successful!\nYour Voter ID: " + voterId +
                            "\nPlease wait for admin approval.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Registration Failed! Roll number may already be registered.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
