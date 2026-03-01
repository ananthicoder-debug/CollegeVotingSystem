package gui;

import models.Voter;
import models.Candidate;
import services.VotingService;
import dao.CandidateDAO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

/**
 * Modern EVM-style Voting Frame with large symbol buttons
 */
public class VotingFrame extends JFrame {

    private Voter voter;
    private VotingService votingService;
    private CandidateDAO candidateDAO;

    private JPanel gridPanel;
    private int selectedCandidateId = -1;

    public VotingFrame(Voter voter) {
        this.voter = voter;
        this.votingService = new VotingService();
        this.candidateDAO = new CandidateDAO();
        initComponents();
    }

    private void initComponents() {

        setTitle("Cast Your Vote");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // FULL SCREEN
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        // HEADER -------------------------------------------------------
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 25));
        header.setBackground(new Color(52, 152, 219));
        header.setPreferredSize(new Dimension(200, 90));

        JLabel title = new JLabel("CAST YOUR VOTE (Select a Symbol)");
        title.setForeground(Color.BLACK);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        header.add(title);

        mainPanel.add(header, BorderLayout.NORTH);

        // GRID ----------------------------------------------------------
        gridPanel = new JPanel();
        gridPanel.setBackground(new Color(236, 240, 241));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        gridPanel.setLayout(new GridLayout(0, 2, 40, 40)); // 2 per row

        List<Candidate> candidates = candidateDAO.getApprovedCandidates();

        if (candidates.isEmpty()) {
            JLabel msg = new JLabel("No Candidates Available for Voting");
            msg.setFont(new Font("Arial", Font.BOLD, 26));
            msg.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(msg, BorderLayout.CENTER);
        } else {
            for (Candidate c : candidates) {
                gridPanel.add(createCandidateCard(c));
            }

            JScrollPane scroll = new JScrollPane(gridPanel);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scroll.getVerticalScrollBar().setUnitIncrement(20);
            mainPanel.add(scroll, BorderLayout.CENTER);
        }

        // FOOTER -------------------------------------------------------
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        footer.setPreferredSize(new Dimension(200, 100));
        footer.setBackground(new Color(236, 240, 241));

        JButton submit = new JButton("SUBMIT VOTE");
        submit.setFont(new Font("Arial", Font.BOLD, 20));
        submit.setBackground(new Color(46, 204, 113));
        submit.setForeground(Color.BLACK);
        submit.setPreferredSize(new Dimension(220, 55));
        submit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submit.addActionListener(e -> handleVote());

        JButton cancel = new JButton("CANCEL");
        cancel.setFont(new Font("Arial", Font.BOLD, 20));
        cancel.setBackground(new Color(231, 76, 60));
        cancel.setForeground(Color.BLACK);
        cancel.setPreferredSize(new Dimension(220, 55));
        cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancel.addActionListener(e -> {
            new VoterDashboardFrame(voter).setVisible(true);
            dispose();
        });

        footer.add(submit);
        footer.add(cancel);

        mainPanel.add(footer, BorderLayout.SOUTH);
        add(mainPanel);
    }

    // CARD -------------------------------------------------------------
    private JPanel createCandidateCard(Candidate c) {

        JPanel card = new JPanel(null);
        card.setPreferredSize(new Dimension(450, 350));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));

        // PHOTO ---------------------------------------------------------
        JLabel photo = new JLabel();
        photo.setBounds(40, 20, 130, 130);
        photo.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        try {
            Image img = ImageIO.read(new File(c.getPhotoPath()))
                    .getScaledInstance(130, 130, Image.SCALE_SMOOTH);
            photo.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            photo.setText("No Photo");
            photo.setHorizontalAlignment(SwingConstants.CENTER);
        }

        card.add(photo);

        // NAME + DEPT --------------------------------------------------
        JLabel name = new JLabel(c.getFullName());
        name.setFont(new Font("Arial", Font.BOLD, 20));
        name.setBounds(200, 30, 350, 30);
        card.add(name);

        JLabel dept = new JLabel("Department: " + c.getDepartment());
        dept.setFont(new Font("Arial", Font.PLAIN, 16));
        dept.setBounds(200, 70, 300, 30);
        card.add(dept);

        // SYMBOL --------------------------------------------------------
        JLabel symbol = new JLabel();
        symbol.setBounds(160, 150, 200, 160);
        symbol.setHorizontalAlignment(SwingConstants.CENTER);
        symbol.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        try {
            Image simg = ImageIO.read(new File("symbols/" + c.getSymbolFilename()))
                    .getScaledInstance(160, 160, Image.SCALE_SMOOTH);
            symbol.setIcon(new ImageIcon(simg));
        } catch (Exception e) {
            symbol.setText("No Symbol");
        }

        card.add(symbol);

        // SELECT BUTTON ------------------------------------------------
        JButton select = new JButton("SELECT");
        select.setFont(new Font("Arial", Font.BOLD, 18));
        select.setBackground(new Color(52, 152, 219));
        select.setForeground(Color.BLACK);
        select.setBounds(380, 240, 140, 45);
        select.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        select.addActionListener((ActionEvent e) -> {
            selectedCandidateId = c.getCandidateId();

            // Visual highlight
            for (Component comp : gridPanel.getComponents()) {
                comp.setBackground(Color.WHITE);
            }
            card.setBackground(new Color(210, 245, 210));
        });

        card.add(select);

        return card;
    }

    // HANDLE SUBMISSION -----------------------------------------------
    private void handleVote() {

        if (selectedCandidateId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a candidate!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Candidate c = candidateDAO.getCandidateById(selectedCandidateId);

        int confirm = JOptionPane.showConfirmDialog(this,
                "You selected:\n\n" +
                        "Name: " + c.getFullName() + "\n" +
                        "Department: " + c.getDepartment() +
                        "\n\nConfirm your vote?",
                "Confirm Vote",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            boolean ok = votingService.castVote(voter.getVoterId(), selectedCandidateId);

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Your vote was successfully recorded!\nThank you.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                new VoterDashboardFrame(voter).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vote failed! You may have already voted or election is inactive.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =================================================================
    // MAIN METHOD FOR TESTING
    // =================================================================
    public static void main(String[] args) {

        // Dummy voter for testing (no NPE)
        Voter dummy = new Voter();
        dummy.setVoterId("V1001");
        dummy.setFullName("Test Voter");

        SwingUtilities.invokeLater(() -> new VotingFrame(dummy).setVisible(true));
    }
}
