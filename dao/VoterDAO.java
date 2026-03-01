package dao;

import models.Voter;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoterDAO {

    /**
     * Authenticate voter login (voter_id + password)
     */
    public Voter authenticateVoter(String voterId, String password) {

        String sql = "SELECT * FROM voters WHERE voter_id = ? AND password = ? AND is_approved = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, voterId);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractVoter(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Fetch voter details by voter_id
     */
    public Voter getVoterById(String voterId) {

        String sql = "SELECT * FROM voters WHERE voter_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, voterId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractVoter(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Check if election is active
     */
    public boolean isElectionActive() {

        String sql = "SELECT is_active FROM elections WHERE election_id = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("is_active") == 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * Check if voter already voted
     */
    public boolean hasVoted(String voterId) {

        String sql = "SELECT has_voted FROM voters WHERE voter_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, voterId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("has_voted");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * Update voting status
     */
    public boolean updateVotingStatus(String voterId, boolean hasVoted) {

        String sql = "UPDATE voters SET has_voted = ? WHERE voter_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, hasVoted);
            ps.setString(2, voterId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    // ---------------------------------------------------------------------
    //                     ADMIN – VOTER MANAGEMENT
    // ---------------------------------------------------------------------

    /**
     * Get ALL voters (approved + pending)
     */
    public List<Voter> getAllVoters() {

        String sql = "SELECT * FROM voters";
        List<Voter> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractVoter(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    /**
     * Get only approved voters
     */
    public List<Voter> getAllApprovedVoters() {

        String sql = "SELECT * FROM voters WHERE is_approved = 1";
        List<Voter> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractVoter(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    /**
     * Get only pending voters
     */
    public List<Voter> getPendingVoters() {

        String sql = "SELECT * FROM voters WHERE is_approved = 0";
        List<Voter> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractVoter(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    // ---------------------------------------------------------------------
    //                     HELPER METHOD TO MAP DB → OBJECT
    // ---------------------------------------------------------------------

    private Voter extractVoter(ResultSet rs) throws Exception {
        Voter v = new Voter();
        v.setVoterId(rs.getString("voter_id"));
        v.setRollNumber(rs.getString("roll_number"));
        v.setFullName(rs.getString("full_name"));
        v.setDepartment(rs.getString("department"));
        v.setYearOfStudy(rs.getString("year_of_study"));
        v.setEmail(rs.getString("email"));
        v.setPassword(rs.getString("password"));
        v.setApproved(rs.getInt("is_approved") == 1);
        v.setHasVoted(rs.getBoolean("has_voted"));
        return v;
    }

public boolean voterExists(String rollNumber) {

    String sql = "SELECT roll_number FROM voters WHERE roll_number = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, rollNumber);
        ResultSet rs = ps.executeQuery();

        return rs.next(); // true if exists

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
public boolean registerVoter(Voter v) {
	String sql = "INSERT INTO voters (voter_id, roll_number, full_name, department, year_of_study, email, password, is_approved, has_voted) "
	           + "VALUES (?, ?, ?, ?, ?, ?, ?, 0, 0)";

    //String sql = "INSERT INTO voters (voter_id, roll_number, full_name, department, year_of_study, email, password, is_approved, has_voted) 
                  //VALUES (?, ?, ?, ?, ?, ?, ?, 0, 0)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        // voter_id format → v + roll_number
        String voterId = "v" + v.getRollNumber();

        ps.setString(1, voterId);
        ps.setString(2, v.getRollNumber());
        ps.setString(3, v.getFullName());
        ps.setString(4, v.getDepartment());
        ps.setString(5, v.getYearOfStudy());
        ps.setString(6, v.getEmail());
        ps.setString(7, v.getPassword());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
public boolean updateVoter(Voter v) {

   
    String sql = "UPDATE voters SET full_name=?, department=?, year_of_study=?, email=?, password=?, is_approved=?, has_voted=? "
                		           + "WHERE voter_id=?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, v.getFullName());
        ps.setString(2, v.getDepartment());
        ps.setString(3, v.getYearOfStudy());
        ps.setString(4, v.getEmail());
        ps.setString(5, v.getPassword());
        ps.setBoolean(6, v.isApproved());
        ps.setBoolean(7, v.hasVoted());
        ps.setString(8, v.getVoterId());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
}

