package models;

import java.sql.Timestamp;

/**
 * Final Candidate model
 * Matches MySQL table 'candidates'
 * -----------------------------------
 * Table columns:
 * candidate_id, rollno, name, dept, activities,
 * symbol_filename, photo_path, description_path,
 * approved, vote_count, created_at
 */
public class Candidate {

    private int candidateId;
    private String rollNumber;
    private String fullName;
    private String department;
    private String activities;         // Extra Curricular / Co Curricular / Sports
    private String symbolFilename;     // Symbol image name (from /symbols folder)
    private String photoPath;          // Candidate photo path
    private String descriptionPath;    // Candidate description PDF path
    private boolean approved;          // Approval flag (default false)
    private int voteCount;             // Number of votes received
    private Timestamp registeredAt;    // When candidate was added (created_at in DB)

    // -------------------- Constructors --------------------
    public Candidate() {
        this.approved = false;
        this.voteCount = 0;
    }

    public Candidate(int candidateId, String rollNumber, String fullName, String department,
                     String activities, String symbolFilename, String photoPath,
                     String descriptionPath, boolean approved, int voteCount, Timestamp registeredAt) {
        this.candidateId = candidateId;
        this.rollNumber = rollNumber;
        this.fullName = fullName;
        this.department = department;
        this.activities = activities;
        this.symbolFilename = symbolFilename;
        this.photoPath = photoPath;
        this.descriptionPath = descriptionPath;
        this.approved = approved;
        this.voteCount = voteCount;
        this.registeredAt = registeredAt;
    }

    // -------------------- Getters and Setters --------------------
    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getSymbolFilename() {
        return symbolFilename;
    }

    public void setSymbolFilename(String symbolFilename) {
        this.symbolFilename = symbolFilename;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getDescriptionPath() {
        return descriptionPath;
    }

    public void setDescriptionPath(String descriptionPath) {
        this.descriptionPath = descriptionPath;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Timestamp registeredAt) {
        this.registeredAt = registeredAt;
    }
   
    // -------------------- Utility Methods --------------------
    public void incrementVoteCount() {
        this.voteCount++;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "candidateId=" + candidateId +
                ", rollNumber='" + rollNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", department='" + department + '\'' +
                ", activities='" + activities + '\'' +
                ", symbolFilename='" + symbolFilename + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", descriptionPath='" + descriptionPath + '\'' +
                ", approved=" + approved +
                ", voteCount=" + voteCount +
                ", registeredAt=" + registeredAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Candidate that = (Candidate) o;

        if (candidateId != that.candidateId) return false;
        return rollNumber != null ? rollNumber.equals(that.rollNumber) : that.rollNumber == null;
    }

    @Override
    public int hashCode() {
        int result = candidateId;
        result = 31 * result + (rollNumber != null ? rollNumber.hashCode() : 0);
        return result;
    }

	public Object getPosition() {
		// TODO Auto-generated method stub
		return null;
	}
}
