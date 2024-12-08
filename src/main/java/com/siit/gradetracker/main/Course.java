package com.siit.gradetracker.main;

public class Course {
  private String courseDescription;
  private Double[] grades;
  private Double courseGrade;
  private int courseUnit;
  private boolean isIncludedInGWA;
  private String courseCode;
  private int gradeId;

  // Constructor
  public Course(String courseDescription, int courseUnit, Double[] grades, Double courseGrade,
      boolean isIncludedInGWA) {
    this.courseDescription = courseDescription;
    this.courseUnit = courseUnit;
    this.grades = grades;
    this.courseGrade = courseGrade;
    this.isIncludedInGWA = isIncludedInGWA;
  }

  public Course(int gradeId, String courseCode, String courseDescription, int courseUnit, Double[] grades,
      Double courseGrade, boolean isIncludedInGWA) {
    this.gradeId = gradeId;
    this.courseCode = courseCode;
    this.courseDescription = courseDescription;
    this.courseUnit = courseUnit;
    this.grades = grades;
    this.courseGrade = courseGrade;
    this.isIncludedInGWA = isIncludedInGWA;
  }

  // Getters

  public int getGradeId() {
    return gradeId;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public String getCourseDescription() {
    return courseDescription;
  }

  public int getCourseUnit() {
    return courseUnit;
  }

  public Double[] getGrades() {
    return grades;
  }

  public Double getCourseGrade() {
    return courseGrade;
  }

  public boolean isIncludedInGWA() {
    return isIncludedInGWA;
  }

}
