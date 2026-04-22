package com.bernardosantiago.invoicing.controller;

import com.bernardosantiago.invoicing.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

public class InvoiceHistoryController {

    @FXML
    private void handleBackToMain(ActionEvent event) throws IOException {
        Parent mainView = FXMLLoader.load(Objects.requireNonNull(
                MainApp.class.getResource("/com/bernardosantiago/invoicing/view/MainView.fxml")
        ));

        ((Node) event.getSource()).getScene().setRoot(mainView);
    }
}
