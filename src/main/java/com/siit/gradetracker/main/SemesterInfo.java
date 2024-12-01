package com.siit.gradetracker.main;

import java.util.List;

public class SemesterInfo {
  private List<Course> courses;
  private double gwa;
  private int overAllUnits;

  public SemesterInfo(List<Course> courses, double gwa, int overaAllUnits) {
    this.courses = courses;
    this.gwa = gwa;
    this.overAllUnits = overaAllUnits;
  }

  public void setGwa(double gwa) {
    this.gwa = gwa;
  }

  public void setOverAllUnits(int overaAllUnits) {
    this.overAllUnits = overaAllUnits;
  }

  public List<Course> getCourses() {
    return courses;
  }

  public double getGwa() {
    return gwa;
  }

  public int getOverAllUnits() {
    return overAllUnits;
  }

}
