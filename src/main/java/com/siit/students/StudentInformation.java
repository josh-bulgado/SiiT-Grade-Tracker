package com.siit.students;

public class StudentInformation {
  private String studentId;
  private String lastName;
  private String firstName;
  private String programAcronym;

  public StudentInformation(String studentId, String lastName, String firstName, String programAcronym) {
    this.studentId = studentId;
    this.lastName = lastName;
    this.firstName = firstName;
    this.programAcronym = programAcronym;
  }

  // Getters and Setters
  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getProgramAcronym() {
    return programAcronym;
  }

  public void setProgramAcronym(String programAcronym) {
    this.programAcronym = programAcronym;
  }
}