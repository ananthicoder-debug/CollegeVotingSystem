package models;

import java.sql.Timestamp;

/**
 * Election entity class representing an election event
 */
public class Election {
    private int electionId;
    private String electionName;
    private Timestamp startDate;
    private Timestamp endDate;
    private boolean isActive;
    private int createdBy;
    private Timestamp createdAt;

    // Constructors
    public Election() {
    }

    public Election(String electionName, Timestamp startDate, Timestamp endDate, int createdBy) {
        this.electionName = electionName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdBy = createdBy;
        this.isActive = false;
    }

    // Getters and Setters
    public int getElectionId() {
        return electionId;
    }

    public void setElectionId(int electionId) {
        this.electionId = electionId;
    }

    public String getElectionName() {
        return electionName;
    }

    public void setElectionName(String electionName) {
        this.electionName = electionName;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Election{" +
                "electionId=" + electionId +
                ", electionName='" + electionName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isActive=" + isActive +
                '}';
    }
}
