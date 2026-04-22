package com.bernardosantiago.invoicing.model;

public class InvoiceItem {
    private final Product product;
    private final int quantity;
    
    public InvoiceItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
    
    public Double calculateSubtotal() {
        return this.product.getUnitPrice() * this.quantity;
    }
    
    public boolean isValid() {
        Product product = this.getProduct();
        return product != null
                && product.getUnitPrice() != null
                && product.getUnitPrice() >= 0
                && this.quantity > 0;
    }
}
