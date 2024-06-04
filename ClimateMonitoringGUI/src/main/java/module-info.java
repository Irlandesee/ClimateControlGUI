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

    opens it.uninsubria to javafx.fxml;
    exports it.uninsubria;

    exports it.uninsubria.controller.loginview;
    opens it.uninsubria.controller.loginview to javafx.fxml;

    exports it.uninsubria.controller.registrazione;
    opens it.uninsubria.controller.registrazione to javafx.fxml;

    opens it.uninsubria.controller.parametroclimatico to javafx.fxml;
    exports it.uninsubria.controller.parametroclimatico;

    exports it.uninsubria.controller.mainscene;
    opens it.uninsubria.controller.mainscene to javafx.fxml;

    exports it.uninsubria.controller.operatore;
    opens it.uninsubria.controller.operatore to javafx.fxml;

    opens it.uninsubria.datamodel.operatore;
    exports it.uninsubria.datamodel.operatore to javafx.base;


    opens it.uninsubria.datamodel.areaInteresse;
    exports it.uninsubria.datamodel.areaInteresse to javafx.fxml;

    opens it.uninsubria.datamodel.city;
    exports it.uninsubria.datamodel.city to javafx.base;

    opens it.uninsubria.datamodel.centroMonitoraggio;
    exports it.uninsubria.datamodel.centroMonitoraggio to javafx.base;


    opens it.uninsubria.datamodel.parametroClimatico;
    exports it.uninsubria.datamodel.parametroClimatico to javafx.base;

    opens it.uninsubria.controller.dialog;
    exports it.uninsubria.controller.dialog;

}