package com.frogrilla.mcskinlibrarian;

import javafx.fxml.FXML;
import com.google.gson.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;

public class LibrarianController {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public List<SkinData> customSkins = new ArrayList<>();
    public List<SkinData> deletedSkins = new ArrayList<>();
    private File skinFile;
    private boolean loaded = false;
    public RecoverController recoverController;

    private Stage recoverStage;
    private Stage shortcutStage;

    @FXML
    public ListView<String> skinListView;
    @FXML
    private TextField skinName;
    @FXML
    private ImageView modelImage;
    @FXML
    private ImageView skinImage;
    @FXML
    private Pane topSpacer;
    @FXML
    private CheckBox slimBox;
    @FXML
    public Label saveText;

    public void initialize() {
        HBox.setHgrow(topSpacer, Priority.SOMETIMES);
        saveText.setVisible(false);

        skinName.textProperty().addListener((observable, a, b) -> {
            int i = skinListView.getSelectionModel().getSelectedIndex();
            if(i < 0) return;

            String newText = Objects.equals(b, "") ? "<unnamed skin>" : b;

            if(!skinListView.getItems().get(i).equals(newText)){
                skinListView.getItems().set(i, newText);
                customSkins.get(i).name = b;
            }
        });

        skinListView.getSelectionModel().selectedIndexProperty().addListener((observable, a, b) -> {
            System.out.println("AHDJSAHD");
            updateView();
        });
    }
    @FXML
    protected void onSelectFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Custom skin JSON", "launcher_custom_skins.json"));
        skinFile = fileChooser.showOpenDialog(LibrarianApplication.pStage);
        // it literally works with my skin wtf
        // ig
        if(skinFile == null) return;
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

        if(!customSkins.isEmpty()) {
            skinListView.getSelectionModel().select(0);
            updateView();
        }

        loaded = true;
    }

    @FXML
    protected void onSaveButton() throws IOException {
        if(!loaded) return;
        JsonObject skinDataMap = new JsonObject();
        for(int i = 0; i < customSkins.size(); i++){
            SkinData skin = customSkins.get(i);
            skin.created = Instant.now().minusSeconds(i).toString();
            if(Objects.equals(skin.capeId, "")) skin.capeId = null;
            skin.id = "skin_" + (i+1);
            skinDataMap.add(skin.id, gson.toJsonTree(skin));
        };

        JsonObject skinObject = new JsonObject();
        skinObject.add("customSkins", skinDataMap);
        skinObject.addProperty("version", 1);
        FileWriter writer = new FileWriter(skinFile);
        writer.write(gson.toJson(skinObject));
        writer.close();

        saveText.setVisible(true);
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

        saveText.setVisible(false);
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

        saveText.setVisible(false);
    }

    @FXML
    protected void onDeleteButton(){
        int i = skinListView.getSelectionModel().getSelectedIndex();
        if(i < 0) return;

        if(recoverStage != null) recoverController.recoverList.getItems().add(skinListView.getItems().get(i));

        deletedSkins.add(customSkins.get(i));
        customSkins.remove(i);
        skinListView.getItems().remove(i);
        skinListView.getSelectionModel().select(Math.min(i, skinListView.getItems().size()-1));

        updateView();
        saveText.setVisible(false);
    }

    @FXML
    protected void onDuplicateButton(){
        int i = skinListView.getSelectionModel().getSelectedIndex();
        if(i < 0) return;

        SkinData rootData = customSkins.get(i);
        SkinData cloneData = new SkinData();

        String duplicateName = skinListView.getItems().get(i);
//        if(!duplicateName.endsWith(" [duplicate]")){
//            duplicateName += " [duplicate]";
//        }

        cloneData.name = duplicateName;
        cloneData.textureId = "";
        cloneData.skinImage = rootData.skinImage;
        cloneData.modelImage = rootData.modelImage;
        cloneData.slim = rootData.slim;
        cloneData.updated = rootData.updated;
        cloneData.created = rootData.created;
        cloneData.capeId = rootData.capeId;

//        int nextID = 0;
//        for(int j = 0; j < customSkins.size()-1; j++){
//            SkinData skinData = customSkins.get(j);
//            String digits = skinData.id.substring(5);
//            try{
//                int num = Integer.parseInt(digits);
//                nextID = Math.max(nextID, num+1);
//            }
//            catch (NumberFormatException e){
//                continue;
//            }
//        }
//
//        cloneData.id = String.format("skin_%d", nextID);

        customSkins.add(i+1, cloneData);
        skinListView.getItems().add(i+1, duplicateName);
        skinListView.getSelectionModel().select(i+1);

        updateView();
        saveText.setVisible(false);
    }

    @FXML
    protected void onRecoverButton() throws IOException {
        if(recoverStage != null) {
            recoverStage.toFront();
            recoverStage.requestFocus();
            recoverStage.show();
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("recover-view.fxml"));
        Parent root = fxmlLoader.load();
        recoverController = fxmlLoader.getController();
        Scene scene = new Scene(root, 600, 400);
        recoverStage = new Stage();
        recoverStage.setTitle("Recover skins");
        deletedSkins.forEach(skinData -> recoverController.recoverList.getItems().add(skinData.name));
        scene.setOnKeyPressed(recoverController::processKeyPress);
        recoverController.library = this;
        recoverStage.setResizable(false);
        recoverStage.setScene(scene);
        recoverStage.show();
    }

    @FXML
    protected void onSlimBox() {
        int i = skinListView.getSelectionModel().getSelectedIndex();
        if(i < 0) return;

        customSkins.get(i).slim ^= true;
        saveText.setVisible(false);
    }

    @FXML
    protected void onModelButton() throws IOException {
        int i = skinListView.getSelectionModel().getSelectedIndex();
        if(i < 0) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image", "*.png"));
        File imageFile = fileChooser.showOpenDialog(LibrarianApplication.pStage);
        if(imageFile == null) return;
        customSkins.get(i).modelImage = "data:image/png;base64," + new String(Base64.getEncoder().encode(Files.readAllBytes(imageFile.toPath())));

        updateView();
        saveText.setVisible(false);
    }

    @FXML
    protected void onSkinButton() throws IOException {
        int i = skinListView.getSelectionModel().getSelectedIndex();
        if(i < 0) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image", "*.png"));
        File imageFile = fileChooser.showOpenDialog(LibrarianApplication.pStage);
        if(imageFile == null) return;
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        Image image = new Image(new ByteArrayInputStream(imageBytes));
        if(image.getWidth() == 64 && (image.getHeight() == 32 || image.getHeight() == 64)){
            customSkins.get(i).skinImage = "data:image/png;base64," + new String(Base64.getEncoder().encode(imageBytes));
            updateView();
            saveText.setVisible(false);
        }
    }

    @FXML
    protected void onShortcutButton() throws IOException {
        if(shortcutStage != null) {
            shortcutStage.toFront();
            shortcutStage.requestFocus();
            shortcutStage.show();
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("shortcut-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 600, 400);
        shortcutStage = new Stage();
        shortcutStage.setTitle("Librarian Shorcuts");
        shortcutStage.setResizable(false);
        shortcutStage.setScene(scene);
        shortcutStage.show();
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

        skinName.setText(customSkins.get(i).name);
        byte[] modelBytes = Base64.getDecoder().decode(customSkins.get(i).modelImage.replace("data:image/png;base64,", "").replace("\\u003d", ""));
        Image imageM = new Image(new ByteArrayInputStream(modelBytes));
        modelImage.setImage(imageM);

        byte[] skinBytes = Base64.getDecoder().decode(customSkins.get(i).skinImage.replace("data:image/png;base64,", "").replace("\\u003d", ""));
        Image imageS = new Image(new ByteArrayInputStream(skinBytes));
        skinImage.setImage(imageS);

        modelImage.setVisible(true);
        skinImage.setVisible(true);
        skinName.setDisable(false);
        slimBox.setSelected(customSkins.get(i).slim);
    }

    public void processKeyPress(KeyEvent event) {
        switch(event.getCode()){
            case Q -> onUpButton();
            case E -> onDownButton();
            case X ->{
                if (event.isControlDown()) onDeleteButton();
            }
            case D -> {
                if (event.isControlDown()) onDuplicateButton();
            }
            case ENTER -> {
                if (skinName.isFocused()) skinListView.requestFocus();
            }
            case R -> {
                if(event.isControlDown()) skinName.requestFocus();
            }
            case M -> {
                if(event.isControlDown()){
                    try{ onModelButton(); }
                    catch (IOException ignored) {}
                }
            }
            case N -> {
                if(event.isControlDown()){
                    try{ onSkinButton(); }
                    catch (IOException ignored) {}
                }
            }
            case S -> {
                if(event.isControlDown()) {
                    try{ onSaveButton(); }
                    catch (IOException ignored) {}
                }
            }
            default -> {}
        }
    }
}