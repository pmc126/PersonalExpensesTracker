package com.expensetracker;

import java.time.LocalDate;

public class Expense extends com.expensetracker.model.Expense {
    public Expense(double amount, String category, LocalDate date, String description) {
        super(amount, category, date, description);
    }

    public Expense(double amount, String category, String dateString, String description) {
        super(amount, category, dateString, description);
    }

    public Expense(String id, double amount, String category, LocalDate date, String description) {
        super(id, amount, category, date, description);
    }
}