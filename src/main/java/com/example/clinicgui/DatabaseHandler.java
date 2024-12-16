package com.example.clinicgui;
import javafx.scene.control.Alert;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import static com.example.clinicgui.HelloController.showAlert;

public class DatabaseHandler {

        // Database configuration
        private static final String URL = "jdbc:mysql://127.0.0.1:3306/Clinic";
        private static final String USER = "root";
        private static final String PASSWORD = "0000";

        // Shared connection
        private Connection connection;

        // Constructor: Opens a connection when the handler is initialized
        public DatabaseHandler() {
            try {
                this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error connecting to the database.");
            }
        }

        // Get connection (for other operations)
        public Connection getConnection() {
            return connection;
        }

        // Method to execute INSERT/UPDATE/DELETE queries
        public int executeUpdate(String query, Object... params) {
            try (PreparedStatement stmt = prepareStatement(query, params)) {
                return stmt.executeUpdate(); // Returns the number of affected rows
            } catch (SQLException e) {
                e.printStackTrace();
                return 0; // Return 0 if an error occurred
            }
        }

        // Method to execute SELECT queries
        public ResultSet executeQuery(String query, Object... params) {
            try {
                PreparedStatement stmt = prepareStatement(query, params);
                return stmt.executeQuery(); // Returns the result set
            } catch (SQLException e) {
                e.printStackTrace();
                return null; // Return null if an error occurred
            }
        }

        // Prepare a statement with parameters
        private PreparedStatement prepareStatement(String query, Object... params) throws SQLException {
            PreparedStatement stmt = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]); // Set parameters dynamically
            }
            return stmt;
        }


        public void reconnect() {
           try {
                  if (this.connection == null || this.connection.isClosed()) {
                         // Reopen the connection here (e.g., using DriverManager.getConnection())
                         this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
                  }
           } catch (SQLException e) {
                 e.printStackTrace();
           }
        }
        //End of the traditional Methods

    //ALL METHODS THAT INSERT INTO TABLES ONLY

        //Method to insert user's email and password into database
        protected int InsertUserInfo(String Email, String Password, String usertype) {
           reconnect();
            String query = "INSERT INTO UserINFO (Email, PasswordHASH, user_type) VALUES (?, ?, ?)";
            int generatedUserID = -1; // Default value if no key is generated
            try (Connection connection = this.getConnection(); // Use existing connection
                 PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                // Set parameters
                stmt.setString(1, Email);
                stmt.setString(2, Password);
                stmt.setString(3, usertype);

                // Execute the update
                int rowsAffected = stmt.executeUpdate();

                if(rowsAffected > 0) showAlert("Success","User info inserted", Alert.AlertType.INFORMATION);
                else showAlert("Error","User info Failed", Alert.AlertType.ERROR);

                // Retrieve the generated key if the insertion was successful
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            generatedUserID = generatedKeys.getInt(1); // Retrieve the auto-generated UserID
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return generatedUserID; // Return the UserID or -1 if none was generated
        }

        // Method to insert patient's info into database
        protected void insertPatient( int id,int userid ,String name, int age, String gender, String PhoneNUM  ) {
            reconnect();
             String query = "INSERT INTO Patient (PatientID,UserID,PatientName, PatientAge, Gender, ContactNUM  ) VALUES (?,?,?,?,?,?)";
              new DatabaseHandler();
             int rowsAffected = executeUpdate(query,id,userid, name, age, gender,PhoneNUM );
            if(rowsAffected > 0) showAlert("Success","Patient info inserted", Alert.AlertType.INFORMATION);
            else showAlert("Error","Patient info Failed", Alert.AlertType.ERROR);
        }

        // Method to insert practitioner's table in the database
        protected void insertPractitioner( int practitionerID , String PractitionerName , String Department , int UserId ) {
            reconnect();

            String query = "INSERT INTO Practitioner (PractitionerID,PractitionerName, Department, UserID) VALUES (?,?,?,?)";
             new DatabaseHandler();
            int rowsAffected = executeUpdate(query,practitionerID , PractitionerName , Department , UserId );
            if(rowsAffected > 0) showAlert("Success","Practitioner info inserted", Alert.AlertType.INFORMATION);
            else showAlert("Error","Practitioner info Failed", Alert.AlertType.ERROR);
        }

       //insert into clinic database //removed close connection from here
        protected void insertClinic( int ClinicID , String Address , String PhoneNUM , String ClinicEmail, double price , int userid ) {
            reconnect();

            String query = "INSERT INTO Clinic (ClinicID , Address , PhoneNUM , ClinicEmail , Price , UserID) VALUES (?,?,?,?,?,?)";
               new DatabaseHandler();
               int rowsAffected = executeUpdate(query,ClinicID , Address , PhoneNUM , ClinicEmail , price , userid);
               if(rowsAffected > 0) showAlert("Success","Clinic info inserted", Alert.AlertType.INFORMATION);
               else showAlert("Error","Clinic info Failed", Alert.AlertType.ERROR);
        }

       //insert into appointment
       protected void insertAppointment(int patientID, int practitionerID, LocalDate selectedDate, String selectedTime) {
           reconnect();

           String query = "INSERT INTO Appointments (PatientID, PractitionerID, AppointmentDate, AppointmentTime, Status) VALUES (?, ?, ?, ?, 'pending')";
           DatabaseHandler db = new DatabaseHandler();
           try (Connection connection = db.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {

               // Check if the time string is missing seconds and append ":00" if necessary
               if (selectedTime.length() == 5) { // This checks if the time format is HH:mm
                   selectedTime += ":00"; // Append seconds part as "00"
               }
               reconnect();

               // Now parse the time
               LocalTime time = LocalTime.parse(selectedTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
               String formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

               // Insert appointment
               stmt.setInt(1, patientID);
               stmt.setInt(2, practitionerID);
               stmt.setDate(3, java.sql.Date.valueOf(selectedDate));
               stmt.setTime(4, Time.valueOf(formattedTime));
               stmt.executeUpdate();

               showAlert("Success", "Appointment booked successfully!", Alert.AlertType.INFORMATION);
           } catch (SQLException e) {
               e.printStackTrace();
               showAlert("Error", "Database error: " + e.getMessage(), Alert.AlertType.ERROR);
           } catch (DateTimeParseException e) {
               e.printStackTrace();
               showAlert("Error", "Invalid time format: " + e.getMessage(), Alert.AlertType.ERROR);
           } catch (IllegalArgumentException e) {
               e.printStackTrace();
               showAlert("Error", "Invalid time format: " + e.getMessage(), Alert.AlertType.ERROR);
           }
       }

       //Method to insert the availability data for practitioner
        protected void InsertIntoAvailability(String starttime, String Endtime , String Day , int practitionerid){
            reconnect();

            String query = "INSERT INTO AVAILABILITY (StartTime , EndTime , DayOfTheWeek , practitionerID) VALUES(?,?,?,?) ";
               new DatabaseHandler();
            // Check if the time string is missing seconds and append ":00" if necessary
            if (starttime.length() == 5) { // This checks if the time format is HH:mm
                starttime += ":00"; // Append seconds part as "00"
            }
            // Check if the time string is missing seconds and append ":00" if necessary
            if (Endtime.length() == 5) { // This checks if the time format is HH:mm
                Endtime += ":00"; // Append seconds part as "00"
            }
               int rowsAffected = executeUpdate(query , starttime , Endtime , Day , practitionerid);
               if(rowsAffected > 0) showAlert("Success","Schedule Set", Alert.AlertType.INFORMATION);
               else showAlert("Error","Error Setting Your Schedule", Alert.AlertType.ERROR);
        }

        //END OF INSERTING INTO DATABASE

        //RETRIEVING FROM DATABASE

         //retrieving email and password for login
         protected boolean retrieveEmail(String email ) {
             reconnect();

             String query = "SELECT Email FROM UserINFO WHERE Email = ?";
        try (ResultSet resultSet = executeQuery(query, email)) {
            if (resultSet != null && resultSet.next()) {
                return true ;// Return the email
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return null if no email is found
    }
         protected boolean retrievePassword(String email, String password) {
             reconnect();

             String query = "SELECT PasswordHASH FROM UserINFO WHERE Email = ?";
        try (ResultSet resultSet = executeQuery(query, email)) {
            if (resultSet != null && resultSet.next()) {
                String storedPassword = resultSet.getString("PasswordHASH");
                return storedPassword.equals(password); // Compare the stored password with the provided one
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if no match is found or an error occurs
    }

         public String retrievepatientname(int userid){
            reconnect();
            String query = "SELECT PatientName From Patient WHERE UserID = ?";

             try(ResultSet resultSet = executeQuery(query,userid)){
                 if(resultSet!= null && resultSet.next()){
                     String name = resultSet.getString("PatientName");
                     return name;
                 }
             }catch (SQLException e){
                 e.printStackTrace();
             }
             return "";
         }


         //retrieve userid for multiple use
         protected int retrieveuserid(String email){
             reconnect();
             String query = " SELECT UserID FROM UserINFO Where email=? ";
         try(ResultSet resultSet = executeQuery(query,email)){
            if(resultSet!= null && resultSet.next()){
                int userid = resultSet.getInt("UserID");
                return userid;
            }
         }catch (SQLException e){
            e.printStackTrace();
         }
         return -1;
    }

        //retrieving data from clinic
        public String retrievepracdepartment(int userid){
            reconnect();
            String query = "SELECT Department from practitioner where userid =?";
        try(ResultSet resultSet = executeQuery(query,userid)){
            if(resultSet!= null && resultSet.next()){
                String Department = resultSet.getString("Department");
                return Department;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "not found";
    }
        public String retrievepractname(int userid){
            reconnect();
            String query = "SELECT practitionername from practitioner where userid =? ";
        try(ResultSet resultSet = executeQuery(query,userid)){
            if(resultSet!= null && resultSet.next()){
                String Pracname = resultSet.getString("PractitionerName");
                return Pracname;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "not found!";
    }
        public String retrieveaddress(int userid){
            reconnect();
            String query = "SELECT Address from Clinic where userid =? ";
        try(ResultSet resultSet = executeQuery(query,userid)){
            if(resultSet!= null && resultSet.next()){
                String Addres = resultSet.getString("Address");
                return Addres;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "not found!";
    }
        public String retrieveclinicphone(int userid){
            reconnect();
            String query = "SELECT PhoneNUM From Clinic where userid=?";
        try(ResultSet resultSet = executeQuery(query,userid)){
            if(resultSet!= null && resultSet.next()){
                String phone = resultSet.getString("PhoneNUM");
                return phone;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "not found!";
    }
        public int retrieveclinicID(int userid){
            reconnect();
            String query = "SELECT ClinicID From Clinic where userid=?";
        try(ResultSet resultSet = executeQuery(query,userid)){
            if(resultSet!= null && resultSet.next()){
                int id = resultSet.getInt("ClinicID");
                return id;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
        public String retrieveclinicemail(int userid){
            reconnect();
            String query = "SELECT clinicEmail From Clinic where userid=?";
        try(ResultSet resultSet = executeQuery(query,userid)){
            if(resultSet!= null && resultSet.next()){
                String email = resultSet.getString("clinicEmail");
                return email;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "not found!";
    }
        public double retrieveprice(int userid){
            reconnect();
            String query = "SELECT price From Clinic where userid=?";
        try(ResultSet resultSet = executeQuery(query,userid)){
            if(resultSet!= null && resultSet.next()){
                double price = resultSet.getInt("price");
                return price;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

        //retrieving patient id to insert into appointments
        protected int retrievepatientID(String email){
            reconnect();
            String query = "SELECT p.PatientID   FROM patient p  JOIN userinfo u ON p.UserID = u.UserID  WHERE u.Email = ?";

        try(ResultSet resultSet = executeQuery(query,email)){
            if(resultSet!= null && resultSet.next()){
                int id = resultSet.getInt("PatientID");
                return id;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

       //retrieveng practitionerid to insert into appointments
       protected int retrievepractitionerID(String Name){
           reconnect();
           String query = "SELECT PractitionerID From Practitioner WHERE PractitionerName=?";
        try(ResultSet resultSet = executeQuery(query,Name)){
            if(resultSet!= null && resultSet.next()){
                int id = resultSet.getInt("PractitionerID");
                return id;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    //get doctors by department
    protected List<String> getDoctorsByDepartment(String department) {
        List<String> doctors = new ArrayList<>();
        String query = "SELECT PractitionerName FROM Practitioner WHERE Department = ?";

        try (ResultSet resultSet = executeQuery(query, department)) {
            while (resultSet != null && resultSet.next()) {
                doctors.add(resultSet.getString("PractitionerName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doctors;
    }

       //Retrieving times and date
       protected List<String> getAvailableTimesByDateAndDoctor(String doctorName, LocalDate selectedDate) {
           reconnect();
           List<String> availableTimes = new ArrayList<>();

        // Fetch available times from the database
        List<String> timesFromDatabase = fetchTimesFromDatabase(doctorName, selectedDate);

        // Check each time slot for availability using isTimeSlotAvailable
        for (String time : timesFromDatabase) {
            LocalTime timeSlot = LocalTime.parse(time); // Parse the time string to LocalTime
            if (isTimeSlotAvailable(doctorName, selectedDate, timeSlot)) {
                availableTimes.add(time); // Add only if the time slot is available
            }
        }

        return availableTimes;
    }

    protected List<String> fetchTimesFromDatabase(String doctorName, LocalDate selectedDate) {
        reconnect();
        List<String> availableTimes = new ArrayList<>();

        String query = """
        SELECT a.StartTime, a.EndTime
        FROM availability a
        JOIN practitioner p ON a.practitionerID = p.practitionerID
        WHERE p.practitionername = ?
        AND a.DayOfTheWeek = ?
    """;
        reconnect();

        try (Connection conn = getConnection()) {
            // Validate the connection
            if (conn == null || conn.isClosed() || !conn.isValid(2)) {
                System.err.println("Database connection is invalid or closed.");
                return availableTimes; // Return empty list if connection is invalid
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // Set the parameters
                stmt.setString(1, doctorName);
                stmt.setString(2, selectedDate.getDayOfWeek().toString().toUpperCase()); // Convert to uppercase to match DB format

                // Execute the query
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Time startTime = rs.getTime("StartTime");
                    Time endTime = rs.getTime("EndTime");

                    // Generate all possible time slots between start and end time
                    availableTimes.addAll(generateTimeSlots(startTime, endTime));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching times from the database: " + e.getMessage());
        }

        return availableTimes;
    }


    private List<String> generateTimeSlots(Time startTime, Time endTime) {
        reconnect();
        List<String> timeSlots = new ArrayList<>();
        LocalTime start = startTime.toLocalTime();
        LocalTime end = endTime.toLocalTime();

        while (start.isBefore(end)) {
            timeSlots.add(start.toString()); // Format as needed, e.g., "HH:mm"
            start = start.plusMinutes(30);  // Increment by 30 minutes or desired interval
        }

        return timeSlots;
    }


    protected List<DayOfWeek> getDoctorAvailableDays(String selectedDoctor) {
        reconnect();
        List<DayOfWeek> availableDays = new ArrayList<>();
        String query = """
        SELECT DayOfTheWeek
        FROM Availability a
        JOIN Practitioner p ON a.PractitionerID = p.PractitionerID
        WHERE p.PractitionerName = ?
        """;

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, selectedDoctor);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String dayOfWeekString = resultSet.getString("DayOfTheWeek");
                try {
                    // Convert the day string from the database to a DayOfWeek enum
                    DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekString.toUpperCase());
                    availableDays.add(dayOfWeek);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid day of week in database: " + dayOfWeekString);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableDays;
    }


    //END OF RETRIEVING FROM DATABASE

    //BOOLEAN METHODS TO CHECK DATABASE

                   //checker that clinic is empty for the user
                   protected boolean isclinicempty(int userId) {
                       reconnect();
                       String query = "SELECT ClinicID, Address, PhoneNUM, ClinicEmail FROM clinic WHERE userid in(SELECT UserID FROM USERINFO WHERE UserID =?)";

                           try (Connection conn = getConnection();
                                PreparedStatement stmt = conn.prepareStatement(query)) {

                                  stmt.setInt(1, userId); // Set the userid parameter
                                   ResultSet rs = stmt.executeQuery();

                                  if (!rs.next()) {
                                      return true; // Return true since the clinic data is "empty"
                                  }

                               // Explicitly check each column for null or empty string
                                  boolean isClinicIDNullOrEmpty = rs.getObject("ClinicID") == null || rs.getString("ClinicID").trim().isEmpty();
                                  boolean isAddressNullOrEmpty = rs.getObject("Address") == null || rs.getString("Address").trim().isEmpty();
                                  boolean isPhoneNumNullOrEmpty = rs.getObject("PhoneNUM") == null || rs.getString("PhoneNUM").trim().isEmpty();
                                  boolean isClinicEmailNullOrEmpty = rs.getObject("ClinicEmail") == null || rs.getString("ClinicEmail").trim().isEmpty();

                                  /*System.out.println("ClinicID is null or empty: " + isClinicIDNullOrEmpty);
                                  System.out.println("Address is null or empty: " + isAddressNullOrEmpty);
                                  System.out.println("PhoneNUM is null or empty: " + isPhoneNumNullOrEmpty);
                                  System.out.println("ClinicEmail is null or empty: " + isClinicEmailNullOrEmpty);*/
                                  // Return true if any column is null or empty
                                  return isClinicIDNullOrEmpty || isAddressNullOrEmpty || isPhoneNumNullOrEmpty || isClinicEmailNullOrEmpty;

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                           return false; // Return false if an error occurs
                   }

                   //checking if time slot available
                   private boolean isTimeSlotAvailable(String doctorName, LocalDate selectedDate, LocalTime timeSlot) {
                       reconnect();
                       String query = """
        SELECT COUNT(*)
        FROM Appointments
        WHERE PractitionerID = (SELECT PractitionerID FROM Practitioner WHERE PractitionerName = ?)
        AND AppointmentDate = ?
        AND AppointmentTime = ?
    """;
        new DatabaseHandler();
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, doctorName);
            stmt.setDate(2, java.sql.Date.valueOf(selectedDate));
            stmt.setTime(3, Time.valueOf(timeSlot));
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    //END OF BOOLEAN METHODS

         //uptade price
         protected boolean updatePrice(int userId, double price) {
             reconnect();
             String query = "UPDATE Clinic SET price = ? WHERE UserID = ?";
              new DatabaseHandler();
             if (getConnection() != null ) {
                 int rowsAffected =executeUpdate(query, price, userId);
                 return rowsAffected > 0;
             } else {
                 System.out.println("Database connection is closed.");
                 return false;
             }
         }

}


