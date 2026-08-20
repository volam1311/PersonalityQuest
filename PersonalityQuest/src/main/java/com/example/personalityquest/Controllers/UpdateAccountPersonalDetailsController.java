package com.example.personalityquest.Controllers;

import com.example.personalityquest.Hashing;
import com.example.personalityquest.SQLite;
import com.example.personalityquest.SystemManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class UpdateAccountPersonalDetailsController {

    protected final static String saveQuery =
            "UPDATE Accounts" +
            " SET email = ?, userName = ?, firstName = ?, lastName = ?" +
            "WHERE email = ?";

    protected final static String accountExists =
            "SELECT * FROM Accounts WHERE email = ?";

    @FXML
    private TextField emailEntry, userNameEntry, firstNameEntry,
            lastNameEntry;

    /*
    * This string will be set to the currently logged in to account once we have that setup
    * */
    private String oldEmail;
    public void OnSaveAndExit() throws SQLException {
        oldEmail = SystemManager.CurrentAccount.currentEmail;
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

    public void OnExit(){

    }

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
    private void ExitScreen(){

    }

}
