package com.example.clinicgui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class User {
    protected String email;
    protected String password;

    public User(String email , String password){
        this.email=email;
        this.password=password;
    }

    public abstract void dashboard();

    public abstract void submitInfo();

    public void navigateToDashboard(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("After-Signingup.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
