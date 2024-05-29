package com.frogrilla.mcskinlibrarian;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import com.google.gson.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.Instant;
import java.util.*;

public class LibrarianController {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private List<SkinData> customSkins = new ArrayList<>();
    private List<SkinData> deletedSkins = new ArrayList<>();
    private File skinFile;
    private boolean loaded = false;

    @FXML
    private ListView<String> skinListView;
    @FXML
    private TextField skinName;
    @FXML
    private ImageView modelImage;
    @FXML
    private ImageView skinImage;
    @FXML
    private Pane rightSpacer;
    @FXML
    private Pane leftSpacer;

    public void initialize() {
        HBox.setHgrow(rightSpacer, Priority.SOMETIMES);
        HBox.setHgrow(leftSpacer, Priority.SOMETIMES);

        skinName.textProperty().addListener((observable, a, b) -> {
            int i = skinListView.getSelectionModel().getSelectedIndex();
            if(i < 0) return;

            if(!skinListView.getItems().get(i).equals(b)) skinListView.getItems().set(i, b);
        });

        skinListView.getSelectionModel().selectedIndexProperty().addListener((observable, a, b) -> {
            updateView();
        });
    }
    @FXML
    protected void onSelectFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Custom skin JSON", "launcher_custom_skins.json"));
        skinFile = fileChooser.showOpenDialog(LibrarianApplication.pStage);
        BufferedReader reader = new BufferedReader(new FileReader(skinFile));
        JsonObject object = gson.fromJson(reader, JsonObject.class).getAsJsonObject("customSkins");
        customSkins.clear();
        object.entrySet().forEach(entry -> {
            SkinData newData = gson.fromJson(entry.getValue(), SkinData.class);
            customSkins.add(newData);
        });
        customSkins.sort(new SkinDataComparator());
        skinListView.getItems().clear();
        customSkins.forEach(skinData -> skinListView.getItems().add(Objects.equals(skinData.name, "") ? "<unnamed skin>" : skinData.name));
        reader.close();

        loaded = true;
    }

    @FXML
    protected void onSaveButton() throws IOException {
        if(!loaded) return;
        JsonObject skinDataMap = new JsonObject();
        customSkins.forEach(skin -> {
            int i = customSkins.indexOf(skin);
            skin.created = Instant.now().minusSeconds(i).toString();
            skin.name = skinListView.getItems().get(i);
            skinDataMap.add(skin.id, gson.toJsonTree(skin));
        });

        JsonObject skinObject = new JsonObject();
        skinObject.add("customSkins", skinDataMap);
        FileWriter writer = new FileWriter(skinFile);
        writer.write(gson.toJson(skinObject));
        writer.close();
    }

    @FXML
    protected void onUpButton(){
        //if(!loaded) return;
        int i = skinListView.getSelectionModel().getSelectedIndex();
        if(i <= 0) return;

        SkinData swappedData = customSkins.get(i-1);
        customSkins.set(i-1, customSkins.get(i));
        customSkins.set(i, swappedData);

        String swappedName = skinListView.getItems().get(i-1);
        skinListView.getItems().set(i-1, skinListView.getItems().get(i));
        skinListView.getItems().set(i, swappedName);

        skinListView.scrollTo(i-1);
        skinListView.getSelectionModel().select(i-1);
    }

    @FXML
    protected void onDownButton(){
        //if(!loaded) return;
        int i = skinListView.getSelectionModel().getSelectedIndex();
        if(i < 0 || i >= skinListView.getItems().size()-1) return;

        SkinData swappedData = customSkins.get(i+1);
        customSkins.set(i+1, customSkins.get(i));
        customSkins.set(i, swappedData);

        String swappedName = skinListView.getItems().get(i+1);
        skinListView.getItems().set(i+1, skinListView.getItems().get(i));
        skinListView.getItems().set(i, swappedName);

        skinListView.scrollTo(i+1);
        skinListView.getSelectionModel().select(i+1);
    }

    @FXML
    protected void onDeleteButton(){
        int i = skinListView.getSelectionModel().getSelectedIndex();
        if(i < 0) return;

        deletedSkins.add(customSkins.get(i));
        customSkins.remove(i);
        skinListView.getItems().remove(i);
        skinListView.getSelectionModel().select(Math.min(i, skinListView.getItems().size()-1));

        updateView();
    }

    @FXML
    protected void onDupliacteButton(){
        int i = skinListView.getSelectionModel().getSelectedIndex();
        if(i < 0) return;

        SkinData rootData = customSkins.get(i);
        SkinData cloneData = new SkinData();

        String duplicateName = skinListView.getItems().get(i);
        if(!duplicateName.endsWith(" [duplicate]")){
            duplicateName += " [duplicate]";
        }

        cloneData.name = duplicateName;
        cloneData.textureId = "";
        cloneData.skinImage = rootData.skinImage;
        cloneData.modelImage = rootData.modelImage;
        cloneData.slim = rootData.slim;
        cloneData.updated = rootData.updated;
        cloneData.created = rootData.created;
        cloneData.capeId = rootData.capeId;

        int nextID = 0;
        for(int j = 0; j < customSkins.size()-1; j++){
            SkinData skinData = customSkins.get(j);
            String digits = skinData.id.substring(5);
            try{
                int num = Integer.parseInt(digits);
                nextID = Math.max(nextID, num+1);
            }
            catch (NumberFormatException e){
                continue;
            }
        }

        cloneData.id = String.format("skin_%d", nextID);

        customSkins.add(i+1, cloneData);
        skinListView.getItems().add(i+1, duplicateName);
        skinListView.getSelectionModel().select(i+1);
        updateView();
    }

    @FXML
    protected void onRecoverButton() {
    }

    public void updateView(){
        int i = skinListView.getSelectionModel().getSelectedIndex();
        if(i < 0){
            skinName.setText("");
            modelImage.setVisible(false);
            skinImage.setVisible(false);
            skinName.setDisable(true);
            return;
        }

        skinName.setText(skinListView.getItems().get(i));
        byte[] modelBytes = Base64.getDecoder().decode(customSkins.get(i).modelImage.replace("data:image/png;base64,", "").replace("\\u003d", ""));
        Image imageM = new Image(new ByteArrayInputStream(modelBytes));
        modelImage.setImage(imageM);

        byte[] skinBytes = Base64.getDecoder().decode(customSkins.get(i).skinImage.replace("data:image/png;base64,", "").replace("\\u003d", ""));
        Image imageS = new Image(new ByteArrayInputStream(skinBytes));
        skinImage.setImage(imageS);

        modelImage.setVisible(true);
        skinImage.setVisible(true);
        skinName.setDisable(false);
    }

    public void processKeyPress(KeyEvent event){
        switch(event.getCode()){
            case Q -> onUpButton();
            case E -> onDownButton();
            case X ->{
                if (event.isControlDown()) onDeleteButton();
            }
            case D -> {
                if (event.isControlDown()) onDupliacteButton();
            }
            case ENTER -> {
                if (skinName.isFocused()) skinListView.requestFocus();
            }
            case R -> {
                if(event.isControlDown()) skinName.requestFocus();
            }
            default -> {}
        }
    }
}