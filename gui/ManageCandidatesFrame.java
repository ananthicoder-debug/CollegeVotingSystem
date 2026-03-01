package gui;

import utils.DatabaseConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 * Fully Responsive Candidate Management Frame — Full Expand + Short Form Support
 */
public class ManageCandidatesFrame extends JFrame {

    private JTable candidatesTable;
    private DefaultTableModel tableModel;
    private JButton deleteButton, refreshButton, viewPhotoButton, viewSymbolButton, viewDescButton;

    private static final String SYMBOLS_DIR = "symbols";
    private static final String PHOTO_DIR = "candidate_photos";
    private static final String DESC_DIR = "candidate_desc";

    public ManageCandidatesFrame() {

        // Auto expand fullscreen on big screens
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (screen.width > 1400) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setSize(950, 620);  // Short Form mode
        }

        setTitle("Manage Candidates");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadCandidates();
    }

    private void initUI() {

        setLayout(new BorderLayout());

        // HEADER
        JLabel title = new JLabel("CANDIDATE MANAGEMENT", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // TABLE MODEL
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Roll No", "Name", "Department", "Symbol", "Approved"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        candidatesTable = new JTable(tableModel);
        candidatesTable.setRowHeight(28);
        candidatesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        candidatesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        candidatesTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(candidatesTable);
        add(scrollPane, BorderLayout.CENTER);

        // FOOTER BUTTON PANEL
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 18));

        deleteButton = createButton("Delete", new Color(231, 76, 60));
        deleteButton.addActionListener(e -> deleteCandidate());

        viewSymbolButton = createButton("View Symbol", new Color(52, 152, 219));
        viewSymbolButton.addActionListener(e -> viewSymbol());

        viewPhotoButton = createButton("View Photo", new Color(155, 89, 182));
        viewPhotoButton.addActionListener(e -> viewPhoto());

        viewDescButton = createButton("View Description", new Color(230, 126, 34));
        viewDescButton.addActionListener(e -> viewDescription());

        refreshButton = createButton("Refresh", new Color(149, 165, 166));
        refreshButton.addActionListener(e -> loadCandidates());

        buttonPanel.add(deleteButton);
        buttonPanel.add(viewSymbolButton);
        buttonPanel.add(viewPhotoButton);
        buttonPanel.add(viewDescButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setFocusPainted(false);
        return btn;
    }

    private void loadCandidates() {
        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT candidate_id, rollno, name, dept, symbol_filename, is_approved FROM candidates");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("candidate_id"),
                        rs.getString("rollno"),
                        rs.getString("name"),
                        rs.getString("dept"),
                        rs.getString("symbol_filename"),
                        rs.getBoolean("is_approved") ? "Yes" : "No"
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading candidates: " + e.getMessage());
        }
    }

    private void deleteCandidate() {
        int row = candidatesTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a candidate to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String symbol = (String) tableModel.getValueAt(row, 4);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this candidate?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM candidates WHERE candidate_id=?");
            ps.setInt(1, id);
            ps.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE symbols SET is_taken=FALSE WHERE filename=?");
            ps2.setString(1, symbol);
            ps2.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Candidate deleted!");
            loadCandidates();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting candidate: " + e.getMessage());
        }
    }

    private void viewSymbol() {
        int row = candidatesTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a candidate first!");
            return;
        }

        String symbol = (String) tableModel.getValueAt(row, 4);
        File img = new File(SYMBOLS_DIR, symbol);

        if (!img.exists()) {
            JOptionPane.showMessageDialog(this, "Symbol not found!");
            return;
        }

        showImage(img, "Symbol Preview");
    }

    private void viewPhoto() {
        int row = candidatesTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a candidate first!");
            return;
        }

        String roll = (String) tableModel.getValueAt(row, 1);
        File dir = new File(PHOTO_DIR);

        File[] matches = dir.listFiles((d, name) -> name.startsWith(roll));
        if (matches == null || matches.length == 0) {
            JOptionPane.showMessageDialog(this, "Photo not found!");
            return;
        }

        showImage(matches[0], "Photo Preview");
    }

    private void viewDescription() {
        int row = candidatesTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a candidate first!");
            return;
        }

        String roll = (String) tableModel.getValueAt(row, 1);
        File pdf = new File(DESC_DIR, roll + ".pdf");

        if (!pdf.exists()) {
            JOptionPane.showMessageDialog(this, "Description PDF not found!");
            return;
        }

        try {
            Desktop.getDesktop().open(pdf);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not open PDF!");
        }
    }

    private void showImage(File file, String title) {
        try {
            BufferedImage img = ImageIO.read(file);
            ImageIcon icon = new ImageIcon(img.getScaledInstance(300, 300, Image.SCALE_SMOOTH));

            JLabel label = new JLabel(icon);
            JOptionPane.showMessageDialog(this, label, title, JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading image!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageCandidatesFrame().setVisible(true));
    }
}

