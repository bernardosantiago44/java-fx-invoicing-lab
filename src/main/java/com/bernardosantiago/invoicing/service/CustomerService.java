package com.bernardosantiago.invoicing.service;

import com.bernardosantiago.invoicing.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Optional;

public class CustomerService {
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();
    
    public CustomerService() {
        addPredefinedCustomers();
    }
    
    public ObservableList<Customer> getAllCustomers() {
        return FXCollections.unmodifiableObservableList(customers);
    }
    
    public Optional<Customer> findCustomerByName(String name) {
        validateSearchName(name);

        String normalizedName = name.trim();
        return customers.stream()
                .filter(customer -> customer.name.equalsIgnoreCase(normalizedName))
                .findFirst();
    }

    public void addCustomer(Customer customer) {
        validateCustomer(customer);

        if (findCustomerByName(customer.name).isPresent()) {
            throw new IllegalArgumentException("Customer name already exists.");
        }

        customers.add(customer);
    }

    private void addPredefinedCustomers() {
        addCustomer(new Customer("Bernardo Santiago", "TAX-001", "Av. Universidad 100, Ciudad de Mexico"));
        addCustomer(new Customer("Ana Martinez", "TAX-002", "Calle Reforma 245, Guadalajara"));
        addCustomer(new Customer("Carlos Rivera", "TAX-003", "Blvd. Kukulkan 88, Cancun"));
        addCustomer(new Customer("Lucia Hernandez", "TAX-004", "Av. Fundidora 310, Monterrey"));
        addCustomer(new Customer("Roberto Garcia", "TAX-005", "Paseo Montejo 52, Merida"));
    }

    private void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required.");
        }

        validateRequired(customer.name, "Customer name");
        validateRequired(customer.taxId, "Customer tax ID");
        validateRequired(customer.address, "Customer address");
    }

    private void validateSearchName(String name) {
        validateRequired(name, "Customer name");
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }
}
