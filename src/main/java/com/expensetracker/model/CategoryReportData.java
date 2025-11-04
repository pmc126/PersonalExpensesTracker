package com.expensetracker.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class CategoryReportData {
    private final SimpleStringProperty categoryName;
    private final SimpleDoubleProperty totalAmount;
    private final SimpleDoubleProperty percentage;

    public CategoryReportData(String categoryName, double totalAmount, double percentage) {
        this.categoryName = new SimpleStringProperty(categoryName);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.percentage = new SimpleDoubleProperty(percentage);
    }

    public String getCategoryName() {
        return categoryName.get();
    }

    public SimpleStringProperty categoryNameProperty() {
        return categoryName;
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    public SimpleDoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    public double getPercentage() {
        return percentage.get();
    }

    public SimpleDoubleProperty percentageProperty() {
        return percentage;
    }
}
