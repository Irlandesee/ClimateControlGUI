module it.uninsubria.climatemonitoringgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.postgresql.jdbc;
    requires java.sql;
    requires javafx.graphics;

    opens it.climatemonitoring to javafx.fxml;
    exports it.climatemonitoring;

    exports it.climatemonitoring.controller.loginview;
    opens it.climatemonitoring.controller.loginview to javafx.fxml;

    exports it.climatemonitoring.controller.registrazione;
    opens it.climatemonitoring.controller.registrazione to javafx.fxml;

    opens it.climatemonitoring.controller.parametroclimatico to javafx.fxml;
    exports it.climatemonitoring.controller.parametroclimatico;

    exports it.climatemonitoring.controller.mainscene;
    opens it.climatemonitoring.controller.mainscene to javafx.fxml;

    exports it.climatemonitoring.controller.operatore;
    opens it.climatemonitoring.controller.operatore to javafx.fxml;

    opens it.climatemonitoring.datamodel.operatore;
    exports it.climatemonitoring.datamodel.operatore to javafx.base;


    opens it.climatemonitoring.datamodel.areaInteresse;
    exports it.climatemonitoring.datamodel.areaInteresse to javafx.fxml;

    opens it.climatemonitoring.datamodel.city;
    exports it.climatemonitoring.datamodel.city to javafx.base;

    opens it.climatemonitoring.datamodel.centroMonitoraggio;
    exports it.climatemonitoring.datamodel.centroMonitoraggio to javafx.base;


    opens it.climatemonitoring.datamodel.parametroClimatico;
    exports it.climatemonitoring.datamodel.parametroClimatico to javafx.base;

    opens it.climatemonitoring.controller.dialog;
    exports it.climatemonitoring.controller.dialog;

}