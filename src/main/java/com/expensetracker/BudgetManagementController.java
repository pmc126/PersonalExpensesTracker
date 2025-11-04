package com.expensetracker;

import com.expensetracker.managers.BudgetManager;
import com.expensetracker.managers.CategoryManager;
import com.expensetracker.model.Budget;
import com.expensetracker.model.Category;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.stream.Collectors;

public class BudgetManagementController {

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField budgetAmountField;
    @FXML private ComboBox<String> periodComboBox;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private ListView<Budget> budgetListView;

    private BudgetManager budgetManager;
    private CategoryManager categoryManager;
    private ExpenseTrackerController expenseTrackerController; // Reference to the main controller

    @FXML
    public void initialize() {
        periodComboBox.setItems(FXCollections.observableArrayList("MONTHLY", "WEEKLY", "YEARLY"));
        periodComboBox.setValue("MONTHLY");

        budgetListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        categoryComboBox.setValue(newValue.getCategory());
                        budgetAmountField.setText(String.valueOf(newValue.getBudgetAmount()));
                        periodComboBox.setValue(newValue.getPeriod());
                    } else {
                        categoryComboBox.getSelectionModel().clearSelection();
                        budgetAmountField.clear();
                        periodComboBox.getSelectionModel().clearSelection();
                    }
                }
        );
    }

    public void setBudgetManager(BudgetManager budgetManager) {
        this.budgetManager = budgetManager;
        budgetListView.setItems(budgetManager.getBudgets());
    }

    public void setCategoryManager(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
        categoryComboBox.setItems(categoryManager.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    // Setter for the main controller
    public void setExpenseTrackerController(ExpenseTrackerController expenseTrackerController) {
        this.expenseTrackerController = expenseTrackerController;
    }

    @FXML
    private void handleAddBudget() {
        String category = categoryComboBox.getValue();
        String budgetAmountText = budgetAmountField.getText().trim();
        String period = periodComboBox.getValue();

        if (category == null || category.isEmpty() || budgetAmountText.isEmpty() || period == null || period.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        double budgetAmount;
        try {
            budgetAmount = Double.parseDouble(budgetAmountText);
            if (budgetAmount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Budget amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Budget amount must be a valid number.");
            return;
        }

        Budget newBudget = new Budget(category, budgetAmount, period);
        budgetManager.addBudget(newBudget);
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Budget added successfully!");
        if (expenseTrackerController != null) {
            expenseTrackerController.updateCategoryComboBoxes();
        }
    }

    @FXML
    private void handleUpdateBudget() {
        Budget selectedBudget = budgetListView.getSelectionModel().getSelectedItem();
        if (selectedBudget == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a budget to update.");
            return;
        }

        String category = categoryComboBox.getValue();
        String budgetAmountText = budgetAmountField.getText().trim();
        String period = periodComboBox.getValue();

        if (category == null || category.isEmpty() || budgetAmountText.isEmpty() || period == null || period.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        double budgetAmount;
        try {
            budgetAmount = Double.parseDouble(budgetAmountText);
            if (budgetAmount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Budget amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Budget amount must be a valid number.");
            return;
        }

        Budget updatedBudget = new Budget(selectedBudget.getId(), category, budgetAmount, period);
        budgetManager.updateBudget(selectedBudget, updatedBudget);
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Budget updated successfully!");
        if (expenseTrackerController != null) {
            expenseTrackerController.updateCategoryComboBoxes();
        }
    }

    @FXML
    private void handleDeleteBudget() {
        Budget selectedBudget = budgetListView.getSelectionModel().getSelectedItem();
        if (selectedBudget == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a budget to delete.");
            return;
        }

        budgetManager.deleteBudget(selectedBudget);
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Budget deleted successfully!");
        if (expenseTrackerController != null) {
            expenseTrackerController.updateCategoryComboBoxes();
        }
    }

    private void clearFields() {
        categoryComboBox.getSelectionModel().clearSelection();
        budgetAmountField.clear();
        periodComboBox.getSelectionModel().clearSelection();
        periodComboBox.setValue("MONTHLY"); // Reset to default
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
