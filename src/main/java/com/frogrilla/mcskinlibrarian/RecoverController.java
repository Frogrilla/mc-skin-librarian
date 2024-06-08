package com.frogrilla.mcskinlibrarian;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.Objects;

public class RecoverController {
    @FXML
    protected ListView<String> recoverList;

    public LibrarianController library;
    public boolean open = false;

    public void shutdown(Event event){
        open = false;
    }

    public void initialize(){
        open = true;
        HBox.setHgrow(recoverList, Priority.SOMETIMES);
    }
    @FXML
    protected void addButton(){
        int i = recoverList.getSelectionModel().getSelectedIndex();
        if(i < 0) return;

        System.out.println("AHDJADADKSH SHIMBLES RECOVER");

        if(addDeleted(i)){
            library.deletedSkins.remove(i);
            recoverList.getItems().remove(i);
        }
    }
    public void processKeyPress(KeyEvent event){
        if (Objects.requireNonNull(event.getCode()) == KeyCode.E) {
            addButton();
        }
    }

    public boolean addDeleted(int i){
        if(i >= 0 && i < library.deletedSkins.size()){
            library.customSkins.add(library.deletedSkins.get(i));
            library.skinListView.getItems().add(library.deletedSkins.get(i).name);
            return true;
        }
        return false;
    }
}
