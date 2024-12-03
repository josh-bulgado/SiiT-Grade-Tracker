package com.siit.gradetracker.util;

import java.util.*;

import com.siit.gradetracker.main.Course;
import com.siit.gradetracker.main.SemesterInfo;

public class GradeComputation {

  // TreeMap to store score thresholds (key) and corresponding grade (value)
  private static final TreeMap<Double, Double> gradeThresholds = new TreeMap<>();

  static {
    // Initialize the grade thresholds (key: score, value: grade)
    gradeThresholds.put(97.5, 1.00);
    gradeThresholds.put(94.5, 1.25);
    gradeThresholds.put(91.5, 1.50);
    gradeThresholds.put(88.5, 1.75);
    gradeThresholds.put(85.5, 2.00);
    gradeThresholds.put(81.5, 2.25);
    gradeThresholds.put(77.5, 2.50);
    gradeThresholds.put(73.5, 2.75);
    gradeThresholds.put(69.5, 3.00);
    gradeThresholds.put(0.0, 5.00); // Default failing grade
  }

  /**
   * Computes the course grade based on weighted scores from multiple grading
   * periods.
   *
   * @param grades An array of grades: [prelims, midterm, prefinal, finals]
   * @return The computed course grade
   */
  public double computeCourseGrade(double[] grades) {
    // Calculate weighted score for each grading period
    Double prelimsPS = grades[0] * 0.2;
    Double midtermPS = grades[1] * 0.2;
    Double prefinalPS = grades[2] * 0.2;
    Double finalPS = grades[3] * 0.4;

    Double courseScore = prelimsPS + midtermPS + prefinalPS + finalPS;

    return getCourseGrade(courseScore);
  }

  /**
   * Returns the course grade based on the computed course score.
   *
   * @param courseScore The computed score based on weighted grades
   * @return The grade corresponding to the given score
   */
  private double getCourseGrade(double courseScore) {
    // Find the closest threshold score less than or equal to the courseScore
    Map.Entry<Double, Double> entry = gradeThresholds.floorEntry(courseScore);

    // Return the grade for the found threshold score, or default to 5.00 (failing)
    return (entry != null) ? entry.getValue() : 5.00;
  }

  public void calculateSemesterGWA(Map<String, SemesterInfo> coursesBySemester, Map<String, List<Course>> tempCoursesBySemester) {
        for (Map.Entry<String, List<Course>> entry : tempCoursesBySemester.entrySet()) {
            String semester = entry.getKey();
            List<Course> courses = entry.getValue();

            // Calculate the GWA for the semester
            double totalUnits = 0;
            double totalCredits = 0;
            int overAllUnits = 0;

            for (Course course : courses) {
                overAllUnits += course.getCourseUnit();
                if (course.isIncludedInGWA()) {
                    totalUnits += course.getCourseUnit();
                    totalCredits += course.getCourseGrade() * course.getCourseUnit();
                }
            }

            double gwa = totalUnits > 0 ? totalCredits / totalUnits : 0.0;

            // Store the SemesterInfo (list of courses and calculated GWA) in the map
            coursesBySemester.put(semester, new SemesterInfo(courses, gwa, overAllUnits));
        }
    }
}
