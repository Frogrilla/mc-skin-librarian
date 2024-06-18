package com.frogrilla.mcskinlibrarian;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class RecoverController {
    @FXML
    protected ListView<String> recoverList;

    public LibrarianController library;

    public void initialize(){
        VBox.setVgrow(recoverList, Priority.ALWAYS);
    }
    @FXML
    protected void addButton(){
        int i = recoverList.getSelectionModel().getSelectedIndex();
        if(i < 0) return;

        library.customSkins.add(library.deletedSkins.get(i));
        library.skinListView.getItems().add(library.deletedSkins.get(i).name);
        library.deletedSkins.remove(i);
        recoverList.getItems().remove(i);
    }

    @FXML
    protected void deleteButton(){
        int i = recoverList.getSelectionModel().getSelectedIndex();
        if(i < 0) return;

        library.deletedSkins.remove(i);
        recoverList.getItems().remove(i);
    }

    @FXML
    protected void clearButton(){
        library.deletedSkins.clear();
        recoverList.getItems().clear();
    }
    public void processKeyPress(KeyEvent event){
        switch(event.getCode()){
            case E:
                addButton();
                break;
            case X:
                if(event.isControlDown()) deleteButton();
                break;
            case C:
                if(event.isControlDown()) clearButton();
                break;
        }
    }
}
