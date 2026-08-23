package com.example.personalityquest.Controllers;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate the given fxml with the users weekly tasks,
        // their current questline and their progress graph
        PopulateQuestline();
        PopulateWeeklyTasks();
        PopulateProgressGraph();
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
        System.out.println("Populated Weekly Tasks");
    }


    /*
     * Populates the graph fxml with the users data
     * */
    private void PopulateProgressGraph(){
        System.out.println("Populated Progress Graph");
    }

}
