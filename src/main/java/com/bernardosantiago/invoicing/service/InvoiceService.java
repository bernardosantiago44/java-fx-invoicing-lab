package com.bernardosantiago.invoicing.service;

import com.bernardosantiago.invoicing.model.Customer;
import com.bernardosantiago.invoicing.model.Invoice;
import com.bernardosantiago.invoicing.model.InvoiceItem;
import com.bernardosantiago.invoicing.model.InvoiceStatus;
import com.bernardosantiago.invoicing.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InvoiceService {
    private static final ObservableList<Invoice> invoices = createSampleInvoices();
    
    public InvoiceService() {}

    public ObservableList<Invoice> getInvoices() {
        return FXCollections.unmodifiableObservableList(invoices);
    }
    
    public void addInvoice(Invoice invoice) {
        if (invoice == null || invoice.isInvalid())
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
            if (isSameProduct(currentItem.product(), product)) {
                int mergedQuantity = currentItem.quantity() + quantity;
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

        addItem(invoice, item.product(), item.quantity());
    }
    
    public void removeItem(Invoice invoice, InvoiceItem item) {
        validateInvoiceForItems(invoice);

        if (item == null || invoice.getItems() == null) {
            return;
        }

        invoice.removeItem(item);
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

        if (product.unitPrice() == null || product.unitPrice() < 0) {
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

        if (hasText(firstProduct.sku()) && hasText(secondProduct.sku())) {
            return firstProduct.sku().equalsIgnoreCase(secondProduct.sku());
        }

        return Objects.equals(firstProduct.name(), secondProduct.name());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static ObservableList<Invoice> createSampleInvoices() {
        Product laptop = new Product("Laptop Lenovo ThinkPad E14", "PROD-001", 18999.00);
        Product monitor = new Product("Monitor Dell 24 Inch", "PROD-002", 4299.00);
        Product keyboard = new Product("Mechanical Keyboard", "PROD-003", 1499.00);
        Product mouse = new Product("Wireless Mouse", "PROD-004", 699.00);
        Product dock = new Product("USB-C Docking Station", "PROD-005", 2599.00);
        Product chair = new Product("Office Chair", "PROD-006", 3799.00);
        Product ssd = new Product("External SSD 1TB", "PROD-007", 2199.00);
        Product webcam = new Product("Webcam Full HD", "PROD-008", 999.00);

        return FXCollections.observableArrayList(
                createInvoice(
                        LocalDate.now().minusDays(42),
                        LocalDate.now().minusDays(12),
                        new Customer("Bernardo Santiago", "TAX-001", "Av. Universidad 100, Ciudad de Mexico"),
                        InvoiceStatus.PAID,
                        new InvoiceItem(laptop, 1),
                        new InvoiceItem(mouse, 2)
                ),
                createInvoice(
                        LocalDate.now().minusDays(35),
                        LocalDate.now().minusDays(5),
                        new Customer("Ana Martinez", "TAX-002", "Calle Reforma 245, Guadalajara"),
                        InvoiceStatus.PAID,
                        new InvoiceItem(monitor, 2),
                        new InvoiceItem(keyboard, 2)
                ),
                createInvoice(
                        LocalDate.now().minusDays(24),
                        LocalDate.now().plusDays(6),
                        new Customer("Carlos Rivera", "TAX-003", "Blvd. Kukulkan 88, Cancun"),
                        InvoiceStatus.PENDING,
                        new InvoiceItem(dock, 1),
                        new InvoiceItem(ssd, 1)
                ),
                createInvoice(
                        LocalDate.now().minusDays(18),
                        LocalDate.now().plusDays(12),
                        new Customer("Lucia Hernandez", "TAX-004", "Av. Fundidora 310, Monterrey"),
                        InvoiceStatus.PENDING,
                        new InvoiceItem(chair, 3)
                ),
                createInvoice(
                        LocalDate.now().minusDays(9),
                        LocalDate.now().plusDays(21),
                        new Customer("Roberto Garcia", "TAX-005", "Paseo Montejo 52, Merida"),
                        InvoiceStatus.PENDING,
                        new InvoiceItem(webcam, 4),
                        new InvoiceItem(mouse, 4)
                ),
                createInvoice(
                        LocalDate.now().minusDays(3),
                        LocalDate.now().plusDays(27),
                        new Customer("Bernardo Santiago", "TAX-001", "Av. Universidad 100, Ciudad de Mexico"),
                        InvoiceStatus.PAID,
                        new InvoiceItem(laptop, 2),
                        new InvoiceItem(monitor, 2),
                        new InvoiceItem(dock, 2)
                )
        );
    }

    private static Invoice createInvoice(
            LocalDate issueDate,
            LocalDate dueDate,
            Customer customer,
            InvoiceStatus status,
            InvoiceItem... items
    ) {
        Invoice invoice = new Invoice(issueDate, dueDate, customer, status);
        for (InvoiceItem item : items) {
            invoice.addItem(item);
        }

        return invoice;
    }
}
