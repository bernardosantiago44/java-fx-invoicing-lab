package com.bernardosantiago.invoicing.controller;

import com.bernardosantiago.invoicing.model.User;
import com.bernardosantiago.invoicing.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainViewController {

    @FXML
    private TextField nameField;

    @FXML
    private Label messageLabel;

    private final UserService userService = new UserService();

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