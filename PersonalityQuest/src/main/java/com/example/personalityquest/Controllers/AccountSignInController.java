package com.example.personalityquest.Controllers;

import com.example.personalityquest.Applications.AccountCreationApplication;
import com.example.personalityquest.Applications.DashboardApplication;
import com.example.personalityquest.Applications.UpdateAccountPersonalDetailsApplication;
import com.example.personalityquest.Hashing;
import com.example.personalityquest.SQLite;
import com.example.personalityquest.SystemManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountSignInController {
    private final String checkForExistingAccount = "SELECT * FROM " +
            "Accounts WHERE email = ?";
    @FXML
    private TextField emailEntry, passwordEntry;

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

        DashboardApplication.launch((Stage)emailEntry.getScene().getWindow());


        System.out.println("Sign in complete");
    }

    /*
     * Checks to see if the given email and password will work as a valid
     * sign in
     * */
    private boolean isValidSignIn() throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(checkForExistingAccount);

        statement.setString(1, emailEntry.getText());
        ResultSet rs = statement.executeQuery();

        // count rs set to see if there is a result
        int count = 0;
        // holds the last hashed password seen
        String lastPassword = "";

        while (rs.next()){
            count++;
            lastPassword = rs.getString(5);
        }

        // if an account does not exist
        if (count != 1){
            System.out.println("Email or Password is incorrect");
            return false;
        }

        // if the passwords do not match
        if (!Hashing.VerifyHash(lastPassword, passwordEntry.getText())){
            System.out.println("Password is incorrect");
            return false;
        }


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
