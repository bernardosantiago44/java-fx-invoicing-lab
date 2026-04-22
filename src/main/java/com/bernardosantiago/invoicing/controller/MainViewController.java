package com.bernardosantiago.invoicing.controller;

import com.bernardosantiago.invoicing.MainApp;
import com.bernardosantiago.invoicing.model.User;
import com.bernardosantiago.invoicing.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Objects;

public class MainViewController {

    @FXML
    private TextField nameField;

    @FXML
    private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleShowHistory(ActionEvent event) throws IOException {
        showView(event, "/com/bernardosantiago/invoicing/view/InvoiceHistoryView.fxml");
    }

    @FXML
    private void handleNewProduct(ActionEvent event) throws IOException {
        showView(event, "/com/bernardosantiago/invoicing/view/ProductFormView.fxml");
    }

    private void showView(ActionEvent event, String viewPath) throws IOException {
        Parent view = FXMLLoader.load(Objects.requireNonNull(MainApp.class.getResource(viewPath)));
        ((Node) event.getSource()).getScene().setRoot(view);
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText();

        if (name == null || name.isBlank()) {
            messageLabel.setText("Name is required.");
            return;
        }

        User user = new User(name);
        userService.save(user);

        messageLabel.setText("Saved: " + user.getName());
        nameField.clear();
    }
}
