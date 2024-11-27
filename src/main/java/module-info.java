module com.siit {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires javafx.graphics;

    opens com.siit to javafx.fxml;
    opens com.siit.main to javafx.fxml;
    opens com.siit.students to javafx.fxml;
    opens com.siit.faculty to javafx.fxml;

    exports com.siit;
    exports com.siit.main;
    exports com.siit.students;
    exports com.siit.faculty;
}
