module com.siit.gradetracker {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    // add icon pack modules
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.unicons;

    requires transitive java.sql;
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
