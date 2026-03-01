package gui;

import models.Admin;
import utils.DatabaseConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;

/**
 * Fully Responsive Pending Candidate Approval Frame (Full expand + short form)
 */
public class ApproveCandidatesFrame extends JFrame {

    private Admin admin;
    private JTable table;
    private DefaultTableModel model;
    private JLabel photoPreviewLabel;
    private JButton viewPdfButton;

    private static final String PHOTO_DIR = "candidate_photos";
    private static final String DESC_DIR = "candidate_desc";

    public ApproveCandidatesFrame(Admin admin) {
        this.admin = admin;
        initUI();
        loadPendingCandidates();
    }

    private void initUI() {

        // FULL EXPAND MODE DETECTION
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (screen.width > 1400) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);  // Full-screen mode
        } else {
            setSize(1100, 650); // Short window mode
        }

        setTitle("Approve Candidates");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // ------------------ HEADER ------------------
        JLabel title = new JLabel("PENDING CANDIDATE APPROVALS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ------------------ TABLE MODEL ------------------
        String[] columns = {
                "Candidate ID", "Roll No", "Full Name",
                "Department", "Symbol", "Photo Path", "Description Path"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(this::showPreview);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ------------------ PREVIEW PANEL ------------------
        JPanel previewPanel = new JPanel(new GridBagLayout());
        previewPanel.setBackground(new Color(245, 245, 245));
        previewPanel.setPreferredSize(new Dimension(330, 0));
        previewPanel.setBorder(BorderFactory.createTitledBorder("Candidate Preview"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel photoLabel = new JLabel("Photo Preview:");
        photoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        addItem(previewPanel, photoLabel, gbc, 0, 0);

        photoPreviewLabel = new JLabel();
        photoPreviewLabel.setPreferredSize(new Dimension(260, 260));
        photoPreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        addItem(previewPanel, photoPreviewLabel, gbc, 0, 1);

        viewPdfButton = new JButton("Open Description PDF");
        viewPdfButton.setFont(new Font("Arial", Font.BOLD, 13));
        viewPdfButton.addActionListener(e -> openPdf());
        addItem(previewPanel, viewPdfButton, gbc, 0, 2);

        add(previewPanel, BorderLayout.EAST);

        // ------------------ FOOTER BUTTONS ------------------
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 15));
        btnPanel.setBackground(new Color(236, 240, 241));

        JButton approveBtn = createButton("Approve", new Color(46, 204, 113));
        approveBtn.addActionListener(e -> approveCandidate());

        JButton rejectBtn = createButton("Reject", new Color(231, 76, 60));
        rejectBtn.addActionListener(e -> rejectCandidate());

        JButton refreshBtn = createButton("Refresh", new Color(52, 152, 219));
        refreshBtn.addActionListener(e -> loadPendingCandidates());

        JButton closeBtn = createButton("Close", new Color(149, 165, 166));
        closeBtn.addActionListener(e -> dispose());

        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(closeBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addItem(JPanel panel, Component comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(comp, gbc);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFocusPainted(false);
        return btn;
    }

    private void loadPendingCandidates() {
        model.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT candidate_id, rollno, name, dept, symbol_filename, photo_path, description_path " +
                             "FROM candidates WHERE is_approved = FALSE");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("candidate_id"),
                        rs.getString("rollno"),
                        rs.getString("name"),
                        rs.getString("dept"),
                        rs.getString("symbol_filename"),
                        rs.getString("photo_path"),
                        rs.getString("description_path")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No pending candidates found!",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading candidates: " + e.getMessage());
        }
    }

    private void showPreview(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        int row = table.getSelectedRow();
        if (row == -1) return;

        String photoPath = (String) model.getValueAt(row, 5);

        try {
            if (photoPath != null && new File(photoPath).exists()) {
                BufferedImage img = ImageIO.read(new File(photoPath));
                photoPreviewLabel.setIcon(
                        new ImageIcon(img.getScaledInstance(260, 260, Image.SCALE_SMOOTH))
                );
            } else {
                photoPreviewLabel.setIcon(null);
            }
        } catch (Exception ex) {
            photoPreviewLabel.setIcon(null);
        }
    }

    private void openPdf() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a candidate first!");
            return;
        }

        String pdfPath = (String) model.getValueAt(row, 6);
        if (pdfPath == null) {
            JOptionPane.showMessageDialog(this, "No PDF available!");
            return;
        }

        try {
            Desktop.getDesktop().open(new File(pdfPath));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to open PDF!");
        }
    }

    private void approveCandidate() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a candidate!");
            return;
        }

        int candidateId = (int) model.getValueAt(row, 0);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE candidates SET is_approved = TRUE WHERE candidate_id = ?")) {

            ps.setInt(1, candidateId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Candidate approved!");
            loadPendingCandidates();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error approving candidate: " + e.getMessage());
        }
    }

    private void rejectCandidate() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a candidate!");
            return;
        }

        int candidateId = (int) model.getValueAt(row, 0);

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Reject this candidate?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM candidates WHERE candidate_id = ?")) {

            ps.setInt(1, candidateId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Candidate rejected!");
            loadPendingCandidates();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error rejecting candidate: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new ApproveCandidatesFrame(null).setVisible(true)
        );
    }
}
