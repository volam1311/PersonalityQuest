module com.example.personalityquest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.desktop;
    requires java.sql;
    requires bcrypt;

    opens com.example.personalityquest to javafx.fxml;
    exports com.example.personalityquest;
    exports com.example.personalityquest.Controllers;
    opens com.example.personalityquest.Controllers to javafx.fxml;
    exports com.example.personalityquest.Applications;
    opens com.example.personalityquest.Applications to javafx.fxml;
}