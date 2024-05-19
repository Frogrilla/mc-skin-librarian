package com.frogrilla.mcskinlibrarian;

import javafx.fxml.FXML;
import com.google.gson.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.time.Instant;
import java.util.*;

public class LibrarianController {
    @FXML
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private List<SkinData> customSkins = new ArrayList<>();
    private File skinFile;

    @FXML
    protected void onSelectFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json Files", "*.json"));
        skinFile = fileChooser.showOpenDialog(LibrarianApplication.pStage);
        BufferedReader reader = new BufferedReader(new FileReader(skinFile));
        JsonObject object = gson.fromJson(reader, JsonObject.class).getAsJsonObject("customSkins");
        customSkins.clear();
        object.entrySet().forEach(entry -> {
            customSkins.add(gson.fromJson(entry.getValue(), SkinData.class));
        });
        System.out.print(customSkins);
        reader.close();
    }

    @FXML
    protected void onOrderButton() {
        Collections.shuffle(customSkins);
    }

    @FXML
    protected void onSaveButton() throws IOException {
        JsonObject skinDataMap = new JsonObject();
        int i = 0;
        customSkins.forEach(skin -> {
            skin.created = Instant.now().plusMillis(i).toString();
            skinDataMap.add(skin.id, gson.toJsonTree(skin));
        });

        JsonObject skinObject = new JsonObject();
        skinObject.add("customSkins", skinDataMap);
        FileWriter writer = new FileWriter(skinFile);
        writer.write(gson.toJson(skinObject));
        writer.close();
    }
}