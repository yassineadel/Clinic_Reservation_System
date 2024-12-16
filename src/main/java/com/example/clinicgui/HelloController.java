package com.example.clinicgui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;

public class HelloController extends DatabaseHandler{
    @FXML
    private Button signupButton;
    @FXML
    private Button login;
    @FXML
    private RadioButton patientradio;
    @FXML
    private RadioButton practitionerradio;
    @FXML
    private TextField emailtext;
    @FXML
    private PasswordField passwordField;

    private static String storedEmail;
    private static String storePassword;

    DatabaseHandler database = new DatabaseHandler();

    public String getemail() {
        storedEmail = emailtext.getText(); // Store the email in a static variable
        return storedEmail;
    }

    public String getpassword(){
        storePassword = passwordField.getText();
        return storePassword;
    }


    // Static method to access the stored email
    public static String getStoredEmail() {
        return storedEmail;
    }

    @FXML
    protected void handlesignup() throws IOException {
        // Load the AfterSignup.fxml file
            Parent root = FXMLLoader.load(getClass().getResource("Signup.fxml"));

            // Get the current stage (the window where the button was clicked)
            Stage stage = (Stage) signupButton.getScene().getWindow();

            // Set the new scene (the second page) and show the window
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }


    @FXML
    protected void handlelogin() throws IOException {
        String email = getemail();
        String password = getpassword();
        String patientname = database.retrievepatientname(database.retrieveuserid(email));
        String practitionername = database.retrievepractname(database.retrieveuserid(email));
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill both email and password", Alert.AlertType.ERROR);
            return;
        } else if (database.retrieveEmail(email) && database.retrievePassword(email, password)) {
            User user;

            if (patientradio.isSelected()) {
                user = new Patient(email, password, patientname);
            } else if (practitionerradio.isSelected()) {
                user = new Practitioner(email, password , practitionername);
            } else {
                showAlert("Error", "Declare whether you are a patient or a doctor", Alert.AlertType.ERROR);
                return;
            }
            // Load the appropriate page based on user type
            loadUserPage(user);
            // Polymorphic method call to handle user dashboard
            user.dashboard();


        } else {
            showAlert("Something Went Wrong", "Email or Password incorrect", Alert.AlertType.ERROR);
        }
    }

    public static void showAlert(String title, String message , AlertType alertType) {
        Alert alert = new Alert(alertType); // Choose the alert type (ERROR, INFORMATION, etc.)
        alert.setTitle(title); // Set the title of the alert
        alert.setHeaderText(null); // Set no header
        alert.setContentText(message); // Set the content of the alert dialog

        // Change the text of the OK button (default is "OK")
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setText("Hello YAY"); // Change "OK" to "Got it"
        }

        alert.showAndWait(); // Show the alert and wait for user acknowledgment
    }


    private void loadUserPage(User user) throws IOException {
        Parent root;
        if (user instanceof Patient) {
            root = FXMLLoader.load(getClass().getResource("AfterLoginPatient.fxml"));
        } else {
            root = FXMLLoader.load(getClass().getResource("AfterLoginPractitioner.fxml"));
        }

        // Get the current stage and set the new scene
        Stage stage = (Stage) login.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}






