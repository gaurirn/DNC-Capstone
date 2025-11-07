package com.training.dunningcuring.invoice.entity;

public enum InvoiceStatus {
    DRAFT,      // Being created
    ISSUED,     // Sent to customer, awaiting payment
    PAID,       // Payment received
    OVERDUE,    // Issued and past due date
    VOID        // Canceled
}