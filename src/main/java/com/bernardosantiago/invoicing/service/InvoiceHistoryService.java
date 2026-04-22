package com.bernardosantiago.invoicing.service;

import com.bernardosantiago.invoicing.model.Invoice;
import com.bernardosantiago.invoicing.model.InvoiceStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.time.LocalDate;

public class InvoiceHistoryService {
    private final ObservableList<Invoice> invoices = FXCollections.observableArrayList();
    
    public InvoiceHistoryService() {}
    
    public void generateInvoice(Invoice invoice) {
        validateInvoice(invoice);

        invoice.setStatus(InvoiceStatus.PAID);

        saveOrReplace(invoice);
    }
    
    public void saveDraft(Invoice invoice) {
        validateInvoice(invoice);

        invoice.setStatus(InvoiceStatus.PENDING);
        saveOrReplace(invoice);
    }
    
    public ObservableList<Invoice> getAllInvoices() {
        return FXCollections.unmodifiableObservableList(invoices);
    }
    
    public FilteredList<Invoice> filterByDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        return new FilteredList<>(invoices, invoice -> isInsideDateRange(invoice, from, to));
    }

    private void validateInvoice(Invoice invoice) {
        if (invoice == null || !invoice.isValid()) {
            throw new IllegalArgumentException("Invoice is required.");
        }
    }

    private void saveOrReplace(Invoice invoice) {
        for (int index = 0; index < invoices.size(); index++) {
            Invoice currentInvoice = invoices.get(index);
            if (hasSameInvoiceNumber(currentInvoice, invoice)) {
                invoices.set(index, invoice);
                return;
            }
        }

        invoices.add(invoice);
    }

    private boolean hasSameInvoiceNumber(Invoice currentInvoice, Invoice invoice) {
        if (currentInvoice == null || invoice == null) {
            return false;
        }

        String currentInvoiceNumber = currentInvoice.getInvoiceNumber();
        String invoiceNumber = invoice.getInvoiceNumber();
        return currentInvoiceNumber != null && currentInvoiceNumber.equals(invoiceNumber);
    }

    private boolean isInsideDateRange(Invoice invoice, LocalDate from, LocalDate to) {
        if (invoice == null) {
            return false;
        }

        LocalDate issueDate = invoice.getIssueDate();
        if (issueDate == null) {
            return from == null && to == null;
        }

        boolean isAfterStart = from == null || !issueDate.isBefore(from);
        boolean isBeforeEnd = to == null || !issueDate.isAfter(to);
        return isAfterStart && isBeforeEnd;
    }
}
