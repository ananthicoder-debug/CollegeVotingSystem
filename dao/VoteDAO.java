package dao;
import models.Vote;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Vote operations
 */
public class VoteDAO {

    /**
     * Cast a vote
     * @param vote Vote object
     * @return true if vote cast successful, false otherwise
     */
    public boolean castVote(Vote vote) {
        String sql = "INSERT INTO votes (voter_id, candidate_id, election_id) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, vote.getVoterId());
            pstmt.setInt(2, vote.getCandidateId());
            pstmt.setInt(3, vote.getElectionId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    vote.setVoteId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error casting vote: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Check if voter has already voted in an election
     * @param voterId Voter ID
     * @param electionId Election ID
     * @return true if voter has voted, false otherwise
     */
    public boolean hasVoted(String voterId, int electionId) {
        String sql = "SELECT COUNT(*) FROM votes WHERE voter_id = ? AND election_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, voterId);
            pstmt.setInt(2, electionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking voting status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Get all votes for an election
     * @param electionId Election ID
     * @return List of votes
     */
    public List<Vote> getVotesByElection(int electionId) {
        List<Vote> votes = new ArrayList<>();
        String sql = "SELECT * FROM votes WHERE election_id = ? ORDER BY voted_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                votes.add(extractVoteFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting votes by election: " + e.getMessage());
            e.printStackTrace();
        }
        
        return votes;
    }

    /**
     * Get vote count for a candidate
     * @param candidateId Candidate ID
     * @return Vote count
     */
    public int getVoteCountForCandidate(int candidateId) {
        String sql = "SELECT COUNT(*) FROM votes WHERE candidate_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, candidateId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting vote count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    /**
     * Get total votes cast in an election
     * @param electionId Election ID
     * @return Total vote count
     */
    public int getTotalVotes(int electionId) {
        String sql = "SELECT COUNT(*) FROM votes WHERE election_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total votes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    /**
     * Get all votes
     * @return List of all votes
     */
    public List<Vote> getAllVotes() {
        List<Vote> votes = new ArrayList<>();
        String sql = "SELECT * FROM votes ORDER BY voted_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                votes.add(extractVoteFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all votes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return votes;
    }

    /**
     * Delete all votes for an election
     * @param electionId Election ID
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteVotesByElection(int electionId) {
        String sql = "DELETE FROM votes WHERE election_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, electionId);
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error deleting votes: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extract Vote object from ResultSet
     * @param rs ResultSet
     * @return Vote object
     * @throws SQLException if extraction fails
     */
    private Vote extractVoteFromResultSet(ResultSet rs) throws SQLException {
        Vote vote = new Vote();
        vote.setVoteId(rs.getInt("vote_id"));
        vote.setVoterId(rs.getString("voter_id"));
        vote.setCandidateId(rs.getInt("candidate_id"));
        vote.setElectionId(rs.getInt("election_id"));
        vote.setVotedAt(rs.getTimestamp("voted_at"));
        return vote;
    }
}
