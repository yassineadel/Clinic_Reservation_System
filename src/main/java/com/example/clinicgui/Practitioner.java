package com.example.clinicgui;

import javafx.scene.control.Alert;

public class Practitioner extends User{
    private String practitionerName;
    private String department;
    private int practitionerID;

    // Constructor
    public Practitioner(String email, String password, String practitionerName) {
        super(email, password);
        this.practitionerName = practitionerName;
    }

    public Practitioner(String email, String password,int practitionerID, String practitionerName, String department) {
        super(email, password);
        this.practitionerID = practitionerID;
        this.practitionerName = practitionerName;
        this.department = department;
    }

    @Override
    public void dashboard() {
        // Practitioner-specific dashboard logic
        HelloController.showAlert("Welcome☺","Hello DR."+practitionerName+"☺" , Alert.AlertType.INFORMATION);
    }

    @Override
    public void submitInfo() {
        DatabaseHandler db = new DatabaseHandler();
        int userId = db.InsertUserInfo(email, password, "Practitioner");
        db.insertPractitioner(practitionerID, practitionerName, department, userId);
    }
}
