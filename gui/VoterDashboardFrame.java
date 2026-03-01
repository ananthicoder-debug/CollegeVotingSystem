package gui;

import models.Voter;
import services.VotingService;
import dao.CandidateDAO;

import javax.swing.*;
import java.awt.*;

/**
 * Fully responsive + null-safe Voter Dashboard Frame (Fixed version)
 */
public class VoterDashboardFrame extends JFrame {

    private Voter voter;
    private VotingService votingService;
    private CandidateDAO candidateDAO;

    public VoterDashboardFrame(Voter voter) {
        this.voter = voter; // may be null — now handled safely
        this.votingService = new VotingService();
        this.candidateDAO = new CandidateDAO();
        initUI();
    }

    private void initUI() {

        // Auto fullscreen on large displays
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (screen.width > 1400) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setSize(900, 600);
        }

        setTitle("Voter Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ------------------------------------------------ SAFE VALUES
        String voterName = (voter != null && voter.getFullName() != null)
                ? voter.getFullName()
                : "Voter";

        String voterIdStr = (voter != null && voter.getVoterId() != null)
                ? voter.getVoterId()
                : "Unknown";

        boolean hasVoted = (voter != null && voter.getVoterId() != null)
                ? votingService.hasVoted(voter.getVoterId())
                : false;

        boolean electionActive = votingService.isElectionActive();

        // ------------------------------------------------ HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 152, 219));
        header.setPreferredSize(new Dimension(200, 90));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        JLabel welcome = new JLabel("Welcome, " + voterName, SwingConstants.LEFT);
        welcome.setFont(new Font("Arial", Font.BOLD, 26));
        welcome.setForeground(Color.black);
        welcome.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 0));

        JLabel voterId = new JLabel("Voter ID: " + voterIdStr, SwingConstants.LEFT);
        voterId.setFont(new Font("Arial", Font.PLAIN, 16));
        voterId.setForeground(Color.black);
        voterId.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 0));

        titlePanel.add(welcome);
        titlePanel.add(voterId);

        header.add(titlePanel, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ------------------------------------------------ STATUS PANEL
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 25, 15, 25),
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2)
        ));

        JLabel statusTitle = new JLabel("Voting Status:");
        statusTitle.setFont(new Font("Arial", Font.BOLD, 18));
        statusTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String statusText;
        Color statusColor;

        if (!electionActive) {
            statusText = "No Active Election";
            statusColor = new Color(231, 76, 60);
        } else if (hasVoted) {
            statusText = "You have already voted";
            statusColor = new Color(46, 204, 113);
        } else {
            statusText = "You have not voted yet";
            statusColor = new Color(241, 196, 15);
        }

        JLabel statusBody = new JLabel(statusText);
        statusBody.setFont(new Font("Arial", Font.BOLD, 20));
        statusBody.setForeground(statusColor);

        JPanel innerStatus = new JPanel(new GridLayout(2, 1));
        innerStatus.setOpaque(false);
        innerStatus.add(statusTitle);
        innerStatus.add(statusBody);

        statusPanel.add(innerStatus, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.CENTER);

        // ------------------------------------------------ BUTTON PANEL
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 40, 40));

        // View Candidates
        JButton viewCandidatesBtn = createButton("View Candidates", new Color(52, 152, 219));
        viewCandidatesBtn.addActionListener(e -> new ViewCandidatesFrame().setVisible(true));
        buttonPanel.add(viewCandidatesBtn);

        // Cast Vote
        JButton castVoteBtn = createButton("Cast Vote", new Color(46, 204, 113));
        castVoteBtn.setEnabled(electionActive && !hasVoted && voter != null);
        castVoteBtn.addActionListener(e -> {
            new VotingFrame(voter).setVisible(true);
            dispose();
        });
        buttonPanel.add(castVoteBtn);

        // View Results
        JButton viewResultsBtn = createButton("View Results", new Color(155, 89, 182));
        viewResultsBtn.addActionListener(e -> new ResultsFrame().setVisible(true));
        buttonPanel.add(viewResultsBtn);

        // Logout
        JButton logoutBtn = createButton("Logout", new Color(231, 76, 60));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBackground(color);
        btn.setForeground(Color.black);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 70));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new VoterDashboardFrame(null).setVisible(true)  // now safe!
        );
    }
}


