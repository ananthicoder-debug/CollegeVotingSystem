package gui;

import models.Admin;
import services.AdminService;

import javax.swing.*;
import java.awt.*;

/**
 * Admin Dashboard Frame – Fully Fixed Version
 */
public class AdminDashboardFrame extends JFrame {

    private final Admin admin;
    private final AdminService adminService;

    public AdminDashboardFrame(Admin admin) {
        this.admin = admin;
        this.adminService = new AdminService();
        initComponents();
    }

    private void initComponents() {
        setTitle("Admin Dashboard");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(236, 240, 241));

        // Header Panel
        JPanel headerPanel = new JPanel(null);
        headerPanel.setBackground(new Color(231, 76, 60));
        headerPanel.setBounds(0, 0, 900, 80);

        String adminName = (admin != null && admin.getFullName() != null)
                ? admin.getFullName()
                : "Admin";

        JLabel welcomeLabel = new JLabel("Admin Dashboard - " + adminName);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setBounds(30, 25, 600, 30);
        headerPanel.add(welcomeLabel);

        mainPanel.add(headerPanel);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(new Color(236, 240, 241));
        statsPanel.setBounds(50, 100, 800, 100);

        statsPanel.add(createStatPanel("Total Voters", String.valueOf(adminService.getTotalVoters()), new Color(52, 152, 219)));
        statsPanel.add(createStatPanel("Total Candidates", String.valueOf(adminService.getTotalCandidates()), new Color(46, 204, 113)));
        statsPanel.add(createStatPanel("Pending Approvals",
                String.valueOf(adminService.getPendingVoterCount() + adminService.getPendingCandidateCount()),
                new Color(241, 196, 15)));
        statsPanel.add(createStatPanel("Total Votes Cast", String.valueOf(adminService.getTotalVotesCast()), new Color(155, 89, 182)));

        mainPanel.add(statsPanel);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 3, 15, 15));
        buttonsPanel.setBackground(new Color(236, 240, 241));
        buttonsPanel.setBounds(100, 230, 700, 320);

        buttonsPanel.add(createDashboardButton("Manage Voters", new Color(52, 152, 219),
                () -> openFrame("ManageVotersFrame")));

        buttonsPanel.add(createDashboardButton("Manage Candidates", new Color(46, 204, 113),
                () -> openFrame("ManageCandidatesFrame")));

        buttonsPanel.add(createDashboardButton("Manage Elections", new Color(155, 89, 182),
                () -> openFrame("ManageElectionsFrame")));

        buttonsPanel.add(createDashboardButton("Approve Voters", new Color(26, 188, 156),
                () -> openFrame("ApproveVotersFrame")));

        buttonsPanel.add(createDashboardButton("Approve Candidates", new Color(52, 73, 94),
                () -> openFrame("ApproveCandidatesFrame")));

        buttonsPanel.add(createDashboardButton("View Results", new Color(230, 126, 34),
                () -> openFrame("ResultsFrame")));

        buttonsPanel.add(createDashboardButton("Add Candidate", new Color(22, 160, 133),
                () -> openFrame("AddCandidateFrame")));

        buttonsPanel.add(createDashboardButton("Refresh Dashboard", new Color(149, 165, 166),
                this::refreshDashboard));

        buttonsPanel.add(createDashboardButton("Logout", new Color(231, 76, 60),
                this::logout));

        mainPanel.add(buttonsPanel);
        add(mainPanel);
    }

    private JPanel createStatPanel(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createLineBorder(color.darker(), 2));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 30));
        valueLabel.setForeground(Color.BLACK);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createDashboardButton(String text, Color color, Runnable onClick) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> onClick.run());
        return button;
    }

    /**
     * Opens frames safely (final working version)
     */
    private void openFrame(String frameName) {
        try {
            switch (frameName) {

                case "ManageVotersFrame":
                    new ManageVotersFrame(admin).setVisible(true);
                    break;

                case "ManageCandidatesFrame":
                    new ManageCandidatesFrame().setVisible(true);
                    break;

                case "ManageElectionsFrame":
                    new ManageElectionsFrame(admin).setVisible(true);
                    break;

                case "ApproveVotersFrame":
                    new ApproveVotersFrame(admin).setVisible(true);
                    break;

                case "ApproveCandidatesFrame":
                    new ApproveCandidatesFrame(admin).setVisible(true);
                    break;

                case "ResultsFrame":
                    new ResultsFrame().setVisible(true);
                    break;

                case "AddCandidateFrame":
                    new AddCandidateFrame().setVisible(true);
                    break;

                default:
                    JOptionPane.showMessageDialog(this,
                            "Unknown Frame: " + frameName,
                            "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error opening " + frameName + "\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshDashboard() {
        dispose();
        new AdminDashboardFrame(admin).setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboardFrame(null).setVisible(true));
    }
}

