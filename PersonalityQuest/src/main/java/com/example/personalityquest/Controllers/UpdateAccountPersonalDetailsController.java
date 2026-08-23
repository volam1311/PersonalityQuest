package com.example.personalityquest.Controllers;

import com.example.personalityquest.*;
import com.example.personalityquest.Managers.EmailManager;
import com.example.personalityquest.Managers.SystemManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class UpdateAccountPersonalDetailsController implements Initializable {

    protected final static String saveQuery =
            "UPDATE Accounts" +
            " SET email = ?, userName = ?, firstName = ?, lastName = ?" +
            "WHERE email = ?";


    @FXML
    private TextField emailEntry, userNameEntry, firstNameEntry,
            lastNameEntry;

    @FXML
    private Label Message;
    // This string will be set to the currently logged in to account once we have that setup
    private String oldEmail;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        oldEmail = SystemManager.CurrentAccount.currentEmail;

        // attempt to populate the entry fields with the users current account details
        try {
            PopulateEntryFields();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * Attempts to save updated changes made to the users account details
    * */
    @FXML
    public void OnSaveAndExit() throws SQLException {
        if (!isValidUpdate()){
            Message.setText("Account update Invalid");
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

        Message.setText("Updated Details");
        System.out.println("Updated Details");
    }

    /*
     * Returns to previous screen without updating user details
     * */
    public void OnExit(){

    }
    /*
     * Checks to see if the given account update is valid
     * */
    private boolean isValidUpdate() throws SQLException {

        /*
        * Does the new email already exist and is the email being updated
        * */
        if (EmailManager.DoesAccountWithEmailExist(emailEntry.getText()) && !Objects.equals(oldEmail, emailEntry.getText())){
            Message.setText("Email of " + emailEntry.getText() + " Already exists. Please Use Another");
            System.out.println("Email of " + emailEntry.getText() + " Already exists");
            return false;
        }

        return true;
    }
    
    /*
    * Populates the entry fields with their current values
    * */
    private boolean PopulateEntryFields() throws Exception {
        EmailDetails emailDetails = EmailManager.GetDetailsForEmail(oldEmail);

        if (Objects.equals(emailDetails, null)){
            System.out.println("Account doesnt exist with the given email " + oldEmail);
            return false;
        }

        emailEntry.setText(emailDetails.getEmail());
        userNameEntry.setText(emailDetails.getUserName());
        firstNameEntry.setText(emailDetails.getFirstName());
        lastNameEntry.setText(emailDetails.getLastName());

        return true;
    }
}
