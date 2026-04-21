package com.bernardosantiago.invoicing.model;

import com.bernardosantiago.invoicing.util.InvoiceNumberGenerator;
import com.bernardosantiago.invoicing.config.Constants;
import java.time.LocalDate;
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
    }
    
    public void addItem(InvoiceItem item) {
        this.items.add(item);
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
    
    public Double calculateTotal() {
        return calculateSubtotal() * (1 + Constants.TAX_RATE);
    }
}
