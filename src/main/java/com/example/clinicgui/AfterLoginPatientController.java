package com.example.clinicgui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class AfterLoginPatientController extends DatabaseHandler{

    @FXML
    private ChoiceBox<String> DepartmentChoices;
    @FXML
    private ChoiceBox<String> DoctorChoices;
    @FXML
    private ChoiceBox<String> appointmentDateChoiceBox;
    @FXML
    private ChoiceBox<String> AppointmentChoicesTime;
    @FXML
    private Button confirmReservationButton;
    @FXML
    private Button CancelReservation;

    DatabaseHandler db = new DatabaseHandler();


    @FXML
    public void initialize() throws IOException {
        // Populate Department Choices
        ObservableList<String> departments = FXCollections.observableArrayList("Cardiology", "Dentistry", "Neurology", "Dermatology", "Internist");
        DepartmentChoices.setItems(departments);

        // Add listener for department choice box
        DepartmentChoices.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateDoctors(newValue);
            }
        });

        // Add listener for doctor choice box
        DoctorChoices.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateAvailableDays(newValue);
            }
        });

        // Add listener for appointment date choice box
        appointmentDateChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    String selectedDoctor = DoctorChoices.getValue();
                    // Use the corrected date format pattern for numeric month
                    LocalDate selectedDate = LocalDate.parse(newValue, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    populateAvailableTimes(selectedDoctor, selectedDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format: " + newValue);
                    e.printStackTrace();
                }
            }
        });


        // Add listener for appointment time choice box
        AppointmentChoicesTime.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            confirmReservationButton.setDisable(newValue == null);
        });

        // Initialize the confirm button as disabled by default
        confirmReservationButton.setDisable(true);



        // MAKING THE CONFIRMATION BUTTON WORKS WELL
        confirmReservationButton.setOnAction(event -> {
            String selectedDoctor = getselecteddoc();
            String selectedDate = appointmentDateChoiceBox.getValue();
            String selectedTime = AppointmentChoicesTime.getValue();

            if (selectedDoctor == null || selectedDate == null || selectedTime == null) {
                HelloController.showAlert("Error", "Please select all fields", AlertType.ERROR);
            } else {
                db.insertAppointment( db.retrievepatientID(HelloController.getStoredEmail()),db.retrievepractitionerID(selectedDoctor), LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")), selectedTime);
                Parent root = null;
                try {
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Rating.fxml")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // Get the current stage (the window where the button was clicked)
                Stage stage = (Stage) confirmReservationButton.getScene().getWindow();

                // Set the new scene (the second page) and show the window
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        });
    }

    //get methods to get values of choice
    protected String getselecteddoc(){
        return DoctorChoices.getValue();
    }

    //POPULATE DOCTORS
    private void populateDoctors(String department) {
        List<String> doctors = db.getDoctorsByDepartment(department);
        DoctorChoices.setItems(FXCollections.observableArrayList(doctors));
    }

    // Populate available days based on the selected doctor
    private void populateAvailableDays(String selectedDoctor) {
        if (selectedDoctor == null || selectedDoctor.isBlank()) {
            System.out.println("Error: No doctor selected!");
            return;
        }

        List<DayOfWeek> availableDays = db.getDoctorAvailableDays(selectedDoctor);

        // Calculate the next 30 days of available dates
        List<String> availableDates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 30; i++) {
            LocalDate date = today.plusDays(i);
            if (availableDays.contains(date.getDayOfWeek())) {
                availableDates.add(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        }

        // Populate the ChoiceBox
        appointmentDateChoiceBox.getItems().clear();
        appointmentDateChoiceBox.getItems().addAll(availableDates);
    }

    //SHOW AVAILABLE TIMES
    private void populateAvailableTimes(String doctorName, LocalDate selectedDate) {
        try {
            // Fetch available times from the database or logic
            List<String> availableTimes = db.getAvailableTimesByDateAndDoctor(doctorName, selectedDate);

            // Ensure the ChoiceBox is populated
            AppointmentChoicesTime.setItems(FXCollections.observableArrayList(availableTimes));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error populating available times: " + e.getMessage());
        }
    }

    @FXML  //RETURN TO MAIN MENU
    private void ReturnBack () throws IOException{
        Parent root = null;
        Stage stage = (Stage) CancelReservation.getScene().getWindow();
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }




}

