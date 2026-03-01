package gui;
import models.Admin;
import models.Election;
import services.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Frame for managing elections
 */
public class ManageElectionsFrame extends JFrame {
    private Admin admin;
    private AdminService adminService;
    private JTable electionsTable;
    private DefaultTableModel tableModel;

    public ManageElectionsFrame(Admin admin) {
        this.admin = admin;
        this.adminService = new AdminService();
        initComponents();
        loadElections();
    }

    private void initComponents() {
        setTitle("Manage Elections");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        // Header
        JLabel titleLabel = new JLabel("MANAGE ELECTIONS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Election Name", "Start Date", "End Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        electionsTable = new JTable(tableModel);
        electionsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        electionsTable.setRowHeight(25);
        electionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(electionsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonsPanel.setBackground(new Color(236, 240, 241));

        JButton createBtn = new JButton("Create Election");
        createBtn.setFont(new Font("Arial", Font.BOLD, 14));
        createBtn.setBackground(new Color(46, 204, 113));
        createBtn.setForeground(Color.BLACK);
        createBtn.setFocusPainted(false);
        createBtn.addActionListener(e -> createElection());
        buttonsPanel.add(createBtn);

        JButton startBtn = new JButton("Start Selected");
        startBtn.setFont(new Font("Arial", Font.BOLD, 14));
        startBtn.setBackground(new Color(52, 152, 219));
        startBtn.setForeground(Color.BLACK);
        startBtn.setFocusPainted(false);
        startBtn.addActionListener(e -> startSelectedElection());
        buttonsPanel.add(startBtn);

        JButton stopBtn = new JButton("Stop Selected");
        stopBtn.setFont(new Font("Arial", Font.BOLD, 14));
        stopBtn.setBackground(new Color(230, 126, 34));
        stopBtn.setForeground(Color.BLACK);
        stopBtn.setFocusPainted(false);
        stopBtn.addActionListener(e -> stopSelectedElection());
        buttonsPanel.add(stopBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(155, 89, 182));
        refreshBtn.setForeground(Color.BLACK);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadElections());
        buttonsPanel.add(refreshBtn);

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.setBackground(new Color(149, 165, 166));
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());
        buttonsPanel.add(closeBtn);

        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadElections() {
        tableModel.setRowCount(0);
        List<Election> elections = adminService.getAllElections();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Election election : elections) {
            Object[] row = {
                election.getElectionId(),
                election.getElectionName(),
                sdf.format(election.getStartDate()),
                sdf.format(election.getEndDate()),
                election.isActive() ? "Active" : "Inactive"
            };
            tableModel.addRow(row);
        }
    }

    private void createElection() {
        JTextField nameField = new JTextField();
        JTextField startDateField = new JTextField("2025-01-01 09:00");
        JTextField endDateField = new JTextField("2025-01-01 17:00");

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Election Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Start Date (yyyy-MM-dd HH:mm):"));
        panel.add(startDateField);
        panel.add(new JLabel("End Date (yyyy-MM-dd HH:mm):"));
        panel.add(endDateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create New Election", 
                                                   JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Timestamp startDate = new Timestamp(sdf.parse(startDateField.getText()).getTime());
                Timestamp endDate = new Timestamp(sdf.parse(endDateField.getText()).getTime());

                Election election = new Election(name, startDate, endDate, admin.getAdminId());
                boolean success = adminService.createElection(election);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Election created successfully!", 
                                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadElections();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create election!", 
                                                "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid date format!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void startSelectedElection() {
        int selectedRow = electionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an election to start!", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int electionId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String electionName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Start election: " + electionName + "?\nThis will deactivate any other active elections.", 
            "Confirm Start", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = adminService.startElection(electionId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Election started successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                loadElections();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to start election!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void stopSelectedElection() {
        int selectedRow = electionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an election to stop!", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int electionId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String electionName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Stop election: " + electionName + "?", 
            "Confirm Stop", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = adminService.stopElection(electionId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Election stopped successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                loadElections();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to stop election!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

