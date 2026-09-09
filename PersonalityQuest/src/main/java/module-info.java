module com.example.personalityquest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.desktop;
    requires java.sql;
    requires bcrypt;
    requires jdk.jshell;

    opens com.example.personalityquest to javafx.fxml;
    exports com.example.personalityquest;
    exports com.example.personalityquest.Controllers;
    opens com.example.personalityquest.Controllers to javafx.fxml;
    exports com.example.personalityquest.Applications;
    opens com.example.personalityquest.Applications to javafx.fxml;
    exports com.example.personalityquest.Managers;
    opens com.example.personalityquest.Managers to javafx.fxml;
    exports com.example.personalityquest.DataClasses;
    opens com.example.personalityquest.DataClasses to javafx.fxml;
    exports com.example.personalityquest.DAO;
    opens com.example.personalityquest.DAO to javafx.fxml;
    exports com.example.personalityquest.Model;
    opens com.example.personalityquest.Model to javafx.fxml;
}