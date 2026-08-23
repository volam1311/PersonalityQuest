package com.example.personalityquest.Controllers;

import com.example.personalityquest.Hashing;
import com.example.personalityquest.SQLite;
import com.example.personalityquest.SystemManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class UpdateAccountPersonalDetailsController implements Initializable {

    protected final static String saveQuery =
            "UPDATE Accounts" +
            " SET email = ?, userName = ?, firstName = ?, lastName = ?" +
            "WHERE email = ?";

    protected final static String accountExists =
            "SELECT * FROM Accounts WHERE email = ?";

    @FXML
    private TextField emailEntry, userNameEntry, firstNameEntry,
            lastNameEntry;

    // This string will be set to the currently logged in to account once we have that setup
    private String oldEmail;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        oldEmail = SystemManager.CurrentAccount.currentEmail;

        // attempt to populate the entry fields with the users current account details
        try {
            PopulateEntryFields();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * Attempts to save updated changes made to the users account details
    * */
    @FXML
    public void OnSaveAndExit() throws SQLException {
        if (!isValidUpdate()){
            System.out.println("Account update Invalid");
            return;
        }

        /*
        * Failsafe for if somehow this variable does not become set
        * */
        if (Objects.equals(oldEmail, "")){
            System.out.println("Old email is null");
            return;
        }
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(saveQuery);

        // assign parameters
        statement.setString(1, emailEntry.getText());
        statement.setString(2, userNameEntry.getText());
        statement.setString(3, firstNameEntry.getText());
        statement.setString(4, lastNameEntry.getText());
        statement.setString(5, oldEmail);
        // execute statement
        statement.executeUpdate();

        System.out.println("Updated Details");
    }
    /*
     * Exits without updating users details
     * */
    public void OnExit(){

    }
    /*
     * Checks to see if the given account update is valid
     * */
    private boolean isValidUpdate() throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(accountExists);
        // assign parameters
        statement.setString(1, emailEntry.getText());

        int count = 0;

        ResultSet rs = statement.executeQuery();
        while (rs.next()){
            count++;
        }

        /*
        * Does the new email already exist and is the email being updated
        * */
        if (count >= 1 && !Objects.equals(oldEmail, emailEntry.getText())){
            System.out.println("Email of " + emailEntry.getText() + " Already exists");
            return false;
        }

        return true;
    }

    /*
     * Returns to previous screen
     * */
    private void ExitScreen(){

    }

    /*
    * Populates the entry fields with their current values
    * */
    private boolean PopulateEntryFields() throws SQLException{
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(accountExists);
        // assign parameters
        statement.setString(1, oldEmail);

        ResultSet rs = statement.executeQuery();

        int count = 0;
        // populates the fields
        while (rs.next()){
            count ++;
            emailEntry.setText(rs.getString("email"));
            userNameEntry.setText(rs.getString("userName"));
            firstNameEntry.setText(rs.getString("firstName"));
            lastNameEntry.setText(rs.getString("lastName"));
        }

        // User has somehow reached edit account details without logging into an account
        // or with an invalid account
        if (count == 0){
            System.out.println("Account doesnt exist with the given email " + oldEmail);
            return false;
        }

        return true;
    }
}
