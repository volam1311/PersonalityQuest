package com.example.personalityquest.Controllers;

import com.example.personalityquest.Applications.AccountCreationApplication;
import com.example.personalityquest.Applications.DashboardApplication;
import com.example.personalityquest.Applications.UpdateAccountPasswordApplication;
import com.example.personalityquest.Applications.UpdateAccountPersonalDetailsApplication;
import com.example.personalityquest.Managers.EmailManager;
import com.example.personalityquest.Managers.HashingManager;
import com.example.personalityquest.Managers.PasswordManager;
import com.example.personalityquest.SQLite;
import com.example.personalityquest.Managers.SystemManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountSignInController {
    @FXML
    private TextField emailEntry, passwordEntry;

    @FXML
    private Label Message;
    /*
     * When "Sign In" Button is clicked
     * */
    @FXML
    private void OnSignIn() throws SQLException, IOException {
        if (!isValidSignIn()) {
            System.out.println("Account Sign In Invalid");
            return;
        }

        /*
         * Set currently logged in account
         * */
        SystemManager.CurrentAccount.currentEmail = emailEntry.getText();

        UpdateAccountPasswordApplication.launch((Stage)emailEntry.getScene().getWindow());


        System.out.println("Sign in complete");
    }

    /*
     * Checks to see if the given email and password will work as a valid
     * sign in
     * */
    private boolean isValidSignIn() throws SQLException {
        // Does the email exist and does the password match the email
        if (!EmailManager.DoesAccountWithEmailExist(emailEntry.getText())
        || !PasswordManager.isPasswordForEmail(emailEntry.getText(), passwordEntry.getText())){
            Message.setText("Email or Password Was entered incorrectly");
            return false;
        }

        Message.setText("Login Successful");
        return true;
    }

    /*
    * When "Click here to Sign Up" label is clicked
    * */
    @FXML
    private void OnSignUp(MouseEvent event) throws IOException {
        Stage currentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        AccountCreationApplication.launch(currentStage);
    }
}
