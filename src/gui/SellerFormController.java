package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;
	
	private DepartmentService depService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private TextField txtBirthDate;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private ComboBox<Department> cbDepartment;
	
	@FXML
	private Label labelErrorDepartment;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private ObservableList<Department> obsList;

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	public void setDepartmentService(DepartmentService depService) {
		this.depService = depService;
	}
	
	public void subscribedDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		if (depService == null) {
			throw new IllegalStateException("Department service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch(DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
		catch(ValidationException e) {
			setErrorMessages(e.getErros());
		}
	}
	
	
	public void loadDepartment() {
		if (depService == null) {
			throw new IllegalStateException("Service was null");
		}
		
		if (obsList == null) {
			List<Department> list = depService.findAll();
			obsList = FXCollections.observableArrayList(list);
			cbDepartment.setItems(obsList);
		}
		
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}
		
	}

	private Seller getFormData() {
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		Date birthDate = null;
		
		if(txtName.getText() == null || txtName.getText().trim().equals("") ) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());
		
		
		if(txtEmail.getText() == null || txtEmail.getText().trim().equals("") ) {
			exception.addError("email", "Field can't be empty");
		}
		else if(txtEmail.getText().indexOf('@') <1 || txtEmail.getText().indexOf('@') !=  txtEmail.getText().lastIndexOf('@') || txtEmail.getText().indexOf('@') > txtEmail.getText().lastIndexOf('.')) {
			exception.addError("email", "E-mail format invalid");
		}
		obj.setEmail(txtEmail.getText());
		
		
		if(txtBirthDate.getText() == null || txtBirthDate.getText().trim().equals("") ) {
			exception.addError("birthDate", "Field can't be empty");
		} else
			try {
				birthDate = sdf.parse(txtBirthDate.getText());
				if(birthDate.compareTo(Date.from(LocalDate.now().minusYears(18).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())) > 0 ) {
					exception.addError("birthDate", "Seller must be older than 18 years old");
				}
			} catch (ParseException e) {
				
				exception.addError("birthDate", "Invalid date. Type date on format dd/MM/yyyy");
			}
		
		obj.setBirthDate(birthDate);
		
		
		if(txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addError("baseSallary", "Field can't be empty");
		}
		else if (Double.parseDouble(txtBaseSalary.getText()) < 1500.00 ) {
			exception.addError("baseSallary", "Minumum base salary is $ 1500.00");
		}
		else {
			obj.setBaseSalary(Double.parseDouble(txtBaseSalary.getText()));
		}
		
		
		if(cbDepartment.getValue() == null) {
			exception.addError("department", "Field can't be empty");
		}
		obj.setDepartment(cbDepartment.getValue());
		
		
		if(exception.getErros().size() > 0) {
			throw exception;
		}
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		Constraints.setTextFieldMaxLength(txtEmail, 30);
		//Constraints.setTextFieldDate(txtBirthDate);
		Constraints.setTextFieldDouble(txtBaseSalary);
		
		
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		if(entity.getBirthDate() != null) {
			txtBirthDate.setText(sdf.format(entity.getBirthDate()));
		}
		txtBaseSalary.setText(String.valueOf(entity.getBaseSalary()));
		if(obsList.contains(entity.getDepartment())) {
			cbDepartment.setValue(entity.getDepartment());
		}
	}
	
	private void setErrorMessages(Map<String,String> errors) {
		Set<String> fields = errors.keySet();
		
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
			
		}
		else {
			labelErrorName.setText(errors.get(""));
		}
		if (fields.contains("email")) {
			labelErrorEmail.setText(errors.get("email"));

		}
		else {
			labelErrorEmail.setText(errors.get(""));
		}
		if (fields.contains("birthDate")) {
			labelErrorBirthDate.setText(errors.get("birthDate"));
		}
		else {
			labelErrorBirthDate.setText(errors.get(""));
		}
		if (fields.contains("baseSallary")) {
			labelErrorBaseSalary.setText(errors.get("baseSallary"));
		}
		else {
			labelErrorBaseSalary.setText(errors.get(""));
		}
		if (fields.contains("department")) {
			labelErrorDepartment.setText(errors.get("department"));
		}
		else {
			labelErrorDepartment.setText(errors.get(""));
		}

	}
	

}
