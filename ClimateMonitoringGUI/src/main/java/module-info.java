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

    opens it.uninsubria.climatemonitoringgui to javafx.fxml;
    exports it.uninsubria.climatemonitoringgui;
    exports it.uninsubria.climatemonitoringgui.controller.loginview;
    opens it.uninsubria.climatemonitoringgui.controller.loginview to javafx.fxml;
    exports it.uninsubria.climatemonitoringgui.controller.registrazione;
    opens it.uninsubria.climatemonitoringgui.controller.registrazione to javafx.fxml;
    exports it.uninsubria.climatemonitoringgui.controller.scene;
    opens it.uninsubria.climatemonitoringgui.controller.scene to javafx.fxml;
    exports it.uninsubria.climatemonitoringgui.controller.parametroclimatico;
    opens it.uninsubria.climatemonitoringgui.controller.parametroclimatico to javafx.fxml;
    exports it.uninsubria.climatemonitoringgui.controller.mainscene;
    opens it.uninsubria.climatemonitoringgui.controller.mainscene to javafx.fxml;
}