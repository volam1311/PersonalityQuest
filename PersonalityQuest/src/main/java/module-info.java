module com.example.personalityquest {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.personalityquest to javafx.fxml;
    exports com.example.personalityquest;
    exports com.example.personalityquest.Controller;
    opens com.example.personalityquest.Controller to javafx.fxml;
    exports com.example.personalityquest.Main;
    opens com.example.personalityquest.Main to javafx.fxml;
    exports com.example.personalityquest.Model;
    opens com.example.personalityquest.Model to javafx.fxml;
}