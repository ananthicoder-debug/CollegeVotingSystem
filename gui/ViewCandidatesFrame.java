package gui;

import models.Candidate;
import dao.CandidateDAO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

/**
 * Fully responsive View Candidates window (Full screen + small screen support)
 */
public class ViewCandidatesFrame extends JFrame {

    private CandidateDAO candidateDAO;

    public ViewCandidatesFrame() {
        this.candidateDAO = new CandidateDAO();
        initUI();
    }

    private void initUI() {

        // Auto fullscreen on larger displays
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (screen.width > 1400) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setSize(1100, 700);
        }

        setTitle("Approved Candidates");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // ---------------- HEADER ----------------
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(100, 85));
        header.setBackground(new Color(52, 152, 219));

        JLabel title = new JLabel("APPROVED CANDIDATES", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.black);

        JButton backBtn = new JButton("⟵ Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setBackground(new Color(231, 76, 60));
        backBtn.setForeground(Color.white);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> dispose());

        header.add(title, BorderLayout.CENTER);
        header.add(backBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ---------------- LIST PANEL ----------------
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 25));
        cardsContainer.setBackground(new Color(236, 240, 241));

        List<Candidate> candidates = candidateDAO.getApprovedCandidates();

        if (candidates.isEmpty()) {
            JLabel msg = new JLabel("No candidates registered yet.");
            msg.setHorizontalAlignment(SwingConstants.CENTER);
            msg.setFont(new Font("Arial", Font.BOLD, 24));
            add(msg, BorderLayout.CENTER);
            return;
        }

        for (Candidate c : candidates) {
            JPanel card = createCandidateCard(c);
            cardsContainer.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createCandidateCard(Candidate c) {

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(350, 460));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // -------- PHOTO --------
        JLabel photo = new JLabel();
        photo.setPreferredSize(new Dimension(150, 150));
        photo.setAlignmentX(Component.CENTER_ALIGNMENT);
        photo.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        try {
            File f = new File(c.getPhotoPath());
            Image img = ImageIO.read(f).getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            photo.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            photo.setText("No Image");
            photo.setHorizontalAlignment(SwingConstants.CENTER);
        }

        card.add(photo);
        card.add(Box.createVerticalStrut(15));

        // -------- TEXT DETAILS --------
        card.add(makeLabel("Name: " + c.getFullName()));
        card.add(makeLabel("Department: " + c.getDepartment()));
        card.add(makeLabel("Roll No: " + c.getRollNumber()));

        card.add(Box.createVerticalStrut(20));

        // -------- SYMBOL --------
        JLabel sym = new JLabel();
        sym.setPreferredSize(new Dimension(80, 80));
        sym.setAlignmentX(Component.CENTER_ALIGNMENT);
        sym.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        try {
            File f = new File("symbols/" + c.getSymbolFilename());
            Image img = ImageIO.read(f).getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            sym.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            sym.setText("N/A");
            sym.setHorizontalAlignment(SwingConstants.CENTER);
        }

        card.add(sym);
        card.add(Box.createVerticalStrut(15));

        // -------- BUTTON --------
        JButton descBtn = new JButton("View Description");
        descBtn.setFont(new Font("Arial", Font.BOLD, 13));
        descBtn.setBackground(new Color(52, 152, 219));
        descBtn.setForeground(Color.white);
        descBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        descBtn.setFocusPainted(false);
        descBtn.setPreferredSize(new Dimension(200, 40));

        descBtn.addActionListener((ActionEvent e) -> {
            try {
                File pdf = new File(c.getDescriptionPath());
                if (pdf.exists()) {
                    Desktop.getDesktop().open(pdf);
                } else {
                    JOptionPane.showMessageDialog(this, "PDF not found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Unable to open file.");
            }
        });

        card.add(descBtn);

        return card;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 16));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewCandidatesFrame().setVisible(true));
    }
}
