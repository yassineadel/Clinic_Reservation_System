package com.example.clinicgui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class Rating {
@FXML
private Button returntomainmenu;
    @FXML
    private TableView<ObservableList<String>> statisticsTable;

    @FXML
    private ComboBox<Integer> ratingComboBox;

    @FXML
    private ComboBox<String> practitionerComboBox;

    @FXML
    private Label confirmationLabel;

    private DatabaseHandler db;

    // Data for ComboBox
    private ObservableList<String> practitionerNames = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        db = new DatabaseHandler(); // Initialize DatabaseHandler
        populateStatisticsTable(); // Populate statistics table
        populatePractitionerComboBox(); // Populate practitioner ComboBox
        ratingComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5)); // Rating options
    }

    // Populate the statistics table with data from the database
    private void populateStatisticsTable() {
        statisticsTable.getItems().clear();
        statisticsTable.getColumns().clear();

        try {
            String query = """
                SELECT p.PractitionerName,
                IFNULL(AVG(r.rating), 0) AS AverageRating,
                COUNT(r.id) AS RatingCount
                FROM practitioner p
                LEFT JOIN ratings r ON p.PractitionerID = r.practitioner_id
                GROUP BY p.PractitionerID, p.PractitionerName;
            """;

            ResultSet rs = db.executeQuery(query);

            // Dynamically create columns for the table based on the result set
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int colIndex = i;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(rs.getMetaData().getColumnName(i + 1));
                column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(colIndex)));
                statisticsTable.getColumns().add(column);
            }

            // Populate table data
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
            statisticsTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Populate the practitioner ComboBox with practitioner names
    private void populatePractitionerComboBox() {
        try {
            String query = "SELECT PractitionerName FROM practitioner";
            ResultSet rs = db.executeQuery(query);

            while (rs.next()) {
                practitionerNames.add(rs.getString("PractitionerName"));
            }
            practitionerComboBox.setItems(practitionerNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Submit the rating for the selected practitioner
    @FXML
    private void submitRating() {
        String selectedPractitioner = practitionerComboBox.getValue();
        Integer selectedRating = ratingComboBox.getValue();

        if (selectedPractitioner == null || selectedRating == null) {
            confirmationLabel.setText("Please select a practitioner and rating.");
            return;
        }

        String query = """
                INSERT INTO ratings (practitioner_id, rating)
                VALUES ((SELECT practitionerid FROM practitioner WHERE PractitionerName = ?), ?)
                """;
        db.executeUpdate(query, selectedPractitioner, selectedRating);

        confirmationLabel.setText("Rating submitted successfully!");
        populateStatisticsTable(); // Refresh the statistics table
    }

    @FXML
    private void gotomainmenu() throws IOException{
// Load the AfterSignup.fxml file
        Parent root = FXMLLoader.load(getClass().getResource("AfterLoginPatient.fxml"));

        // Get the current stage (the window where the button was clicked)
        Stage stage = (Stage) returntomainmenu.getScene().getWindow();

        // Set the new scene (the second page) and show the window
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
