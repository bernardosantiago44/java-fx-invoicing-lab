package com.bernardosantiago.invoicing.model;

import com.bernardosantiago.invoicing.config.Constants;

public record InvoiceItem(Product product, int quantity) {

    public Double calculateSubtotal() {
        return this.product.unitPrice() * this.quantity;
    }
    
    public Double calculateTax() {
        return this.product.unitPrice() * this.quantity * Constants.TAX_RATE;
    }
    
    public double calculateTotal() {
        return calculateSubtotal() + calculateTax();
    }

    public boolean isValid() {
        Product product = this.product();
        return product != null
                && product.unitPrice() != null
                && product.unitPrice() >= 0
                && this.quantity > 0;
    }
}
