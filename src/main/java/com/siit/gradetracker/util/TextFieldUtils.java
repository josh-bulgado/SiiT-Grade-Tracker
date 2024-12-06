package com.siit.gradetracker.util;

import javafx.scene.control.TextField;

public class TextFieldUtils {
  public static void makeTextFieldInteger(TextField textField) {
    textField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("^[\\d*]$")) {
        textField.setText(newValue.replaceAll("[^\\d]", ""));
      }
    });
  }

  public static void makeTextFieldDouble(TextField textField) {
    textField.textProperty().addListener((observable, oldValue, newValue) -> {
      try {
        if (!newValue.matches("\\d*\\.?\\d*")) {
          textField.setText(oldValue);
        } else if (!newValue.isEmpty() && Double.parseDouble(newValue) > 100) {
          textField.setText(oldValue);
        }
      } catch (NumberFormatException e) {
        textField.setText(oldValue);
      }
    });
  }
}
