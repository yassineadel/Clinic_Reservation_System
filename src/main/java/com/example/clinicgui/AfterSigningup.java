package com.example.clinicgui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class AfterSigningup {
    @FXML
    private Button gotomainpage;

    @FXML
    protected void Gotomainpage() throws IOException {
        // Load the AfterSignup.fxml file
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));

        // Get the current stage (the window where the button was clicked)
        Stage stage = (Stage) gotomainpage.getScene().getWindow();

        // Set the new scene (the second page) and show the window
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}


