package com.example.personalityquest.Controllers;

import javafx.fxml.FXML;

public class NavBarController {

    /*
    * Profile Button Clicked
    * */
    @FXML
    public void OnProfile(){
        System.out.println("Going to profile scene");
    }

    /*
     * Dashboard Button Clicked
     * */
    @FXML
    public void OnDashboard(){
        System.out.println("Going to dashboard scene");
    }

    /*
     * Settings Button Clicked
     * */
    @FXML
    public void OnSettings(){
        System.out.println("Going to settings scene");
    }

    /*
     * Quiz Button Clicked
     * */
    @FXML
    public void OnQuiz(){
        System.out.println("Going to Quiz scene");
    }
}
