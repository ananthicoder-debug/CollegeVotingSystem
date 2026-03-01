package services;

import dao.CandidateDAO;
import dao.VoterDAO;

public class VotingService {

    private final VoterDAO voterDAO;
    private final CandidateDAO candidateDAO;

    public VotingService() {
        this.voterDAO = new VoterDAO();
        this.candidateDAO = new CandidateDAO();
    }

    /**
     * Check if election is active
     */
    public boolean isElectionActive() {
        return voterDAO.isElectionActive();
    }

    /**
     * Check if a voter has already voted
     */
    public boolean hasVoted(String voterId) {
        return voterDAO.hasVoted(voterId);
    }

    /**
     * Cast a vote for a candidate
     */
    public boolean castVote(String voterId, int candidateId) {

        // ❌ Election not active
        if (!voterDAO.isElectionActive()) {
            System.err.println("❌ Election is not active!");
            return false;
        }

        // ❌ Voter already voted
        if (voterDAO.hasVoted(voterId)) {
            System.err.println("❌ Voter already voted!");
            return false;
        }

        // ✔ Increase the candidate vote count
        boolean voteIncremented = candidateDAO.incrementVoteCount(candidateId);

        // ✔ Mark voter as voted
        boolean voterUpdated = voterDAO.updateVotingStatus(voterId, true);

        return voteIncremented && voterUpdated;
    }
}
