package com.example.personalityquest.Controllers;

import com.example.personalityquest.DataClasses.EmailDetails;
import com.example.personalityquest.Managers.EmailManager;
import com.example.personalityquest.Managers.SystemManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label welcomeMessage;
    @FXML
    private ListView weeklyTasks;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate the given fxml with the users weekly tasks,
        // their current questline and their progress graph
        try {
            SetWelcome();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PopulateQuestline();
        PopulateWeeklyTasks();
        PopulateProgressGraph();
    }

    /*
     * Sets the welcome name
     * */
    private void SetWelcome() throws Exception {
        EmailDetails emailDetails = EmailManager.GetDetailsForEmail(SystemManager.CurrentAccount.currentEmail);
        welcomeMessage.setText("Welcome back, " + emailDetails.getFirstName());
        System.out.println("Set Welcome Name");
    }

    /*
    * Populates the Questline fxml with the users data
    * */
    private void PopulateQuestline(){
        System.out.println("Populated Questline");
    }

    /*
     * Populates the Weekly tasks fxml with the users data
     * */
    private void PopulateWeeklyTasks(){
        // Need Weekly Quests Before I can do this so this is just dummy data
        weeklyTasks.getItems().addAll("Dummy Task 1", "Dummy Task 2", "Dummy Task 3");
        weeklyTasks.setMinHeight(weeklyTasks.getItems().size() * 24);

        System.out.println("Populated Weekly Tasks");
    }



    /*
     * Populates the graph fxml with the users data
     * */
    private void PopulateProgressGraph(){
        System.out.println("Populated Progress Graph");
    }

}
