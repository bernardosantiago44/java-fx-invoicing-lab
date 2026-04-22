package com.bernardosantiago.invoicing.controller;

import com.bernardosantiago.invoicing.MainApp;
import com.bernardosantiago.invoicing.config.Constants;
import com.bernardosantiago.invoicing.model.Customer;
import com.bernardosantiago.invoicing.model.Invoice;
import com.bernardosantiago.invoicing.model.InvoiceItem;
import com.bernardosantiago.invoicing.model.InvoiceStatus;
import com.bernardosantiago.invoicing.model.Product;
import com.bernardosantiago.invoicing.service.CustomerService;
import com.bernardosantiago.invoicing.service.InvoiceService;
import com.bernardosantiago.invoicing.service.ProductService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class MainViewController {

    public Button newProductButton;
    public Button addItemButton;
    public Button openHistoryButton;
    public Button resetFormButton;
    public Button saveDraftButton;
    public Button generateInvoiceButton;
    @FXML
    private ComboBox<Customer> customerComboBox;

    @FXML
    private TextField taxIdField;

    @FXML
    private DatePicker issueDatePicker;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private TextField addressField;

    @FXML
    private ComboBox<Product> productComboBox;

    @FXML
    private TextField quantityField;

    @FXML
    private TableView<InvoiceItem> lineItemsTableView;

    @FXML
    private TableColumn<InvoiceItem, String> productColumn;

    @FXML
    private TableColumn<InvoiceItem, String> quantityColumn;

    @FXML
    private TableColumn<InvoiceItem, String> unitPriceColumn;

    @FXML
    private TableColumn<InvoiceItem, String> lineTaxColumn;

    @FXML
    private TableColumn<InvoiceItem, String> lineTotalColumn;

    @FXML
    private javafx.scene.control.Label invoiceNumberLabel;

    @FXML
    private javafx.scene.control.Label subtotalLabel;

    @FXML
    private javafx.scene.control.Label taxLabel;

    @FXML
    private javafx.scene.control.Label totalLabel;

    private final CustomerService customerService = new CustomerService();
    private final ProductService productService = new ProductService();
    private final InvoiceService invoiceService = new InvoiceService();
    private final ObservableList<InvoiceItem> lineItems = FXCollections.observableArrayList();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    private Invoice currentInvoice;
    private TableColumn<InvoiceItem, Void> removeColumn;

    @FXML
    private void initialize() {
        configureCustomerComboBox();
        configureProductComboBox();
        configureLineItemsTable();
        configureDatePickers();
        createDraftInvoice();
        refreshLineItems();
        refreshTotals();
    }

    @FXML
    private void handleAddItem() {
        Product selectedProduct = productComboBox.getValue();
        if (selectedProduct == null) {
            showValidationError("Select a product before adding an item.");
            return;
        }

        Integer quantity = readQuantity(quantityField.getText());
        if (quantity == null) {
            return;
        }

        try {
            invoiceService.addItem(currentInvoice, selectedProduct, quantity);
            refreshLineItems();
            refreshTotals();
            quantityField.clear();
        } catch (IllegalArgumentException exception) {
            showValidationError(exception.getMessage());
        }
    }

    @FXML
    private void handleResetForm() {
        createDraftInvoice();
        customerComboBox.getSelectionModel().clearSelection();
        productComboBox.getSelectionModel().clearSelection();
        taxIdField.clear();
        addressField.clear();
        quantityField.clear();
        refreshLineItems();
        refreshTotals();
    }

    @FXML
    private void handleSaveDraft() {
        saveCurrentInvoice(InvoiceStatus.PENDING, "Draft invoice saved.");
    }

    @FXML
    private void handleGenerateInvoice() {
        saveCurrentInvoice(InvoiceStatus.PAID, "Invoice generated.");
    }

    @FXML
    private void handleShowHistory(ActionEvent event) throws IOException {
        showView(event, "/com/bernardosantiago/invoicing/view/InvoiceHistoryView.fxml");
    }

    @FXML
    private void handleNewProduct(ActionEvent event) throws IOException {
        showView(event, "/com/bernardosantiago/invoicing/view/ProductFormView.fxml");
    }

    private void configureCustomerComboBox() {
        customerComboBox.setItems(customerService.getAllCustomers());
        customerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer customer) {
                return customer == null ? "" : customer.name;
            }

            @Override
            public Customer fromString(String value) {
                return null;
            }
        });

        customerComboBox.valueProperty().addListener((_, _, selectedCustomer) -> {
            currentInvoice.setCustomer(selectedCustomer);
            if (selectedCustomer == null) {
                taxIdField.clear();
                addressField.clear();
                return;
            }

            taxIdField.setText(selectedCustomer.taxId);
            addressField.setText(selectedCustomer.address);
        });
    }

    private void configureProductComboBox() {
        productComboBox.setItems(productService.getAllProducts());
        productComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Product product) {
                if (product == null) {
                    return "";
                }

                return product.name() + " (" + product.sku() + ")";
            }

            @Override
            public Product fromString(String value) {
                return null;
            }
        });
    }

    private void configureLineItemsTable() {
        lineItemsTableView.setItems(lineItems);
        lineItemsTableView.setEditable(true);

        productColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().product().name()));
        quantityColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().quantity())));
        unitPriceColumn.setCellValueFactory(data -> new SimpleStringProperty(formatCurrency(data.getValue().product().unitPrice())));
        lineTaxColumn.setCellValueFactory(data -> new SimpleStringProperty(formatCurrency(calculateItemTax(data.getValue()))));
        lineTotalColumn.setCellValueFactory(data -> new SimpleStringProperty(formatCurrency(data.getValue().calculateSubtotal() + calculateItemTax(data.getValue()))));

        configureEditableQuantityColumn();
        addRemoveColumn();
    }

    private void configureDatePickers() {
        issueDatePicker.valueProperty().addListener((_, _, selectedDate) -> currentInvoice.setIssueDate(selectedDate));
        dueDatePicker.valueProperty().addListener((_, _, selectedDate) -> currentInvoice.setDueDate(selectedDate));
    }

    private void configureEditableQuantityColumn() {
        quantityColumn.setEditable(true);
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        quantityColumn.setOnEditCommit(event -> {
            InvoiceItem item = event.getRowValue();
            Integer editedQuantity = readQuantity(event.getNewValue());
            if (editedQuantity == null) {
                lineItemsTableView.refresh();
                return;
            }

            int itemIndex = currentInvoice.getItems().indexOf(item);
            if (itemIndex < 0) {
                lineItemsTableView.refresh();
                return;
            }

            currentInvoice.getItems().set(itemIndex, new InvoiceItem(item.product(), editedQuantity));
            refreshLineItems();
            refreshTotals();
        });
    }

    private void addRemoveColumn() {
        if (removeColumn != null) {
            return;
        }

        removeColumn = new TableColumn<>("Action");
        removeColumn.setPrefWidth(90);
        removeColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");

            {
                removeButton.getStyleClass().add("ghost-button");
                removeButton.setOnAction(_ -> {
                    InvoiceItem item = getTableView().getItems().get(getIndex());
                    invoiceService.removeItem(currentInvoice, item);
                    refreshLineItems();
                    refreshTotals();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeButton);
            }
        });

        lineItemsTableView.getColumns().add(removeColumn);
    }

    private void createDraftInvoice() {
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(30);

        currentInvoice = new Invoice(issueDate, dueDate, null, InvoiceStatus.PENDING);
        invoiceNumberLabel.setText(currentInvoice.getInvoiceNumber());
        issueDatePicker.setValue(issueDate);
        dueDatePicker.setValue(dueDate);
    }

    private void saveCurrentInvoice(InvoiceStatus status, String successMessage) {
        currentInvoice.setStatus(status);
        currentInvoice.setIssueDate(issueDatePicker.getValue());
        currentInvoice.setDueDate(dueDatePicker.getValue());
        currentInvoice.setCustomer(customerComboBox.getValue());

        try {
            invoiceService.addInvoice(currentInvoice);
            showInfo(successMessage);
            handleResetForm();
        } catch (IllegalArgumentException exception) {
            showValidationError(exception.getMessage());
        }
    }

    private Integer readQuantity(String rawQuantity) {
        if (rawQuantity == null || rawQuantity.isBlank()) {
            showValidationError("Quantity is required.");
            return null;
        }

        try {
            int quantity = Integer.parseInt(rawQuantity.trim());
            if (quantity <= 0) {
                showValidationError("Quantity must be greater than zero.");
                return null;
            }

            return quantity;
        } catch (NumberFormatException exception) {
            showValidationError("Quantity must be a whole number.");
            return null;
        }
    }

    private void refreshLineItems() {
        List<InvoiceItem> currentItems = currentInvoice == null || currentInvoice.getItems() == null
                ? List.of()
                : currentInvoice.getItems();
        lineItems.setAll(currentItems);
        lineItemsTableView.refresh();
    }

    private void refreshTotals() {
        double subtotal = currentInvoice.calculateSubtotal();
        double tax = currentInvoice.calculateTax();
        double total = currentInvoice.calculateTotal();

        subtotalLabel.setText(formatCurrency(subtotal));
        taxLabel.setText(formatCurrency(tax));
        totalLabel.setText(formatCurrency(total));
    }

    private double calculateItemTax(InvoiceItem item) {
        return item.calculateSubtotal() * Constants.TAX_RATE;
    }

    private String formatCurrency(Double amount) {
        return currencyFormat.format(amount == null ? 0.0 : amount);
    }

    private void showView(ActionEvent event, String viewPath) throws IOException {
        Parent view = FXMLLoader.load(Objects.requireNonNull(MainApp.class.getResource(viewPath)));
        ((Node) event.getSource()).getScene().setRoot(view);
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invoice");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
