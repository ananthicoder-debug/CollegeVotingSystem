package gui;

import models.Candidate;
import services.ResultService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Frame for viewing election results
 */
public class ResultsFrame extends JFrame {

    private ResultService resultService;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel totalVotesLabel;

    public ResultsFrame() {
        this.resultService = new ResultService();
        initComponents();
        loadResults();
    }

    private void initComponents() {

        setTitle("Election Results");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        // ========== HEADER ==========
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(155, 89, 182));
        headerPanel.setPreferredSize(new Dimension(900, 100));

        JLabel titleLabel = new JLabel("ELECTION RESULTS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        totalVotesLabel = new JLabel("Total Votes Cast: 0", SwingConstants.CENTER);
        totalVotesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalVotesLabel.setForeground(Color.WHITE);
        totalVotesLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.add(totalVotesLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ========== TABLE ==========
        String[] columnNames = {
                "Rank", "Candidate Name", "Position", "Department",
                "Symbol", "Votes", "Percentage"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        resultsTable.setRowHeight(30);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ========== BUTTONS ==========
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonsPanel.setBackground(new Color(236, 240, 241));

        JButton refreshBtn = new JButton("Refresh Results");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.BLACK);
        refreshBtn.addActionListener(e -> loadResults());
        buttonsPanel.add(refreshBtn);

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.setBackground(new Color(149, 165, 166));
        closeBtn.setForeground(Color.BLACK);
        closeBtn.addActionListener(e -> dispose());
        buttonsPanel.add(closeBtn);

        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void loadResults() {

        tableModel.setRowCount(0);

        if (!resultService.areResultsAvailable()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No votes have been cast yet!",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        List<Candidate> candidates = resultService.getDetailedResults();
        candidates.sort((c1, c2) -> Integer.compare(c2.getVoteCount(), c1.getVoteCount()));

        int totalVotes = resultService.getTotalVotesCast();
        totalVotesLabel.setText("Total Votes Cast: " + totalVotes);

        int rank = 1;

        for (Candidate c : candidates) {

            double percentage = resultService.getVotePercentage(c.getCandidateId());

            Object[] row = {
                    rank++,
                    c.getFullName(),
                    c.getPosition(),
                    c.getDepartment(),
                    c.getSymbolFilename(),
                    c.getVoteCount(),
                    String.format("%.2f%%", percentage)
            };

            tableModel.addRow(row);
        }

        applyTop3Highlight();
    }

    private void applyTop3Highlight() {

        resultsTable.setDefaultRenderer(Object.class,
                new javax.swing.table.DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int col
                    ) {
                        Component comp = super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, col
                        );

                        if (!isSelected) {
                            if (row == 0) {
                                comp.setBackground(new Color(255, 215, 0));  // Gold
                            } else if (row == 1) {
                                comp.setBackground(new Color(192, 192, 192)); // Silver
                            } else if (row == 2) {
                                comp.setBackground(new Color(205, 127, 50)); // Bronze
                            } else {
                                comp.setBackground(Color.WHITE);
                            }
                            comp.setForeground(Color.BLACK);
                        }

                        return comp;
                    }
                }
        );
    }

    // ========= MAIN METHOD =========
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ResultsFrame().setVisible(true);
        });
    }
}
