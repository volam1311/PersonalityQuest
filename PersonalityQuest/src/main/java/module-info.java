module com.example.personalityquest {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.personalityquest to javafx.fxml;
    exports com.example.personalityquest;
}