package gui.util;

import javafx.scene.control.TextField;

public class Constraints {

	public static void setTextFieldInteger(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*")) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldMaxLength(TextField txt, int max) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.length() > max) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldDouble(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldDate(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if(newValue.length() >= oldValue.length()) {
				if (newValue.length() == 1) {
					if (newValue.matches("\\d*")) {
						if (Integer.parseInt(newValue) > 3) {
							txt.setText(oldValue);
						}
					} else {
						txt.setText(oldValue);
					}
				} else if (newValue.length() == 2) {
					if (newValue.matches("\\d*")) {
						if (Integer.parseInt(newValue) > 31 || Integer.parseInt(newValue) < 1) {
							txt.setText(oldValue);
						} else {
							txt.setText(newValue + "/");
						}
					} else {
						txt.setText(oldValue);
					}
				} else if (newValue.length() == 4) {
					if (newValue.substring(3).matches("\\d*")) {
						if (Integer.parseInt(newValue.substring(3)) > 1) {
							txt.setText(oldValue);
						}
					} else {
						txt.setText(oldValue);
					}
				} else if (newValue.length() == 5) {
					if (newValue.substring(3).matches("\\d*")) {
						if (Integer.parseInt(newValue.substring(3)) > 12 || Integer.parseInt(newValue.substring(3)) < 1) {
							txt.setText(oldValue);
						} else {
							txt.setText(newValue + "/");
						}
					} else {
						txt.setText(oldValue);
					}
				} else if (newValue.length() > 10) {
					txt.setText(oldValue);
				}
			} 
			
		});
	}
}