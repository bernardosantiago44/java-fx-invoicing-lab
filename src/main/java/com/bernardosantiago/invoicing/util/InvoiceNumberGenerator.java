package com.bernardosantiago.invoicing.util;

public class InvoiceNumberGenerator {
    private static int counter = 0;
    public static String Generate() {
        // TODO: Implement actual counting logic and formatting
        return "INV-" + "-XXXX-" + counter++;
    }
}