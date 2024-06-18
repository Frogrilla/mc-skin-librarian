package com.frogrilla.mcskinlibrarian;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class RecoverController {
    @FXML
    protected ListView<String> recoverList;
    @FXML
    protected ImageView recoverImage;

    public LibrarianController library;

    public void initialize(){
        VBox.setVgrow(recoverList, Priority.ALWAYS);
        recoverList.getSelectionModel().selectedIndexProperty().addListener((observable, a, b) -> {
            int i = recoverList.getSelectionModel().getSelectedIndex();
            if(i < 0){
                recoverImage.setVisible(false);
                return;
            }
            byte[] skinBytes = Base64.getDecoder().decode(library.deletedSkins.get(i).skinImage.replace("data:image/png;base64,", "").replace("\\u003d", ""));
            Image imageS = new Image(new ByteArrayInputStream(skinBytes));
            recoverImage.setImage(imageS);
            recoverImage.setVisible(true);
        });
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
