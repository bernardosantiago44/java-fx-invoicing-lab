package com.bernardosantiago.invoicing.service;

import com.bernardosantiago.invoicing.config.Constants;
import com.bernardosantiago.invoicing.model.Invoice;
import com.bernardosantiago.invoicing.model.InvoiceItem;
import com.bernardosantiago.invoicing.model.InvoiceStatus;
import com.bernardosantiago.invoicing.model.Product;
import com.bernardosantiago.invoicing.util.InvoiceNumberGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InvoiceService {
    private final ObservableList<Invoice> invoices = FXCollections.observableArrayList();
    
    public InvoiceService() {}

    public ObservableList<Invoice> getInvoices() {
        return FXCollections.unmodifiableObservableList(invoices);
    }
    
    public String generateInvoiceNumber() {
        return InvoiceNumberGenerator.Generate();
    }
    
    public void addInvoice(Invoice invoice) {
        if (invoice == null || !invoice.isValid())
            throw new IllegalArgumentException("Invoice is incomplete or invalid.");
        
        invoices.add(invoice);
    }

    public void addItem(Invoice invoice, Product product, int quantity) {
        validateInvoiceForItems(invoice);
        validateProduct(product);
        validateQuantity(quantity);

        List<InvoiceItem> items = invoice.getItems();
        if (items == null) {
            items = new ArrayList<>();
            invoice.setItems(items);
        }

        for (int index = 0; index < items.size(); index++) {
            InvoiceItem currentItem = items.get(index);
            if (isSameProduct(currentItem.getProduct(), product)) {
                int mergedQuantity = currentItem.getQuantity() + quantity;
                items.set(index, new InvoiceItem(product, mergedQuantity));
                return;
            }
        }

        items.add(new InvoiceItem(product, quantity));
    }

    public void addItem(Invoice invoice, InvoiceItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Invoice item is required.");
        }

        addItem(invoice, item.getProduct(), item.getQuantity());
    }
    
    public void removeItem(Invoice invoice, InvoiceItem item) {
        validateInvoiceForItems(invoice);

        if (item == null || invoice.getItems() == null) {
            return;
        }

        invoice.removeItem(item);
    }
    
    public Double calculateSubtotal(List<InvoiceItem> items) {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }

        double subtotal = 0.0;
        for (InvoiceItem item : items) {
            if (item != null) {
                subtotal += item.calculateSubtotal();
            }
        }

        return subtotal;
    }
    
    public Double calculateTax(Double subtotal) {
        return normalizeAmount(subtotal) * Constants.TAX_RATE;
    }
    
    public Double calculateTotal(Double subtotal, Double tax) {
        return normalizeAmount(subtotal) + normalizeAmount(tax);
    }
    
    public void resetInvoice(Invoice invoice) {
        validateInvoiceForItems(invoice);

        invoice.setIssueDate(null);
        invoice.setDueDate(null);
        invoice.setCustomer(null);
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.clearItems();
    }

    private void validateInvoiceForItems(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice is required.");
        }
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product is required.");
        }

        if (product.getUnitPrice() == null || product.getUnitPrice() < 0) {
            throw new IllegalArgumentException("Product unit price must be zero or greater.");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
    }

    private boolean isSameProduct(Product firstProduct, Product secondProduct) {
        if (firstProduct == secondProduct) {
            return true;
        }

        if (firstProduct == null || secondProduct == null) {
            return false;
        }

        if (hasText(firstProduct.getSku()) && hasText(secondProduct.getSku())) {
            return firstProduct.getSku().equalsIgnoreCase(secondProduct.getSku());
        }

        return Objects.equals(firstProduct.getName(), secondProduct.getName());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private double normalizeAmount(Double amount) {
        return amount == null ? 0.0 : amount;
    }
}
