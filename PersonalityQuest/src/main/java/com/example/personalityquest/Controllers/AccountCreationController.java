package com.example.personalityquest.Controllers;

import com.example.personalityquest.Applications.AccountSignInApplication;
import com.example.personalityquest.SQLite;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class AccountCreationController {

    private final String sqlSignUp = "INSERT INTO Accounts (email, userName, firstName, lastName, password) VALUES(?, ?, ?, ?, ?)";

    @FXML
    private TextField emailEntry, userNameEntry, firstNameEntry,
            lastNameEntry, passwordEntry, reEnterPasswordEntry;

    @FXML
    private void OnSignUp() throws SQLException {
        if (!IsValidSignUp()) {
            System.out.println("Account creation Invalid");
            return;
        }

        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(sqlSignUp);
        // assign parameters
        statement.setString(1, emailEntry.getText());
        statement.setString(2, userNameEntry.getText());
        statement.setString(3, firstNameEntry.getText());
        statement.setString(4, lastNameEntry.getText());
        statement.setString(5, passwordEntry.getText());

        // execute statement
        statement.executeUpdate();

        System.out.println("Account created");
    }

    private boolean IsValidSignUp(){
        // passwords do not match
        if (!Objects.equals(passwordEntry.getText(), reEnterPasswordEntry.getText())){
            System.out.println("Passwords do not match");
            return false;
        }

        // fields are empty
        if (Objects.equals(emailEntry.getText(), "")){
            System.out.println("Email is empty");
            return false;
        }
        else if (Objects.equals(userNameEntry.getText(), "")){
            System.out.println("UserName is empty");
            return false;
        }
        else if (Objects.equals(firstNameEntry.getText(), "")){
            System.out.println("FirstName is empty");
            return false;
        }
        else if (Objects.equals(lastNameEntry.getText(), "")){
            System.out.println("LastName is empty");
            return false;
        }
        else if (Objects.equals(passwordEntry.getText(), "")){
            System.out.println("Password is empty");
            return false;
        }

        // account creation valid
        return true;
    }
    @FXML
    private void OnSignIn(MouseEvent event) throws IOException {
        Stage currentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        AccountSignInApplication.launch(currentStage);
    }
}
