package com.bernardosantiago.invoicing.controller;

import com.bernardosantiago.invoicing.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

public class ProductFormController {

    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        showMainView(event);
    }

    @FXML
    private void handleSaveProduct(ActionEvent event) throws IOException {
        showMainView(event);
    }

    private void showMainView(ActionEvent event) throws IOException {
        Parent mainView = FXMLLoader.load(Objects.requireNonNull(
                MainApp.class.getResource("/com/bernardosantiago/invoicing/view/MainView.fxml")
        ));

        ((Node) event.getSource()).getScene().setRoot(mainView);
    }
}
