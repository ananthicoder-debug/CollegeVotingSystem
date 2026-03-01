package models;
import java.sql.Timestamp;

/**
 * Voter entity class representing a student voter
 */
public class Voter {
    private String voterId;
    private String rollNumber;
    private String fullName;
    private String department;
    private String yearOfStudy;
    private String email;
    private String password;
    private boolean isApproved;
    private boolean hasVoted;
    private Timestamp registeredAt;

    // Constructors
    public Voter() {
    }

    public Voter(String voterId, String rollNumber, String fullName, String department, 
                 String yearOfStudy, String email, String password) {
        this.voterId = voterId;
        this.rollNumber = rollNumber;
        this.fullName = fullName;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.email = email;
        this.password = password;
        this.isApproved = false;
        this.hasVoted = false;
    }

    // Getters and Setters
    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
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

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Timestamp registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public String toString() {
        return "Voter{" +
                "voterId='" + voterId + '\'' +
                ", rollNumber='" + rollNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", department='" + department + '\'' +
                ", yearOfStudy=" + yearOfStudy +
                ", isApproved=" + isApproved +
                ", hasVoted=" + hasVoted +
                '}';
    }
}
