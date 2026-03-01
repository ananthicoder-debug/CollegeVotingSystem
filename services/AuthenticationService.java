package services;

import dao.AdminDAO;
import dao.VoterDAO;
import models.Admin;
import models.Voter;

public class AuthenticationService {

    private VoterDAO voterDAO;
    private AdminDAO adminDAO;

    public AuthenticationService() {
        voterDAO = new VoterDAO();
        adminDAO = new AdminDAO();
    }

    // LOGIN VOTER
    public Voter authenticateVoter(String voterId, String password) {
        Voter v = voterDAO.authenticateVoter(voterId, password);

        if (v != null && !v.isApproved()) {
            System.out.println("Voter not approved by admin.");
            return null;
        }
        return v;
    }

    // LOGIN ADMIN
    public Admin authenticateAdmin(String username, String password) {
        return adminDAO.authenticateAdmin(username, password);
    }

    // REGISTER VOTER
    public boolean registerVoter(Voter voter) {

        if (voterDAO.voterExists(voter.getRollNumber())) {
            System.out.println("Roll number already exists.");
            return false;
        }

        return voterDAO.registerVoter(voter);
    }

    // CHANGE VOTER PASSWORD
    public boolean changeVoterPassword(String voterId, String oldPass, String newPass) {
        Voter v = voterDAO.authenticateVoter(voterId, oldPass);
        if (v == null) return false;

        v.setPassword(newPass);
        return voterDAO.updateVoter(v);
    }

    // CHANGE ADMIN PASSWORD
    public boolean changeAdminPassword(int adminId, String oldPass, String newPass) {
        Admin a = adminDAO.getAdminById(adminId);
        if (a == null || !a.getPassword().equals(oldPass)) return false;

        return adminDAO.changePassword(adminId, newPass);
    }
}
