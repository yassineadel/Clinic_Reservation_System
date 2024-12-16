package com.example.clinicgui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class Signup{
    //beginning of initializing ids
    @FXML
    private Button Submit; //sign up button id

    @FXML
    private ChoiceBox<String> UserChoice; //choice between practitioner and patient

    //practitioners id in fxml file Signup
    @FXML
    private TextField PractitionerID; //practionerid id

    @FXML
    private TextField PractitionerName; //practionerid id

    @FXML
    private TextField PractitionerDepartment; //practionerid id

    @FXML
    private TextField Practitioneremail; //practionerid id

    @FXML
    private TextField Practitionerpassword; //practionerid id

    //patient id in fxml file signup
    @FXML
    private TextField patientID; // patient ID id

    @FXML
    private TextField patientName; // patient name id

    @FXML
    private TextField patientAge; // patient age id

    @FXML
    private TextField patientEmail; // patient email id

    @FXML
    private TextField patientPassword; // patient password id

    @FXML
    private TextField patientPhoneNUM; //patient phone number

    @FXML
    private HBox GenderHbox; //Patient Gender
    @FXML
    private ChoiceBox<String> GenderChoiceBox;

    @FXML
    private VBox PatientVBOX;
    //end of initializing the ids





    //MAIN METHOD THAT WORKS WITH SUBMIT BUTTON PRESS
    @FXML
    protected void DoneSubmitting() throws IOException {
        String UserRole = UserChoice.getValue();
        String Patientgender = GenderChoiceBox.getValue();
        //CHECKS IF THE USER ROLE IS EMPTY
        if (UserRole == null || UserRole.isEmpty()) {
            HelloController.showAlert("Error","You must specify a role from the choice box.", Alert.AlertType.ERROR);
            return;
        }
        User user; // made an object from the user

        try {
            if (UserRole.equals("Patient")) {
                // Validate patient fields
                if (patientID.getText().isEmpty() || patientAge.getText().isEmpty() || patientEmail.getText().isEmpty() ||
                        patientPassword.getText().isEmpty() || patientName.getText().isEmpty() || patientPhoneNUM.getText().isEmpty() ||
                        Patientgender == null || Patientgender.isEmpty()) {
                    HelloController.showAlert("Error", "All patient fields must be filled.", Alert.AlertType.ERROR);
                    return;
                }

                // Parse patient data
                int patientIDInt = Integer.parseInt(patientID.getText());
                int patientAgeInt = Integer.parseInt(patientAge.getText());


                // Create a Patient object
                user = new Patient(
                        patientEmail.getText(),
                        patientPassword.getText(),
                        patientIDInt,
                        patientName.getText(),
                        patientAgeInt,
                        Patientgender,
                        patientPhoneNUM.getText()
                );

            } else if (UserRole.equals("Practitioner")) {
                // Validate practitioner fields
                if (PractitionerID.getText().isEmpty() || PractitionerName.getText().isEmpty() ||
                        PractitionerDepartment.getText().isEmpty() || Practitioneremail.getText().isEmpty() ||
                        Practitionerpassword.getText().isEmpty()) {
                    HelloController.showAlert("Error", "All practitioner fields must be filled.", Alert.AlertType.ERROR);
                    return;
                }

                // Parse practitioner data
                int practitionerIDInt = Integer.parseInt(PractitionerID.getText());

                // Create a Practitioner object
                user = new Practitioner(
                        Practitioneremail.getText(),
                        Practitionerpassword.getText(),
                        practitionerIDInt,
                        PractitionerName.getText(),
                        PractitionerDepartment.getText()
                );

            } else {
                HelloController.showAlert("Error", "Invalid user role selected.", Alert.AlertType.ERROR);
                return;
            }

            // Polymorphic method call to submit the user's info
            user.submitInfo();

            // Navigate to the dashboard
            Stage stage = (Stage) Submit.getScene().getWindow();
            user.navigateToDashboard(stage);

        } catch (NumberFormatException e) {
            // Handle parsing errors for integer fields
            HelloController.showAlert("Error", "Please enter valid numeric values for ID and Age fields.", Alert.AlertType.ERROR);
        }
    }

    // Method to initialize the controller
    @FXML
    public void initialize() {
        // Add a listener for the ChoiceBox to handle changes in selection
        UserChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleChoiceChange(newValue);
            }
        });

        // Set default selection (optional)
        UserChoice.getSelectionModel().clearSelection();
    }

    // Method to handle the choice box selection and show/hide relevant fields
    private void handleChoiceChange(String selectedChoice) {
        // Hide all fields at first
        hideAllFields();

        // Show fields based on the selected option
        if (selectedChoice.equals("Practitioner")) {
            PractitionerID.setVisible(true);
            PractitionerName.setVisible(true);
            PractitionerDepartment.setVisible(true);
            Practitioneremail.setVisible(true);
            Practitionerpassword.setVisible(true);
        } else if (selectedChoice.equals("Patient")) {
            PatientVBOX.setVisible(true);
        }
    }
    private void hideAllFields(){
        PractitionerID.setVisible(false);
        PractitionerName.setVisible(false);
        PractitionerDepartment.setVisible(false);
        Practitioneremail.setVisible(false);
        Practitionerpassword.setVisible(false);
        PatientVBOX.setVisible(false);
    }


}
