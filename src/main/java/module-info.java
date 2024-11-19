module com.sgpt.mavenproject1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql; 
    

    opens com.sgpt.mavenproject1 to javafx.fxml;
    exports com.sgpt.mavenproject1;
    exports com.sgpt.mavenproject1.main;
}
