package com.example.clinicgui;

import javafx.scene.control.Alert;

public class Patient extends User{
    private String patientName;
    private int age;
    private String gender;
    private String phone;
    private int patientid;

    // Constructor
    public Patient(String email, String password, String patientName) {
        super(email, password);
        this.patientName = patientName;
    }

    public Patient(String email, String password,int patientid,String patientName, int age, String gender, String phone) {
        super(email, password);
        this.patientid=patientid;
        this.patientName = patientName;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
    }

    @Override
    public void dashboard() {
        // Patient-specific dashboard logic
        HelloController.showAlert("Welcome☺","Hello "+patientName+"☺, you can reserve an appointment from here" , Alert.AlertType.INFORMATION);
    }

    @Override
    public void submitInfo() {
        DatabaseHandler db = new DatabaseHandler();
        int userId = db.InsertUserInfo(email, password, "Patient");
        db.insertPatient(patientid, userId, patientName, age, gender, phone);
    }
}
