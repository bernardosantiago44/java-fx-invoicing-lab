package com.bernardosantiago.invoicing.model;

public class Product {
    private final String name;
    private final String sku;
    private final Double unitPrice;
    
    public Product(String name, String sku, Double unitPrice) {
        this.name = name;
        this.sku = sku;
        this.unitPrice = unitPrice;
    }
    
    public String getName() { return this.name; }
    public String getSku() { return this.sku; }
    public  Double getUnitPrice() { return this.unitPrice; }
}