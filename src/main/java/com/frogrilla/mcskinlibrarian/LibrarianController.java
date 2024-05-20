package com.frogrilla.mcskinlibrarian;

import javafx.fxml.FXML;
import com.google.gson.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.*;
import java.time.Instant;
import java.util.*;

public class LibrarianController {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private List<SkinData> customSkins = new ArrayList<>();
    private File skinFile;
    private boolean loaded = false;

    @FXML
    private ListView<String> skinListView;

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
        customSkins.forEach(skinData -> skinListView.getItems().add(skinData.name));
        reader.close();

        loaded = true;
    }

    @FXML
    protected void onOrderButton() {
        Collections.shuffle(customSkins);
    }

    @FXML
    protected void onSaveButton() throws IOException {
        if(!loaded) return;
        JsonObject skinDataMap = new JsonObject();
        customSkins.forEach(skin -> {
            int i = customSkins.indexOf(skin);
            skin.created = Instant.now().minusSeconds(i).toString();
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
        if(!loaded) return;
        int i = skinListView.getSelectionModel().getSelectedIndex();
        if(i <= 0) return;

        SkinData swappedData = customSkins.get(i-1);
        customSkins.set(i-1, customSkins.get(i));
        customSkins.set(i, swappedData);

        String swappedName = skinListView.getItems().get(i-1);
        skinListView.getItems().set(i-1, skinListView.getItems().get(i));
        skinListView.getItems().set(i, swappedName);

        skinListView.getSelectionModel().select(i-1);
    }

    @FXML
    protected void onDownButton(){
        if(!loaded) return;
        int i = skinListView.getSelectionModel().getSelectedIndex();
        if(i < 0 || i >= skinListView.getItems().size()-1) return;

        SkinData swappedData = customSkins.get(i+1);
        customSkins.set(i+1, customSkins.get(i));
        customSkins.set(i, swappedData);

        String swappedName = skinListView.getItems().get(i+1);
        skinListView.getItems().set(i+1, skinListView.getItems().get(i));
        skinListView.getItems().set(i, swappedName);
        skinListView.getSelectionModel().select(i+1);
    }
}