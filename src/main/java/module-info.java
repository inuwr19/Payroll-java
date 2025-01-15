module com.ivanka {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.ivanka to javafx.fxml;
    opens com.ivanka.controllers to javafx.fxml;
    opens com.ivanka.models to javafx.base;
    
    exports com.ivanka;
    exports com.ivanka.controllers;
}
