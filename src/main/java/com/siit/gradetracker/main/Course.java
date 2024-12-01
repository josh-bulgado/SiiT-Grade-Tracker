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
    this.courseCode = courseCode;
    this.courseDescription = courseDescription;
    this.courseUnit = courseUnit;
    this.grades = grades;
    this.courseGrade = courseGrade;
    this.isIncludedInGWA = isIncludedInGWA;
  }

  // Getters
  public String getCourseCode() {
    return courseCode;
  }

  public String getCourseDescription() {
    return courseDescription;
  }

  public int getCourseUnit() {
    return courseUnit;
  }

  public double[] getGrades() {
    return grades;
  }

  public Double getCourseGrade() {
    return courseGrade;
  }

  public boolean isIncludedInGWA() {
    return isIncludedInGWA;
  }

}
