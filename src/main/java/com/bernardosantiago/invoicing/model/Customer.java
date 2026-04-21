package com.bernardosantiago.invoicing.model;

public class Customer {
    public String name;
    public String taxId;
    public String address;
    
    public Customer(String name, String taxId, String address) {
        this.name = name;
        this.taxId = taxId;
        this.address = address;
    }
}