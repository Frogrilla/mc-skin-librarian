package com.frogrilla.mcskinlibrarian;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class LibrarianApplication extends Application {

    public static Scene scene;
    public static Stage pStage;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibrarianApplication.class.getResource("librarian-view.fxml"));
        Parent root = fxmlLoader.load();
        LibrarianController controller = fxmlLoader.getController();
        pStage = stage;
        scene = new Scene(root, 1280, 720);
        stage.setTitle("MC Skin Librarian");
        scene.setOnKeyPressed(e -> controller.processKeyPress(e.getCode()));
        stage.setScene(scene);
        stage.show();

    }
}