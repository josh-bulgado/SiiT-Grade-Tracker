package com.siit.gradetracker.util;

import javafx.scene.control.Alert;

public class DisplayError {
  public void showErrorDialog(String title, String message) {
    // Show a user-friendly dialog with the error message
    // This could be a simple JavaFX Alert
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
