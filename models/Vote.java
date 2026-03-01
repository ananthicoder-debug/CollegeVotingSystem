package models;

import java.sql.Timestamp;

/**
 * Vote entity class representing a cast vote
 */
public class Vote {
    private int voteId;
    private String voterId;
    private int candidateId;
    private int electionId;
    private Timestamp votedAt;

    // Constructors
    public Vote() {
    }

    public Vote(String voterId, int candidateId, int electionId) {
        this.voterId = voterId;
        this.candidateId = candidateId;
        this.electionId = electionId;
    }

    // Getters and Setters
    public int getVoteId() {
        return voteId;
    }

    public void setVoteId(int voteId) {
        this.voteId = voteId;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getElectionId() {
        return electionId;
    }

    public void setElectionId(int electionId) {
        this.electionId = electionId;
    }

    public Timestamp getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(Timestamp votedAt) {
        this.votedAt = votedAt;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "voteId=" + voteId +
                ", voterId='" + voterId + '\'' +
                ", candidateId=" + candidateId +
                ", electionId=" + electionId +
                ", votedAt=" + votedAt +
                '}';
    }
}

