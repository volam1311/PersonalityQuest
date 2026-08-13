module com.example.personalityquest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.desktop;


    opens com.example.personalityquest to javafx.fxml;
    exports com.example.personalityquest;
}