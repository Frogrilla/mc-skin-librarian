package com.frogrilla.mcskinlibrarian;

import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ShortcutController {

    @FXML
    private FlowPane libraryTab;
    @FXML
    private FlowPane recoverTab;

    public void initialize(){
        VBox.setVgrow(libraryTab, Priority.ALWAYS);
        VBox.setVgrow(recoverTab, Priority.ALWAYS);
    }
}
