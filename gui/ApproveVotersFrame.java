package gui;

import models.Admin;
import utils.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * Fully Responsive Frame for approving pending voters (Full expand + short form)
 */
public class ApproveVotersFrame extends JFrame {

    private Admin admin;
    private JTable votersTable;
    private DefaultTableModel tableModel;

    public ApproveVotersFrame(Admin admin) {
        this.admin = admin;
        initUI();
        loadPendingVoters();
    }

    private void initUI() {

        // AUTO FULL-SCREEN IF LARGE DISPLAY
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (screen.width > 1400) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setSize(900, 600);  // laptop short window
        }

        setTitle("Approve Voters");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // --------------------- HEADER -------------------------
        JLabel title = new JLabel("PENDING VOTER APPROVALS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // --------------------- TABLE ---------------------------
        String[] columns = {
                "Voter ID", "Roll Number", "Full Name",
                "Department", "Year", "Email"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        votersTable = new JTable(tableModel);
        votersTable.setRowHeight(28);
        votersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        votersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));

        JScrollPane scrollPane = new JScrollPane(votersTable);
        add(scrollPane, BorderLayout.CENTER);

        // --------------------- BUTTON BAR ----------------------
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 15));
        btnPanel.setBackground(new Color(236, 240, 241));

        JButton viewBtn = createButton("View Details", new Color(241, 196, 15));
        viewBtn.addActionListener(e -> viewSelectedVoter());

        JButton approveBtn = createButton("Approve", new Color(46, 204, 113));
        approveBtn.addActionListener(e -> approveSelectedVoter());

        JButton rejectBtn = createButton("Reject", new Color(231, 76, 60));
        rejectBtn.addActionListener(e -> rejectSelectedVoter());

        JButton refreshBtn = createButton("Refresh", new Color(52, 152, 219));
        refreshBtn.addActionListener(e -> loadPendingVoters());

        JButton closeBtn = createButton("Close", new Color(127, 140, 141));
        closeBtn.addActionListener(e -> dispose());

        btnPanel.add(viewBtn);
        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(closeBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFocusPainted(false);
        return btn;
    }

    // ----------------------- LOAD DATA -----------------------
    private void loadPendingVoters() {
        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT voter_id, roll_number, full_name, department, year_of_study, email " +
                             "FROM voters WHERE is_approved = FALSE");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("voter_id"),
                        rs.getString("roll_number"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getInt("year_of_study"),
                        rs.getString("email")
                });
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No pending voter approvals!",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading voters: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----------------------- VIEW DETAILS -----------------------
    private void viewSelectedVoter() {
        int row = votersTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a voter to view.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String details =
                "Voter ID: " + tableModel.getValueAt(row, 0) + "\n" +
                "Roll Number: " + tableModel.getValueAt(row, 1) + "\n" +
                "Full Name: " + tableModel.getValueAt(row, 2) + "\n" +
                "Department: " + tableModel.getValueAt(row, 3) + "\n" +
                "Year: " + tableModel.getValueAt(row, 4) + "\n" +
                "Email: " + tableModel.getValueAt(row, 5);

        JOptionPane.showMessageDialog(this, details,
                "Voter Details", JOptionPane.INFORMATION_MESSAGE);
    }

    // ----------------------- APPROVE -----------------------
    private void approveSelectedVoter() {
        int row = votersTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a voter to approve.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String voterId = (String) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 2);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE voters SET is_approved = TRUE WHERE voter_id = ?")) {

            ps.setString(1, voterId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Voter " + name + " approved!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadPendingVoters();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error approving voter: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----------------------- REJECT -----------------------
    private void rejectSelectedVoter() {
        int row = votersTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a voter to reject.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String voterId = (String) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Reject voter " + name + "?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM voters WHERE voter_id = ?")) {

            ps.setString(1, voterId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Voter " + name + " rejected!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadPendingVoters();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error rejecting voter: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new ApproveVotersFrame(null).setVisible(true));
    }
}
