package com.expensetracker.util;

import com.expensetracker.model.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDate;
import java.util.function.Predicate;

public class ExpenseFilter {
    private final FilteredList<Expense> filteredList;
    private final SortedList<Expense> sortedList;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private String categoryFilter;
    private Double minAmount;
    private Double maxAmount;
    private String searchText;

    public ExpenseFilter(ObservableList<Expense> expenses) {
        this.filteredList = new FilteredList<>(expenses);
        this.sortedList = new SortedList<>(filteredList);
        
        // Set default filter predicate
        updateFilterPredicate();
    }

    public void setDateRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        updateFilterPredicate();
    }

    public void setCategory(String category) {
        this.categoryFilter = category;
        updateFilterPredicate();
    }

    public void setAmountRange(Double minAmount, Double maxAmount) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        updateFilterPredicate();
    }

    public void setSearchQuery(String searchText) { // Added this method
        this.searchText = searchText;
        updateFilterPredicate();
    }

    private void updateFilterPredicate() {
        filteredList.setPredicate(expense -> {
            if (expense == null) return false;

            boolean matches = true;

            // Date range filter
            if (startDate != null && expense.getDate().isBefore(startDate)) {
                matches = false;
            }
            if (endDate != null && expense.getDate().isAfter(endDate)) {
                matches = false;
            }

            // Category filter
            if (categoryFilter != null && !categoryFilter.isEmpty() && 
                !categoryFilter.equals("All Categories") && 
                !expense.getCategory().equals(categoryFilter)) { // Changed "All" to "All Categories"
                matches = false;
            }

            // Amount range filter
            if (minAmount != null && expense.getAmount() < minAmount) {
                matches = false;
            }
            if (maxAmount != null && expense.getAmount() > maxAmount) {
                matches = false;
            }

            // Search text filter
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseSearch = searchText.toLowerCase();
                boolean containsSearch = expense.getDescription().toLowerCase().contains(lowerCaseSearch) ||
                                      expense.getCategory().toLowerCase().contains(lowerCaseSearch) ||
                                      String.valueOf(expense.getAmount()).contains(lowerCaseSearch);
                if (!containsSearch) {
                    matches = false;
                }
            }

            return matches;
        });
    }

    public SortedList<Expense> getSortedList() {
        return sortedList;
    }

    public void bindSort(javafx.scene.control.TableView<Expense> tableView) {
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
    }

    public void clearFilters() {
        startDate = null;
        endDate = null;
        categoryFilter = null;
        minAmount = null;
        maxAmount = null;
        searchText = null;
        updateFilterPredicate();
    }
}
