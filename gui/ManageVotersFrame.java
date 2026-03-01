package gui;

import models.Admin;
import models.Voter;
import services.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Fully Responsive Manage Voters Frame (Full-expand & short form supported)
 */
public class ManageVotersFrame extends JFrame {

    private Admin admin;
    private AdminService adminService;
    private JTable votersTable;
    private DefaultTableModel tableModel;

    public ManageVotersFrame(Admin admin) {
        this.admin = admin;
        this.adminService = new AdminService();

        initUI();
        loadVotersSafe();
    }

    private void initUI() {

        // FULL EXPAND MODE AUTO-DETECT
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (screen.width > 1400) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);   // FULL SCREEN
        } else {
            setSize(900, 600);                         // SHORT FORM MODE
        }

        setTitle("Manage Voters");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        // ---------------- HEADER ----------------
        JLabel titleLabel = new JLabel("MANAGE VOTERS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ---------------- TABLE MODEL ----------------
        String[] columns = {
                "Voter ID", "Roll Number", "Name",
                "Department", "Year", "Approved", "Voted"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        votersTable = new JTable(tableModel);
        votersTable.setRowHeight(28);
        votersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        votersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        votersTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(votersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(150,150,150), 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // -------------- BUTTON PANEL ----------------
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 18));
        btnPanel.setBackground(new Color(236, 240, 241));

        JButton deleteBtn = createButton("Delete Selected", new Color(231, 76, 60));
        deleteBtn.addActionListener(e -> deleteSelectedVoter());

        JButton refreshBtn = createButton("Refresh", new Color(52, 152, 219));
        refreshBtn.addActionListener(e -> loadVotersSafe());

        JButton closeBtn = createButton("Close", new Color(149, 165, 166));
        closeBtn.addActionListener(e -> dispose());

        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(closeBtn);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /** Styled buttons */
    private JButton createButton(String txt, Color bg) {
        JButton btn = new JButton(txt);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(180, 40));
        return btn;
    }

    /** Wrap load with error handling */
    private void loadVotersSafe() {
        try {
            loadVoters();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load voters: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Load ONLY approved voters */
    private void loadVoters() {
        tableModel.setRowCount(0);

        List<Voter> voters = adminService.getApprovedVoters();

        if (voters == null || voters.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No approved voters found!",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Voter v : voters) {
            tableModel.addRow(new Object[]{
                    v.getVoterId(),
                    v.getRollNumber(),
                    v.getFullName(),
                    v.getDepartment(),
                    v.getYearOfStudy(),
                    v.isApproved() ? "YES" : "NO",
                    v.hasVoted() ? "YES" : "NO"
            });
        }
    }

    /** Delete selected voter */
    private void deleteSelectedVoter() {
        int row = votersTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a voter to delete!",
                    "Warning", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String voterId = (String) tableModel.getValueAt(row, 0);
        String voterName = (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete voter '" + voterName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        if (adminService.rejectVoter(voterId)) {
            JOptionPane.showMessageDialog(this, "Voter deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadVotersSafe();
        } else {
            JOptionPane.showMessageDialog(this, "Delete failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageVotersFrame(null).setVisible(true));
    }
}
