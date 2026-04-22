package com.bernardosantiago.invoicing.service;

import com.bernardosantiago.invoicing.model.Invoice;
import com.bernardosantiago.invoicing.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductService {
    private ObservableList<Product> products = FXCollections.observableArrayList();
    
    public ProductService() {}
    
    public ObservableList<Product> getAllProducts() {
        return FXCollections.unmodifiableObservableList(products);
    }
    
    /**
     * Looks for a product with the given sku in the repository.
     * 
     * @param sku String of the sku to search for.
     * @return Product with the given sku, or null if not found.
     */
    public Product findProductBySku(String sku) {
        for (Product product : products) {
            if (product.getSku().equalsIgnoreCase(sku.trim())) return product;
        }
        return null;
    }
}