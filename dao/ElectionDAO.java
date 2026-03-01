package dao;
import models.Election;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Election operations
 */
public class ElectionDAO {

    /**
     * Create a new election
     * @param election Election object
     * @return true if creation successful, false otherwise
     */
    public boolean createElection(Election election) {
        String sql = "INSERT INTO elections (election_name, start_date, end_date, is_active, created_by) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, election.getElectionName());
            pstmt.setTimestamp(2, election.getStartDate());
            pstmt.setTimestamp(3, election.getEndDate());
            pstmt.setBoolean(4, election.isActive());
            pstmt.setInt(5, election.getCreatedBy());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    election.setElectionId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating election: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Get election by ID
     * @param electionId Election ID
     * @return Election object or null if not found
     */
    public Election getElectionById(int electionId) {
        String sql = "SELECT * FROM elections WHERE election_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractElectionFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting election: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Get all elections
     * @return List of all elections
     */
    public List<Election> getAllElections() {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT * FROM elections ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                elections.add(extractElectionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all elections: " + e.getMessage());
            e.printStackTrace();
        }
        
        return elections;
    }

    /**
     * Get active election
     * @return Active Election object or null if none active
     */
    public Election getActiveElection() {
        String sql = "SELECT * FROM elections WHERE is_active = TRUE LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return extractElectionFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting active election: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Start an election (set as active)
     * @param electionId Election ID
     * @return true if successful, false otherwise
     */
    public boolean startElection(int electionId) {
        // First, deactivate all other elections
        String deactivateSql = "UPDATE elections SET is_active = FALSE";
        String activateSql = "UPDATE elections SET is_active = TRUE WHERE election_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try (Statement stmt = conn.createStatement();
                 PreparedStatement pstmt = conn.prepareStatement(activateSql)) {
                
                stmt.executeUpdate(deactivateSql);
                pstmt.setInt(1, electionId);
                int rowsAffected = pstmt.executeUpdate();
                
                conn.commit();
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            System.err.println("Error starting election: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Stop an election (set as inactive)
     * @param electionId Election ID
     * @return true if successful, false otherwise
     */
    public boolean stopElection(int electionId) {
        String sql = "UPDATE elections SET is_active = FALSE WHERE election_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, electionId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error stopping election: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update election information
     * @param election Election object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateElection(Election election) {
        String sql = "UPDATE elections SET election_name = ?, start_date = ?, end_date = ? " +
                     "WHERE election_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, election.getElectionName());
            pstmt.setTimestamp(2, election.getStartDate());
            pstmt.setTimestamp(3, election.getEndDate());
            pstmt.setInt(4, election.getElectionId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating election: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete an election
     * @param electionId Election ID
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteElection(int electionId) {
        String sql = "DELETE FROM elections WHERE election_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, electionId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting election: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extract Election object from ResultSet
     * @param rs ResultSet
     * @return Election object
     * @throws SQLException if extraction fails
     */
    private Election extractElectionFromResultSet(ResultSet rs) throws SQLException {
        Election election = new Election();
        election.setElectionId(rs.getInt("election_id"));
        election.setElectionName(rs.getString("election_name"));
        election.setStartDate(rs.getTimestamp("start_date"));
        election.setEndDate(rs.getTimestamp("end_date"));
        election.setActive(rs.getBoolean("is_active"));
        election.setCreatedBy(rs.getInt("created_by"));
        election.setCreatedAt(rs.getTimestamp("created_at"));
        return election;
    }
}
