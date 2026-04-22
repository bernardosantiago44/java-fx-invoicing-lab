package com.bernardosantiago.invoicing.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class InvoiceNumberGenerator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    public static String Generate() {
        return "INV-" + LocalDate.now().format(DATE_FORMATTER) + "-" + String.format("%04d", COUNTER.getAndIncrement());
    }

    public static String generate() {
        return Generate();
    }
}
