module com.siit.gradetracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires javafx.graphics;

    opens com.siit.gradetracker to javafx.fxml;
    opens com.siit.gradetracker.main to javafx.fxml;
    opens com.siit.gradetracker.students to javafx.fxml;
    opens com.siit.gradetracker.faculty to javafx.fxml;

    exports com.siit.gradetracker;
    exports com.siit.gradetracker.main;
    exports com.siit.gradetracker.students;
    exports com.siit.gradetracker.faculty;
}
