package com.bernardosantiago.invoicing.controller;

import com.bernardosantiago.invoicing.MainApp;
import com.bernardosantiago.invoicing.model.Customer;
import com.bernardosantiago.invoicing.model.Invoice;
import com.bernardosantiago.invoicing.model.InvoiceItem;
import com.bernardosantiago.invoicing.service.InvoiceService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class InvoiceHistoryController {

    public Button backToMainButton;
    public Button clearFilterButton;
    public Button filterButton;
    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private TableView<Invoice> invoicesTableView;

    @FXML
    private TableColumn<Invoice, String> invoiceNumberColumn;

    @FXML
    private TableColumn<Invoice, String> issueDateColumn;

    @FXML
    private TableColumn<Invoice, String> dueDateColumn;

    @FXML
    private TableColumn<Invoice, String> customerColumn;

    @FXML
    private TableColumn<Invoice, String> statusColumn;

    @FXML
    private TableColumn<Invoice, String> totalColumn;

    @FXML
    private Label detailInvoiceNumberLabel;

    @FXML
    private Label detailCustomerLabel;

    @FXML
    private Label detailIssueDateLabel;

    @FXML
    private Label detailDueDateLabel;

    @FXML
    private Label detailStatusLabel;

    @FXML
    private Label detailSubtotalLabel;

    @FXML
    private Label detailTaxLabel;

    @FXML
    private Label detailTotalLabel;

    @FXML
    private TableView<InvoiceItem> detailItemsTableView;

    @FXML
    private TableColumn<InvoiceItem, String> detailProductColumn;

    @FXML
    private TableColumn<InvoiceItem, String> detailQuantityColumn;

    @FXML
    private TableColumn<InvoiceItem, String> detailLineTotalColumn;

    private final InvoiceService invoiceService = new InvoiceService();
    private final ObservableList<InvoiceItem> detailItems = FXCollections.observableArrayList();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    private FilteredList<Invoice> filteredInvoices;

    @FXML
    private void initialize() {
        configureInvoicesTable();
        configureDetailItemsTable();
        configureSelection();
        clearDetails();
    }

    @FXML
    private void handleFilter() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            showValidationError("From date must be before or equal to the to date.");
            return;
        }

        filteredInvoices.setPredicate(invoice -> matchesDateFilter(invoice, fromDate, toDate));
        invoicesTableView.getSelectionModel().clearSelection();
        clearDetails();
    }

    @FXML
    private void handleClearFilter() {
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        filteredInvoices.setPredicate((_) -> true);
        invoicesTableView.getSelectionModel().clearSelection();
        clearDetails();
    }

    @FXML
    private void handleBackToMain(ActionEvent event) throws IOException {
        Parent mainView = FXMLLoader.load(Objects.requireNonNull(
                MainApp.class.getResource("/com/bernardosantiago/invoicing/view/MainView.fxml")
        ));

        ((Node) event.getSource()).getScene().setRoot(mainView);
    }

    private void configureInvoicesTable() {
        filteredInvoices = new FilteredList<>(invoiceService.getInvoices(), (_) -> true);
        invoicesTableView.setItems(filteredInvoices);

        invoiceNumberColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getInvoiceNumber()));
        issueDateColumn.setCellValueFactory(data -> new SimpleStringProperty(formatDate(data.getValue().getIssueDate())));
        dueDateColumn.setCellValueFactory(data -> new SimpleStringProperty(formatDate(data.getValue().getDueDate())));
        customerColumn.setCellValueFactory(data -> new SimpleStringProperty(formatCustomer(data.getValue().getCustomer())));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(formatStatus(data.getValue())));
        totalColumn.setCellValueFactory(data -> new SimpleStringProperty(formatCurrency(calculateTotal(data.getValue()))));
    }

    private void configureDetailItemsTable() {
        detailItemsTableView.setItems(detailItems);
        detailProductColumn.setCellValueFactory(data -> new SimpleStringProperty(formatProduct(data.getValue())));
        detailQuantityColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().quantity())));
        detailLineTotalColumn.setCellValueFactory(data -> new SimpleStringProperty(formatCurrency(calculateLineTotal(data.getValue()))));
    }

    private void configureSelection() {
        invoicesTableView.getSelectionModel().selectedItemProperty().addListener((_, _, selectedInvoice) -> {
            if (selectedInvoice == null) {
                clearDetails();
                return;
            }

            showInvoiceDetails(selectedInvoice);
        });
    }

    private void showInvoiceDetails(Invoice invoice) {
        double subtotal = invoice.calculateSubtotal();
        double tax = invoice.calculateTax();
        double total = invoice.calculateTotal();

        detailInvoiceNumberLabel.setText(valueOrDash(invoice.getInvoiceNumber()));
        detailCustomerLabel.setText(formatCustomer(invoice.getCustomer()));
        detailIssueDateLabel.setText(formatDate(invoice.getIssueDate()));
        detailDueDateLabel.setText(formatDate(invoice.getDueDate()));
        detailStatusLabel.setText(formatStatus(invoice));
        detailSubtotalLabel.setText(formatCurrency(subtotal));
        detailTaxLabel.setText(formatCurrency(tax));
        detailTotalLabel.setText(formatCurrency(total));
        detailItems.setAll(invoice.getItems() == null ? List.of() : invoice.getItems());
        detailItemsTableView.refresh();
    }

    private void clearDetails() {
        detailInvoiceNumberLabel.setText("-");
        detailCustomerLabel.setText("-");
        detailIssueDateLabel.setText("-");
        detailDueDateLabel.setText("-");
        detailStatusLabel.setText("-");
        detailSubtotalLabel.setText(formatCurrency(0.0));
        detailTaxLabel.setText(formatCurrency(0.0));
        detailTotalLabel.setText(formatCurrency(0.0));
        detailItems.clear();
    }

    private boolean matchesDateFilter(Invoice invoice, LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null && toDate == null) {
            return true;
        }

        if (invoice == null || invoice.getIssueDate() == null) {
            return false;
        }

        LocalDate invoiceDate = invoice.getIssueDate();
        boolean afterFrom = fromDate == null || !invoiceDate.isBefore(fromDate);
        boolean beforeTo = toDate == null || !invoiceDate.isAfter(toDate);
        return afterFrom && beforeTo;
    }

    private double calculateTotal(Invoice invoice) {
        return invoice.calculateTotal();
    }

    private double calculateLineTotal(InvoiceItem item) {
        return item.calculateTotal();
    }

    private String formatCustomer(Customer customer) {
        return customer == null ? "-" : customer.name;
    }

    private String formatProduct(InvoiceItem item) {
        return item == null || item.product() == null ? "-" : item.product().name();
    }

    private String formatStatus(Invoice invoice) {
        return invoice == null || invoice.getStatus() == null ? "-" : invoice.getStatus().name();
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : date.toString();
    }

    private String formatCurrency(Double amount) {
        return currencyFormat.format(amount == null ? 0.0 : amount);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
