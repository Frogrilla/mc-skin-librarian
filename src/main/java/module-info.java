module com.frogrilla.mcskinlibrarian {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens com.frogrilla.mcskinlibrarian to javafx.fxml;
    exports com.frogrilla.mcskinlibrarian;
}