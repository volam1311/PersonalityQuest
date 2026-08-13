package com.example.personalityquest.Controllers;

import com.example.personalityquest.Applications.AccountCreationApplication;
import com.example.personalityquest.SQLite;
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
            "Accounts WHERE email = ? AND password = ?";
    @FXML
    private TextField emailEntry, passwordEntry;

    @FXML
    private void OnSignIn() throws SQLException {
        if (!isValidSignIn()) {
            System.out.println("Account Sign In Invalid");
            return;
        }
        System.out.println("Sign in complete");
    }

    private boolean isValidSignIn() throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(checkForExistingAccount);
        statement.setString(1, emailEntry.getText());
        statement.setString(2, passwordEntry.getText());
        ResultSet rs = statement.executeQuery();

        // count rs set to see if there is a result
        int count = 0;
        while (rs.next()){
            count++;
        }
        if (count != 1){
            System.out.println("Email or Password is incorrect");
            return false;
        }


        return true;
    }
    @FXML
    private void OnSignUp(MouseEvent event) throws IOException {
        Stage currentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        AccountCreationApplication.launch(currentStage);
    }
}
