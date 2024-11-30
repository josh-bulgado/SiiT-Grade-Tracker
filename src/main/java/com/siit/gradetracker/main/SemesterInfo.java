package com.siit.gradetracker.main;

import java.util.List;

public class SemesterInfo {
  private List<Course> courses;
  private double gwa;

  public SemesterInfo(List<Course> courses, double gwa) {
    this.courses = courses;
    this.gwa = gwa;
  }

  public List<Course> getCourses() {
    return courses;
  }

  public double getGwa() {
    return gwa;
  }

  public void setGwa(double gwa) {
    this.gwa = gwa;
  }
}
