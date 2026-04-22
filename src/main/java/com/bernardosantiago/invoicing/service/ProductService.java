package com.bernardosantiago.invoicing.service;

import com.bernardosantiago.invoicing.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductService {
    private static final ObservableList<Product> products = createSampleProducts();
    
    public ProductService() {}
    
    public ObservableList<Product> getAllProducts() {
        return FXCollections.unmodifiableObservableList(products);
    }

    public Product createProduct(String name, String sku, Double unitPrice) {
        Product product = new Product(name, sku, unitPrice);
        addProduct(product);
        return product;
    }

    public void addProduct(Product product) {
        validateProduct(product);

        if (findProductBySku(product.sku()) != null) {
            throw new IllegalArgumentException("Product SKU already exists.");
        }

        products.add(product);
    }

    public Product updateProduct(String currentSku, Product updatedProduct) {
        validateSearchSku(currentSku);
        validateProduct(updatedProduct);

        String normalizedCurrentSku = currentSku.trim();
        int productIndex = findProductIndexBySku(normalizedCurrentSku);
        if (productIndex < 0) {
            throw new IllegalArgumentException("Product with SKU " + normalizedCurrentSku + " was not found.");
        }

        Product existingProductWithUpdatedSku = findProductBySku(updatedProduct.sku());
        if (existingProductWithUpdatedSku != null
                && !existingProductWithUpdatedSku.sku().equalsIgnoreCase(normalizedCurrentSku)) {
            throw new IllegalArgumentException("Product SKU already exists.");
        }

        products.set(productIndex, updatedProduct);
        return updatedProduct;
    }

    public Product updateProduct(Product updatedProduct) {
        validateProduct(updatedProduct);
        return updateProduct(updatedProduct.sku(), updatedProduct);
    }
    
    /**
     * Looks for a product with the given sku in the repository.
     * 
     * @param sku String of the sku to search for.
     * @return Product with the given sku, or null if not found.
     */
    public Product findProductBySku(String sku) {
        validateSearchSku(sku);

        String normalizedSku = sku.trim();
        for (Product product : products) {
            if (product.sku().equalsIgnoreCase(normalizedSku)) return product;
        }
        return null;
    }

    private int findProductIndexBySku(String sku) {
        for (int index = 0; index < products.size(); index++) {
            if (products.get(index).sku().equalsIgnoreCase(sku)) {
                return index;
            }
        }

        return -1;
    }

    private static ObservableList<Product> createSampleProducts() {
        return FXCollections.observableArrayList(
                new Product("Laptop Lenovo ThinkPad E14", "PROD-001", 18999.00),
                new Product("Monitor Dell 24 Inch", "PROD-002", 4299.00),
                new Product("Mechanical Keyboard", "PROD-003", 1499.00),
                new Product("Wireless Mouse", "PROD-004", 699.00),
                new Product("USB-C Docking Station", "PROD-005", 2599.00),
                new Product("Office Chair", "PROD-006", 3799.00),
                new Product("External SSD 1TB", "PROD-007", 2199.00),
                new Product("Webcam Full HD", "PROD-008", 999.00)
        );
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product is required.");
        }

        validateRequired(product.name(), "Product name");
        validateRequired(product.sku(), "Product SKU");

        if (product.unitPrice() == null || product.unitPrice() < 0) {
            throw new IllegalArgumentException("Product unit price must be zero or greater.");
        }
    }

    private void validateSearchSku(String sku) {
        validateRequired(sku, "Product SKU");
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }
}
