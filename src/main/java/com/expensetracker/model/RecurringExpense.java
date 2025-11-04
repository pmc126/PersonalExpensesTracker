package com.expensetracker.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.util.UUID;

public class RecurringExpense {
    private final String id;
    private final SimpleDoubleProperty amount;
    private final SimpleStringProperty category;
    private final SimpleObjectProperty<LocalDate> startDate;
    private final SimpleStringProperty frequency; // e.g., "DAILY", "WEEKLY", "MONTHLY", "YEARLY"
    private final SimpleStringProperty description;

    public RecurringExpense(double amount, String category, LocalDate startDate, String frequency, String description) {
        this(UUID.randomUUID().toString(), amount, category, startDate, frequency, description);
    }

    public RecurringExpense(String id, double amount, String category, LocalDate startDate, String frequency, String description) {
        this.id = id;
        this.amount = new SimpleDoubleProperty(amount);
        this.category = new SimpleStringProperty(category);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.frequency = new SimpleStringProperty(frequency);
        this.description = new SimpleStringProperty(description == null ? "" : description);
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount.get();
    }

    public SimpleDoubleProperty amountProperty() {
        return amount;
    }

    public String getCategory() {
        return category.get();
    }

    public SimpleStringProperty categoryProperty() {
        return category;
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public SimpleObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public String getFrequency() {
        return frequency.get();
    }

    public SimpleStringProperty frequencyProperty() {
        return frequency;
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("%.2f - %s (%s) starting %s", amount.get(), category.get(), frequency.get(), startDate.get());
    }
}
