package com.bernardosantiago.invoicing.controller;

import com.bernardosantiago.invoicing.MainApp;
import com.bernardosantiago.invoicing.service.ProductService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Objects;

public class ProductFormController {

    @FXML
    private TextField productNameField;

    @FXML
    private TextField skuField;

    @FXML
    private TextField unitPriceField;

    private final ProductService productService = new ProductService();

    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        showMainView(event);
    }

    @FXML
    private void handleSaveProduct(ActionEvent event) throws IOException {
        String productName = readRequiredText(productNameField, "Product name");
        String sku = readRequiredText(skuField, "SKU");
        Double unitPrice = readUnitPrice();

        if (productName == null || sku == null || unitPrice == null) {
            return;
        }

        try {
            productService.createProduct(productName, sku, unitPrice);
            showMainView(event);
        } catch (IllegalArgumentException exception) {
            showValidationError(exception.getMessage());
        }
    }

    private String readRequiredText(TextField field, String fieldName) {
        String value = field.getText();
        if (value == null || value.isBlank()) {
            showValidationError(fieldName + " is required.");
            field.requestFocus();
            return null;
        }

        return value.trim();
    }

    private Double readUnitPrice() {
        String value = unitPriceField.getText();
        if (value == null || value.isBlank()) {
            showValidationError("Unit price is required.");
            unitPriceField.requestFocus();
            return null;
        }

        String normalizedValue = value.trim().replace("$", "").replace(",", "");
        try {
            double unitPrice = Double.parseDouble(normalizedValue);
            if (unitPrice < 0) {
                showValidationError("Unit price must be zero or greater.");
                unitPriceField.requestFocus();
                return null;
            }

            return unitPrice;
        } catch (NumberFormatException exception) {
            showValidationError("Unit price must be a valid number.");
            unitPriceField.requestFocus();
            return null;
        }
    }

    private void showMainView(ActionEvent event) throws IOException {
        Parent mainView = FXMLLoader.load(Objects.requireNonNull(
                MainApp.class.getResource("/com/bernardosantiago/invoicing/view/MainView.fxml")
        ));

        ((Node) event.getSource()).getScene().setRoot(mainView);
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
