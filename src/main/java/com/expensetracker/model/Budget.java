package com.expensetracker.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.UUID;

public class Budget {
    private final String id;
    private final SimpleStringProperty category;
    private final SimpleDoubleProperty budgetAmount;
    private final SimpleStringProperty period; // e.g., "MONTHLY", "WEEKLY"

    public Budget(String category, double budgetAmount, String period) {
        this(UUID.randomUUID().toString(), category, budgetAmount, period);
    }

    public Budget(String id, String category, double budgetAmount, String period) {
        this.id = id;
        this.category = new SimpleStringProperty(category);
        this.budgetAmount = new SimpleDoubleProperty(budgetAmount);
        this.period = new SimpleStringProperty(period);
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category.get();
    }

    public SimpleStringProperty categoryProperty() {
        return category;
    }

    public double getBudgetAmount() {
        return budgetAmount.get();
    }

    public SimpleDoubleProperty budgetAmountProperty() {
        return budgetAmount;
    }

    public String getPeriod() {
        return period.get();
    }

    public SimpleStringProperty periodProperty() {
        return period;
    }

    @Override
    public String toString() {
        return String.format("%s: K %.2f (%s)", category.get(), budgetAmount.get(), period.get());
    }
}
