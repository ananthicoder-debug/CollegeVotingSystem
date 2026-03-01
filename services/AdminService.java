package services;

import dao.VoterDAO;
import dao.CandidateDAO;
import dao.ElectionDAO;
import dao.VoteDAO;
import models.Voter;
import models.Candidate;
import models.Election;

import java.util.List;

/**
 * Service class for admin operations
 */
public class AdminService {

    private VoterDAO voterDAO;
    private CandidateDAO candidateDAO;
    private ElectionDAO electionDAO;
    private VoteDAO voteDAO;

    public AdminService() {
        this.voterDAO = new VoterDAO();
        this.candidateDAO = new CandidateDAO();
        this.electionDAO = new ElectionDAO();
        this.voteDAO = new VoteDAO();
    }

    // --------------------
    // VOTER MANAGEMENT
    // --------------------

    /** Get ALL voters (not filtered) */
    public List<Voter> getAllVoters() {
        return voterDAO.getAllApprovedVoters();
    }

    /** ⭐ Get ONLY APPROVED voters (USED in ManageVotersFrame) */
    public List<Voter> getApprovedVoters() {
        return voterDAO.getAllApprovedVoters();
    }

    /** Get pending voters */
    public List<Voter> getPendingVoters() {
        return voterDAO.getPendingVoters();
    }

    /** Approve a voter */
    public boolean approveVoter(String voterId) {
        return voterDAO.approveVoter(voterId);
    }

    /** Reject/Delete voter */
    public boolean rejectVoter(String voterId) {
        return voterDAO.deleteVoter(voterId);
    }

    /** Update voter info */
    public boolean updateVoter(Voter voter) {
        return voterDAO.updateVoter(voter);
    }

    // --------------------
    // CANDIDATE MANAGEMENT
    // --------------------

    public List<Candidate> getAllCandidates() {
        return candidateDAO.getAllCandidates();
    }

    public List<Candidate> getPendingCandidates() {
        return candidateDAO.getPendingCandidates();
    }

    public List<Candidate> getApprovedCandidates() {
        return candidateDAO.getApprovedCandidates();
    }

    public boolean approveCandidate(int candidateId) {
        return candidateDAO.approveCandidate(candidateId);
    }

    public boolean rejectCandidate(int candidateId) {
        return candidateDAO.deleteCandidate(candidateId);
    }

    public boolean updateCandidate(Candidate candidate) {
        return candidateDAO.updateCandidate(candidate);
    }

    public boolean addCandidate(Candidate candidate) {
        return candidateDAO.registerCandidate(candidate);
    }

    // --------------------
    // ELECTION MANAGEMENT
    // --------------------

    public List<Election> getAllElections() {
        return electionDAO.getAllElections();
    }

    public Election getActiveElection() {
        return electionDAO.getActiveElection();
    }

    public boolean createElection(Election election) {
        return electionDAO.createElection(election);
    }

    public boolean startElection(int electionId) {
        return electionDAO.startElection(electionId);
    }

    public boolean stopElection(int electionId) {
        return electionDAO.stopElection(electionId);
    }

    public boolean updateElection(Election election) {
        return electionDAO.updateElection(election);
    }

    public boolean deleteElection(int electionId) {
        voteDAO.deleteVotesByElection(electionId);
        return electionDAO.deleteElection(electionId);
    }

    // --------------------
    // DASHBOARD STATISTICS
    // --------------------

    public int getTotalVoters() {
        return getAllVoters().size();
    }

    public int getTotalCandidates() {
        return getAllCandidates().size();
    }

    public int getTotalVotesCast() {
        Election activeElection = getActiveElection();
        if (activeElection != null) {
            return voteDAO.getTotalVotes(activeElection.getElectionId());
        }
        return 0;
    }

    public int getPendingVoterCount() {
        return getPendingVoters().size();
    }

    public int getPendingCandidateCount() {
        return getPendingCandidates().size();
    }
}

