package gui;

import utils.DatabaseConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.sql.*;

public class AddCandidateFrame extends JFrame {

    private JTextField rollNumberField;
    private JTextField fullNameField;
    private JComboBox<String> departmentCombo;
    private JComboBox<String> symbolCombo;
    private JLabel symbolPreviewLabel;
    private JButton uploadPhotoButton, uploadDescButton, addButton, cancelButton;
    private JLabel photoPreviewLabel;
    private File selectedPhotoFile, selectedDescFile;

    private static final String SYMBOLS_DIR = "symbols";
    private static final String PHOTO_DIR = "candidate_photos";
    private static final String DESC_DIR = "candidate_desc";
    private static final long MAX_PHOTO_SIZE = 300 * 1024; // 300KB

    public AddCandidateFrame() {
        setTitle("Add Candidate - Election Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initFolders();
        initUI();
        loadSymbols();
        pack();
        setLocationRelativeTo(null);
        setPreferredSize(new Dimension(820, 920));
    }

    private void initFolders() {
        try {
            Files.createDirectories(Paths.get(SYMBOLS_DIR));
            Files.createDirectories(Paths.get(PHOTO_DIR));
            Files.createDirectories(Paths.get(DESC_DIR));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to create directories: " + e.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        PatternBackgroundPanel background = new PatternBackgroundPanel();
        background.setLayout(new GridBagLayout());
        background.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(700, 820);
            }
        };
        card.setBackground(new Color(255, 255, 255, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(18, 18, 18, 18)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.gridx = 0; 
        c.gridy = 0; 
        c.gridwidth = 2; 
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("REGISTER NEW CANDIDATE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(new Color(34, 40, 49));
        c.insets = new Insets(6, 6, 12, 6);
        card.add(title, c);

        // Roll Number
        c.gridwidth = 1;
        c.gridy++;
        c.insets = new Insets(6, 6, 6, 6);
        card.add(new JLabel("Roll Number:"), c);
        rollNumberField = new JTextField();
        rollNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.gridx = 1; 
        card.add(rollNumberField, c);

        // Full Name
        c.gridx = 0; 
        c.gridy++;
        card.add(new JLabel("Full Name:"), c);
        fullNameField = new JTextField();
        fullNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.gridx = 1; 
        card.add(fullNameField, c);

        // Department
        c.gridx = 0; 
        c.gridy++;
        card.add(new JLabel("Department:"), c);
        String[] departments = {
                "Computer Science", "Electronics", "Mechanical",
                "Civil", "Electrical", "AI", "Biomedical", "Biotech"
        };
        departmentCombo = new JComboBox<>(departments);
        departmentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.gridx = 1; 
        card.add(departmentCombo, c);

        // Symbol selection
        c.gridx = 0; 
        c.gridy++;
        card.add(new JLabel("Select Symbol:"), c);
        symbolCombo = new JComboBox<>();
        symbolCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        symbolCombo.addActionListener(e -> showSymbolPreview());
        c.gridx = 1; 
        card.add(symbolCombo, c);

        // Symbol preview
        c.gridx = 1; 
        c.gridy++;
        symbolPreviewLabel = new JLabel();
        symbolPreviewLabel.setPreferredSize(new Dimension(140, 140));
        symbolPreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.add(symbolPreviewLabel, c);

        // Photo upload
        c.gridx = 0; 
        c.gridy++;
        card.add(new JLabel("Candidate Photo:"), c);
        uploadPhotoButton = new JButton("Choose Photo");
        uploadPhotoButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        uploadPhotoButton.addActionListener(e -> choosePhoto());
        c.gridx = 1; 
        card.add(uploadPhotoButton, c);

        // Photo preview
        c.gridx = 1; 
        c.gridy++;
        photoPreviewLabel = new JLabel();
        photoPreviewLabel.setPreferredSize(new Dimension(140, 140));
        photoPreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.add(photoPreviewLabel, c);

        // Description
        c.gridx = 0; 
        c.gridy++;
        card.add(new JLabel("Description (PDF):"), c);
        uploadDescButton = new JButton("Upload PDF");
        uploadDescButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        uploadDescButton.addActionListener(e -> chooseDescription());
        c.gridx = 1; 
        card.add(uploadDescButton, c);

        // Buttons
        c.gridx = 0; 
        c.gridy++;
        c.gridwidth = 2;
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);
        GridBagConstraints b = new GridBagConstraints();
        b.insets = new Insets(6, 8, 6, 8);

        addButton = new JButton("ADD CANDIDATE");
        stylePrimary(addButton);
        addButton.addActionListener(e -> addCandidate());
        b.gridx = 0; 
        btnPanel.add(addButton, b);

        cancelButton = new JButton("CANCEL");
        styleDanger(cancelButton);
        cancelButton.addActionListener(e -> dispose());
        b.gridx = 1; 
        btnPanel.add(cancelButton, b);

        card.add(btnPanel, c);

        // Footer note
        c.gridy++;
        JLabel note = new JLabel("<html><i>Please ensure this candidate is already registered as a voter.</i></html>");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        note.setForeground(new Color(80, 80, 80));
        card.add(note, c);

        background.add(card, new GridBagConstraints());
        setContentPane(background);

        getRootPane().setDefaultButton(addButton);

        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), esc, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void stylePrimary(JButton b) {
        b.setBackground(new Color(46, 204, 113));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleDanger(JButton b) {
        b.setBackground(new Color(231, 76, 60));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void loadSymbols() {
        symbolCombo.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT filename FROM symbols WHERE is_taken = FALSE");
             ResultSet rs = ps.executeQuery()) {

            boolean any = false;
            while (rs.next()) {
                any = true;
                symbolCombo.addItem(rs.getString("filename"));
            }

            if (!any) {
                symbolCombo.addItem("(No symbols available)");
                symbolCombo.setEnabled(false);
            } else {
                symbolCombo.setEnabled(true);
                symbolCombo.setSelectedIndex(-1);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load symbols: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSymbolPreview() {
        String symbol = (String) symbolCombo.getSelectedItem();
        if (symbol == null || symbol.startsWith("(")) {
            symbolPreviewLabel.setIcon(null);
            return;
        }

        File file = new File(SYMBOLS_DIR, symbol);
        if (!file.exists()) {
            symbolPreviewLabel.setIcon(null);
            return;
        }

        try {
            BufferedImage img = ImageIO.read(file);
            symbolPreviewLabel.setIcon(
                    new ImageIcon(img.getScaledInstance(140, 140, Image.SCALE_SMOOTH))
            );
        } catch (IOException e) {
            symbolPreviewLabel.setIcon(null);
        }
    }

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if (file.length() > MAX_PHOTO_SIZE) {
                JOptionPane.showMessageDialog(this, "Photo must be less than 300 KB.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            selectedPhotoFile = file;
            try {
                BufferedImage img = ImageIO.read(file);
                photoPreviewLabel.setIcon(
                        new ImageIcon(img.getScaledInstance(140, 140, Image.SCALE_SMOOTH))
                );
            } catch (IOException e) {
                photoPreviewLabel.setIcon(null);
            }
        }
    }

    private void chooseDescription() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedDescFile = chooser.getSelectedFile();

            if (!selectedDescFile.getName().toLowerCase().endsWith(".pdf")) {
                JOptionPane.showMessageDialog(this, "Please select a valid PDF file.", "Validation", JOptionPane.WARNING_MESSAGE);
                selectedDescFile = null;
            }
        }
    }

    private boolean voterExists(String roll) {
        String sql = "SELECT roll_number FROM voters WHERE roll_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roll);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addCandidate() {
        String roll = rollNumberField.getText().trim();
        String name = fullNameField.getText().trim();
        String dept = (String) departmentCombo.getSelectedItem();
        String symbol = (String) symbolCombo.getSelectedItem();

        if (roll.isEmpty() || name.isEmpty() ||
                symbol == null || selectedPhotoFile == null || selectedDescFile == null ||
                symbolCombo.getItemCount() == 0 || !symbolCombo.isEnabled()) {

            JOptionPane.showMessageDialog(this, "Please fill all fields and select a symbol, photo & PDF.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!voterExists(roll)) {
            JOptionPane.showMessageDialog(this,
                    "This candidate must be registered as a voter first!",
                    "Not Registered",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String photoFileName = roll + getExtension(selectedPhotoFile);
        String descFileName = roll + ".pdf";

        Path photoDest = Paths.get(PHOTO_DIR, photoFileName);
        Path descDest = Paths.get(DESC_DIR, descFileName);

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            String insertSql = "INSERT INTO candidates(rollno, name, dept, symbol_filename, photo_path, description_path) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(insertSql);
                 PreparedStatement psSymbol = conn.prepareStatement("UPDATE symbols SET is_taken = TRUE WHERE filename = ?")) {

                ps.setString(1, roll);
                ps.setString(2, name);
                ps.setString(3, dept);
                ps.setString(4, symbol);
                ps.setString(5, photoDest.toString());
                ps.setString(6, descDest.toString());
                ps.executeUpdate();

                psSymbol.setString(1, symbol);
                psSymbol.executeUpdate();

                Files.copy(selectedPhotoFile.toPath(), photoDest, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(selectedDescFile.toPath(), descDest, StandardCopyOption.REPLACE_EXISTING);

                conn.commit();
                JOptionPane.showMessageDialog(this, "Candidate Registered Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                clearForm();
                loadSymbols();

            } catch (SQLException | IOException ex) {
                conn.rollback();

                if (ex instanceof SQLException && ex.getMessage().toLowerCase().contains("foreign key")) {
                    JOptionPane.showMessageDialog(this,
                            "The given roll number is not registered as a voter. Please register voter first.",
                            "Foreign Key Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error registering candidate: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        rollNumberField.setText("");
        fullNameField.setText("");
        departmentCombo.setSelectedIndex(0);
        if (symbolCombo.getItemCount() > 0) symbolCombo.setSelectedIndex(-1);
        selectedPhotoFile = null;
        selectedDescFile = null;
        photoPreviewLabel.setIcon(null);
        symbolPreviewLabel.setIcon(null);
    }

    private String getExtension(File f) {
        String name = f.getName();
        int idx = name.lastLastIndexOf('.');
        return idx >= 0 ? name.substring(idx) : "";
    }

    // -------- MAIN METHOD (FIXED) --------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddCandidateFrame f = new AddCandidateFrame();
            f.setVisible(true);
        });
    }

    // -------- CUSTOM BACKGROUND PANEL --------
    private static class PatternBackgroundPanel extends JPanel {

        private static final int SPACING = 20;
        private static final float LINE_ALPHA = 0.08f;
        private final Color base = new Color(245, 247, 250);
        private final Color lineColor = new Color(40, 50, 60);

        PatternBackgroundPanel() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int w = getWidth();
            int h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setColor(base);
            g2.fillRect(0, 0, w, h);

            g2.setStroke(new BasicStroke(1f));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, LINE_ALPHA));
            g2.setColor(lineColor);

            for (int x = -h; x < w; x += SPACING) {
                g2.draw(new Line2D.Float(x, 0, x + h, h));
            }

            g2.dispose();
        }
    }
}
