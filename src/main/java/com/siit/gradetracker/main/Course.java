package com.siit.gradetracker.main;

public class Course {
  private String courseDescription;
  private Double[] grades;
  private Double courseGrade;
  private int courseUnit;
  private boolean isIncludedInGWA;

  // Constructor
  public Course(String courseDescription, Double[] grades, Double courseGrade, int courseUnit,
      boolean isIncludedInGWA) {
    this.courseDescription = courseDescription;
    this.grades = grades;
    this.courseGrade = courseGrade;
    this.courseUnit = courseUnit;
    this.isIncludedInGWA = isIncludedInGWA;
  }

  // Getters and Setters
  public String getCourseDescription() {
    return courseDescription;
  }

  public Double[] getGrades() {
    return grades;
  }

  public Double getCourseGrade() {
    return courseGrade;
  }

  public int getCourseUnit() {
    return courseUnit;
  }

  public boolean isIncludedInGWA() {
    return isIncludedInGWA;
  }
}
