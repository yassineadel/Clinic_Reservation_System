    package com.example.clinicgui;

    import javafx.concurrent.Task;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Alert;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.control.TextField;
    import javafx.stage.Stage;

    import java.io.IOException;



    public class AfterLogingInPractitionerController  {
    @FXML
        private Button logout;
    @FXML
        private Button SaveButton;
    @FXML
        private Label deptname;
    @FXML
        private Label pracname;
    @FXML
        private Label addressinfolabel;
    @FXML
        private Label clinicnamelabel;
    @FXML
        private Label phonenumlabel;
    @FXML
        private  Label Emaildislabel;
    @FXML
        private Label clinicnumberlabel;
    @FXML
    private Label Daylabel;
    @FXML
    private Label STARTTIMELABEL;
    @FXML
    private  Label ENDTIMELABEL;
    @FXML
        private TextField ClinicAddress;
    @FXML
        private TextField ClinicNumber;
    @FXML
        private TextField ClinicPhone;
    @FXML
        private TextField ClinicEmail;
    @FXML
            private TextField ChooseAday;
    @FXML
            private  TextField ChooseStarttime;
    @FXML
            private TextField ChooseEndTime;
    @FXML
            private Label ScheduleLabel;

    DatabaseHandler db = new DatabaseHandler();


     @FXML
    public void initialize(){
         //when loaded display practitioner info
         String pracemail=HelloController.getStoredEmail();
         int userid = db.retrieveuserid(pracemail);
         pracname.setText(db.retrievepractname(userid));
         deptname.setText(db.retrievepracdepartment(userid));
         clinicnamelabel.setText("DR. "+db.retrievepractname(userid)+" Clinic");
         if(db.isclinicempty(userid)) {
             validatetextfields();
         }
         else {
             //HIDE SAVE BUTTON
             SaveButton.setVisible(false);
             hidetextfields();
             String idd= String.valueOf(db.retrieveclinicID(userid));
             //threads to retrieve data
             retrieveAddressFromDb(userid);
             retrievePhonenumFromDb(userid);
             retrieveEmailFromDb(userid);
             retrieveClinicIDFromDb(userid);
             retrievePriceFromDb(userid);
             //end of retrieving by threads
              phonenumlabel.setText(db.retrieveclinicphone(userid));
              clinicnumberlabel.setText(idd);
              Emaildislabel.setText(db.retrieveclinicemail(userid));
         }

     }

        @FXML
        public void Logout() throws IOException{
            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));

            // Get the current stage (the window where the button was clicked)
            Stage stage = (Stage) logout.getScene().getWindow();

            // Set the new scene (the second page) and show the window
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        @FXML
        public void saveinfo() throws IOException {

            String pracemail = HelloController.getStoredEmail();
            String caddress = ClinicAddress.getText();
            String cnumber1 = ClinicNumber.getText();
            int cnumber = Integer.parseInt(cnumber1);
            String cphone = ClinicPhone.getText();
            String cemail = ClinicEmail.getText();
            String StartTime = ChooseStarttime.getText();
            String EndTime = ChooseEndTime.getText();
            String DAYOFTHEWEEK = ChooseAday.getText();

                db.reconnect();
                // Check if the connection is open before performing any operations
                    int userid = db.retrieveuserid(pracemail);
                    db.insertClinic(cnumber, caddress, cphone, cemail, 250.0, db.retrieveuserid(pracemail));
                    db.InsertIntoAvailability(StartTime,EndTime,DAYOFTHEWEEK,db.retrievepractitionerID(db.retrievepractname(userid)));

        }

         //if there is info inside database
        private void hidetextfields(){
            ClinicNumber.setVisible(false);
            ClinicNumber.setDisable(true);
            ClinicAddress.setVisible(false);
            ClinicAddress.setDisable(true);
            ClinicPhone.setVisible(false);
            ClinicPhone.setDisable(true);
            ClinicEmail.setVisible(false);
            ClinicEmail.setDisable(true);
            ChooseEndTime.setVisible(false);
            ChooseEndTime.setDisable(true);
            ChooseStarttime.setVisible(false);
            ChooseStarttime.setDisable(true);
            ChooseAday.setVisible(false);
            ChooseAday.setDisable(true);
            //labels
            addressinfolabel.setVisible(true);
            phonenumlabel.setVisible(true);
            Emaildislabel.setVisible(true);
            clinicnumberlabel.setVisible(true);
            ENDTIMELABEL.setVisible(false);
            STARTTIMELABEL.setVisible(false);
            Daylabel.setVisible(false);
            ScheduleLabel.setVisible(false);
        }

        private void validatetextfields(){
            ClinicNumber.setVisible(true);
            ClinicNumber.setDisable(false);
            ClinicAddress.setVisible(true);
            ClinicAddress.setDisable(false);
            ClinicPhone.setVisible(true);
            ClinicPhone.setDisable(false);
            ClinicEmail.setVisible(true);
            ClinicEmail.setDisable(false);
            ChooseEndTime.setVisible(true);
            ChooseEndTime.setDisable(false);
            ChooseStarttime.setVisible(true);
            ChooseStarttime.setDisable(false);
            ChooseAday.setVisible(true);
            ChooseAday.setDisable(false);
            //labels
            addressinfolabel.setVisible(false);
            phonenumlabel.setVisible(false);
            Emaildislabel.setVisible(false);
            clinicnumberlabel.setVisible(false);
            ENDTIMELABEL.setVisible(true);
            STARTTIMELABEL.setVisible(true);
            Daylabel.setVisible(true);
        }


        //Retrieving data by tasks(threads)
        public void retrieveAddressFromDb(int userid) {
            // Create a new task to run the database query in the background
            Task<String> task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    DatabaseHandler dbHandler = new DatabaseHandler();
                    return dbHandler.retrieveaddress(userid); // Call the database method
                }
            };

            // Set what happens when the task succeeds (after the query completes)
            task.setOnSucceeded(e -> {
                String address = task.getValue();
                addressinfolabel.setText(address); // Update the UI with the retrieved address
            });

            // Set what happens if the task fails (e.g., error in the query)
            task.setOnFailed(e -> {
                addressinfolabel.setText("Error retrieving address");
            });

            // Start the task in a background thread
            new Thread(task).start();
        }
        public void retrievePhonenumFromDb(int userid) {
            // Create a new task to run the database query in the background
            Task<String> task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    DatabaseHandler dbHandler = new DatabaseHandler();
                    return dbHandler.retrieveclinicphone(userid); // Call the database method
                }
            };

            // Set what happens when the task succeeds (after the query completes)
            task.setOnSucceeded(e -> {
                String phonenum = task.getValue();
                phonenumlabel.setText(phonenum); // Update the UI with the retrieved address
            });

            // Set what happens if the task fails (e.g., error in the query)
            task.setOnFailed(e -> {
                phonenumlabel.setText("Error retrieving address");
            });

            // Start the task in a background thread
            new Thread(task).start();
        }
        public void retrieveEmailFromDb(int userid) {
            // Create a new task to run the database query in the background
            Task<String> task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    DatabaseHandler dbHandler = new DatabaseHandler();
                    return dbHandler.retrieveclinicemail(userid); // Call the database method
                }
            };

            // Set what happens when the task succeeds (after the query completes)
            task.setOnSucceeded(e -> {
                String email = task.getValue();
                Emaildislabel.setText(email); // Update the UI with the retrieved address
            });

            // Set what happens if the task fails (e.g., error in the query)
            task.setOnFailed(e -> {
                Emaildislabel.setText("Error retrieving address");
            });

            // Start the task in a background thread
            new Thread(task).start();
        }
        public void retrieveClinicIDFromDb(int userid) {
            // Create a new task to run the database query in the background
            Task<Integer> task = new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    DatabaseHandler dbHandler = new DatabaseHandler();
                    return dbHandler.retrieveclinicID(userid); // Call the database method to retrieve an integer
                }
            };

            // Set what happens when the task succeeds (after the query completes)
            task.setOnSucceeded(e -> {
                Integer clinicID = task.getValue();
                clinicnumberlabel.setText(String.valueOf(clinicID)); // Update the UI with the retrieved clinic ID
            });

            // Set what happens if the task fails (e.g., error in the query)
            task.setOnFailed(e -> {
                clinicnumberlabel.setText("Error retrieving Clinic ID");
            });

            // Start the task in a background thread
            new Thread(task).start();
        }
        public void retrievePriceFromDb(int userid){
            Task<Double> task = new Task<Double>() {
                @Override
                protected Double call() throws Exception {
                    DatabaseHandler dbHandler = new DatabaseHandler();
                    return dbHandler.retrieveprice(userid); // Call the database method to retrieve an integer
                }
            };

            // Set what happens when the task succeeds (after the query completes)
            task.setOnSucceeded(e -> {
                Double PRICE = task.getValue();
                priceLabel.setText(String.valueOf(PRICE)); // Update the UI with the retrieved clinic ID
            });

            // Set what happens if the task fails (e.g., error in the query)
            task.setOnFailed(e -> {
                priceLabel.setText("Error retrieving price of Database");
            });

            // Start the task in a background thread
            new Thread(task).start();
        }
        //End of retrieving

        //ADJUSTING PRICE
        @FXML
        private Label priceLabel; // Label to display the price

        private double price = 0.0; // Default price

        @FXML
        public void incrementPrice() {
            if(price < 500 ) {
                price += 5.0;
                updatePriceLabel();
            }
        }

        @FXML
        public void decrementPrice() {
            if (price >= 5 ) { // Ensure price doesn't go below 0
                price -= 5.0;
                updatePriceLabel();
            }
        }

        // Method to update the price label
        private void updatePriceLabel() {
            priceLabel.setText(String.format("%.2f", price)); // Format to two decimal places
        }

        @FXML
        public void Confirmediting()    {
            String pracemail = HelloController.getStoredEmail(); // Get the stored email
            int userId = db.retrieveuserid(pracemail); // Retrieve user ID
            double currentPrice = price; // Get the current price to update

            // Create a new Task for updating the price
            Task<Boolean> updatePriceTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return db.updatePrice(userId, currentPrice); // Perform the update in the background
                }
            };

            // Handle success (when the task completes)
            updatePriceTask.setOnSucceeded(event -> {
                Boolean success = updatePriceTask.getValue();
                if (success) {
                    HelloController.showAlert("Success", "Price updated successfully!", Alert.AlertType.INFORMATION);
                } else {
                    HelloController.showAlert("Error", "Failed to update price!", Alert.AlertType.ERROR);
                }
                // Ensure the connection is closed after completion
            });

            // Handle failure (if an error occurs during execution)
            updatePriceTask.setOnFailed(event -> {
                HelloController.showAlert("Error", "An error occurred while updating the price.", Alert.AlertType.ERROR);
                 // Ensure the connection is closed
            });

            // Run the task in a background thread
            new Thread(updatePriceTask).start();
        }



    }
