package gui.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				try {
					sdf.parse(newValue);
				}
				catch (ParseException e) {
					txt.setText(oldValue);
				}		    	
		});
	}
}