package com.bernardosantiago.invoicing.model;

public class InvoiceItem {
    private final Product product;
    private final int quantity;
    
    public InvoiceItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    public Double calculateSubtotal() {
        return this.product.getUnitPrice() * this.quantity;
    }
}