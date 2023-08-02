module it.uninsubria.climatemonitoringgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens it.uninsubria to javafx.fxml;
    exports it.uninsubria;
    exports it.uninsubria.controller.loginview;
    opens it.uninsubria.controller.loginview to javafx.fxml;
    exports it.uninsubria.controller.registrazione;
    opens it.uninsubria.controller.registrazione to javafx.fxml;
    exports it.uninsubria.controller.scene;
    opens it.uninsubria.controller.scene to javafx.fxml;
    exports it.uninsubria.controller.parametroclimatico;
    opens it.uninsubria.controller.parametroclimatico to javafx.fxml;
    exports it.uninsubria.controller.mainscene;
    opens it.uninsubria.controller.mainscene to javafx.fxml;
}