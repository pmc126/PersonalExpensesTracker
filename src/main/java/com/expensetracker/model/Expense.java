package com.expensetracker.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.UUID;



public class Expense {



    private final String id;

    private final SimpleDoubleProperty amount;

    private final SimpleStringProperty category;

    private final SimpleObjectProperty<LocalDate> date;

    private final SimpleStringProperty description;



    private static final DateTimeFormatter CSV_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");



        // Constructor with LocalDate for DatePicker and programmatic creation



        public Expense(double amount, String category, LocalDate date, String description) {



            this(UUID.randomUUID().toString(), amount, category, date, description);



        }



    



        // Constructor for loading from CSV (String date)



        public Expense(double amount, String category, String dateString, String description) {



            this(UUID.randomUUID().toString(), amount, category, LocalDate.parse(dateString, CSV_FORMATTER), description);



        }



    



        // Full constructor including ID



        public Expense(String id, double amount, String category, LocalDate date, String description) {



            this.id = id;



            this.amount = new SimpleDoubleProperty(amount);



            this.category = new SimpleStringProperty(category);



            this.date = new SimpleObjectProperty<>(date);



            this.description = new SimpleStringProperty(description == null ? "" : description);



        }



    // Properties for TableView bindings

    public SimpleDoubleProperty amountProperty() { return amount; }

    public SimpleStringProperty categoryProperty() { return category; }

    public SimpleObjectProperty<LocalDate> dateProperty() { return date; }

    public SimpleStringProperty descriptionProperty() { return description; }



    // Getters for logic

    public String getId() { return id; }

    public double getAmount() { return amount.get(); }

    public String getCategory() { return category.get(); }

    public LocalDate getDate() { return date.get(); }

    public String getDescription() { return description.get(); }



}
