module com.sgpt.mavenproject1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires javafx.graphics;

    opens com.sgpt.mavenproject1 to javafx.fxml;
    opens com.sgpt.mavenproject1.main to javafx.fxml;
    opens com.sgpt.mavenproject1.students to javafx.fxml;
    opens com.sgpt.mavenproject1.faculty to javafx.fxml;

    exports com.sgpt.mavenproject1;
    exports com.sgpt.mavenproject1.main;
    exports com.sgpt.mavenproject1.students;
    exports com.sgpt.mavenproject1.faculty;
}
