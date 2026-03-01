package services;
import dao.CandidateDAO;
import dao.VoteDAO;
import dao.ElectionDAO;
import models.Candidate;
import models.Election;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for election results operations
 */
public class ResultService {
    private CandidateDAO candidateDAO;
    private VoteDAO voteDAO;
    private ElectionDAO electionDAO;

    public ResultService() {
        this.candidateDAO = new CandidateDAO();
        this.voteDAO = new VoteDAO();
        this.electionDAO = new ElectionDAO();
    }

    /**
     * Get results for all candidates
     * @return Map of candidate ID to vote count
     */
    public Map<Integer, Integer> getAllResults() {
        Map<Integer, Integer> results = new HashMap<>();
        List<Candidate> candidates = candidateDAO.getApprovedCandidates();
        
        for (Candidate candidate : candidates) {
            int voteCount = voteDAO.getVoteCountForCandidate(candidate.getCandidateId());
            results.put(candidate.getCandidateId(), voteCount);
        }
        
        return results;
    }

    /**
     * Get results by position
     * @param position Position name
     * @return Map of candidate ID to vote count for the position
     */
    public Map<Integer, Integer> getResultsByPosition(String position) {
        Map<Integer, Integer> results = new HashMap<>();
        List<Candidate> candidates = candidateDAO.getCandidatesByPosition(position);
        
        for (Candidate candidate : candidates) {
            int voteCount = voteDAO.getVoteCountForCandidate(candidate.getCandidateId());
            results.put(candidate.getCandidateId(), voteCount);
        }
        
        return results;
    }

    /**
     * Get winner for a position
     * @param position Position name
     * @return Winning Candidate object or null if no votes
     */
    public Candidate getWinnerByPosition(String position) {
        List<Candidate> candidates = candidateDAO.getCandidatesByPosition(position);
        
        if (candidates.isEmpty()) {
            return null;
        }

        Candidate winner = null;
        int maxVotes = -1;
        
        for (Candidate candidate : candidates) {
            int voteCount = voteDAO.getVoteCountForCandidate(candidate.getCandidateId());
            if (voteCount > maxVotes) {
                maxVotes = voteCount;
                winner = candidate;
            }
        }
        
        return winner;
    }

    /**
     * Get all winners (one per position)
     * @return Map of position to winning Candidate
     */
    public Map<String, Candidate> getAllWinners() {
        Map<String, Candidate> winners = new HashMap<>();
        List<String> positions = candidateDAO.getAllPositions();
        
        for (String position : positions) {
            Candidate winner = getWinnerByPosition(position);
            if (winner != null) {
                winners.put(position, winner);
            }
        }
        
        return winners;
    }

    /**
     * Get total votes cast
     * @return Total vote count
     */
    public int getTotalVotesCast() {
        Election activeElection = electionDAO.getActiveElection();
        
        if (activeElection != null) {
            return voteDAO.getTotalVotes(activeElection.getElectionId());
        }
        
        return 0;
    }

    /**
     * Get vote percentage for a candidate
     * @param candidateId Candidate ID
     * @return Vote percentage
     */
    public double getVotePercentage(int candidateId) {
        int totalVotes = getTotalVotesCast();
        
        if (totalVotes == 0) {
            return 0.0;
        }
        
        int candidateVotes = voteDAO.getVoteCountForCandidate(candidateId);
        return (candidateVotes * 100.0) / totalVotes;
    }

    /**
     * Get detailed results for all candidates
     * @return List of candidates with updated vote counts
     */
    public List<Candidate> getDetailedResults() {
        List<Candidate> candidates = candidateDAO.getApprovedCandidates();
        
        for (Candidate candidate : candidates) {
            int voteCount = voteDAO.getVoteCountForCandidate(candidate.getCandidateId());
            candidate.setVoteCount(voteCount);
        }
        
        return candidates;
    }

    /**
     * Check if results are available
     * @return true if results are available, false otherwise
     */
    public boolean areResultsAvailable() {
        return getTotalVotesCast() > 0;
    }
}
