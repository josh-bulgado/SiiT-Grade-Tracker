package com.siit.gradetracker.main;

public class Course {
  private String courseDescription;
  private double[] grades;
  private Double courseGrade;
  private int courseUnit;
  private boolean isIncludedInGWA;
  private String courseCode;

  // Constructor
  public Course(String courseCode, String courseDescription, int courseUnit, double[] grades, Double courseGrade,
      boolean isIncludedInGWA) {
    this.courseDescription = courseDescription;
    this.grades = grades;
    this.courseGrade = courseGrade;
    this.courseUnit = courseUnit;
    this.isIncludedInGWA = isIncludedInGWA;
  }

  public Course(String courseCode, String courseDescription, int courseUnit, double[] grades) {
    this.courseDescription = courseDescription;
    this.grades = grades;
    // this.courseGrade = courseGrade;
    this.courseUnit = courseUnit;
    // this.isIncludedInGWA = isIncludedInGWA;
    this.courseCode = courseCode;
  }

  // Getters and Setters
  public String getCourseDescription() {
    return courseDescription;
  }

  public double[] getGrades() {
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

  public String getCourseCode() {
    return courseCode;
  }
}
