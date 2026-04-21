package com.bernardosantiago.invoicing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/com/bernardosantiago/invoicing/view/MainView.fxml")
        );

        Scene scene = new Scene(loader.load(), 800, 500);
        scene.getStylesheets().add(
                Objects.requireNonNull(MainApp.class.getResource("/com/bernardosantiago/invoicing/styles/app.css")).toExternalForm()
        );

        stage.setTitle("JavaFX MVC App");
        stage.setScene(scene);
        stage.show();
    }

}