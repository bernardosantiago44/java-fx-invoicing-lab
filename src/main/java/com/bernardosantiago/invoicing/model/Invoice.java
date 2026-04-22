package com.bernardosantiago.invoicing.model;

import com.bernardosantiago.invoicing.util.InvoiceNumberGenerator;
import com.bernardosantiago.invoicing.config.Constants;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private final String invoiceNumber;
    
    private LocalDate issueDate;
    private LocalDate dueDate;
    
    private Customer customer;
    private List<InvoiceItem> items;
    private InvoiceStatus status;
    
    public Invoice(LocalDate issueDate, LocalDate dueDate, Customer customer, InvoiceStatus status) {
        this.invoiceNumber = InvoiceNumberGenerator.Generate();
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.customer = customer;
        this.status = status;
        this.items = new ArrayList<>();
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
    
    public void addItem(InvoiceItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }

        this.items.add(item);
    }

    public void removeItem(InvoiceItem item) {
        if (this.items == null) {
            return;
        }

        this.items.remove(item);
    }

    public void clearItems() {
        if (this.items == null) {
            this.items = new ArrayList<>();
            return;
        }

        this.items.clear();
    }
    
    public Double calculateSubtotal() {
        if (this.items == null || this.items.isEmpty()) {
            return 0.0;
        }

        double subtotal = 0.0;
        for (InvoiceItem item : this.items) {
            subtotal += item.calculateSubtotal();
        }

        return subtotal;
    }
    
    public Double calculateTax() {
        return calculateSubtotal() * Constants.TAX_RATE;
    }
    
    public Double calculateTotal() {
        return calculateSubtotal() * (1 + Constants.TAX_RATE);
    }
    
    public boolean isInvalid() {
        if (this.getInvoiceNumber() == null || this.getInvoiceNumber().isBlank()) {
            return true;
        }

        if (this.getIssueDate() == null || this.getDueDate() == null) {
            return true;
        }

        if (this.getDueDate().isBefore(this.getIssueDate())) {
            return true;
        }

        if (this.getCustomer() == null || this.getStatus() == null) {
            return true;
        }

        return !this.areValidInvoiceItems(this.getItems());
    }
    
    private boolean areValidInvoiceItems(List<InvoiceItem> items) {
        if (items == null || items.isEmpty()) return false;

        for (InvoiceItem item : items) {
            if (!item.isValid()) {
                return false;
            }
        }

        return true;
    }
}
