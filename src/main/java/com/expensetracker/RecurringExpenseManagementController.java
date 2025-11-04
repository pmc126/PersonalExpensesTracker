package com.expensetracker;

import com.expensetracker.managers.CategoryManager;
import com.expensetracker.managers.RecurringExpenseManager;
import com.expensetracker.model.Category;
import com.expensetracker.model.RecurringExpense;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

public class RecurringExpenseManagementController {

    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private ComboBox<String> frequencyComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private ListView<RecurringExpense> recurringExpenseListView;

    private RecurringExpenseManager recurringExpenseManager;
    private CategoryManager categoryManager;
    private ExpenseTrackerController expenseTrackerController;

    @FXML
    public void initialize() {
        frequencyComboBox.setItems(FXCollections.observableArrayList("DAILY", "WEEKLY", "MONTHLY", "YEARLY"));
        frequencyComboBox.setValue("MONTHLY");
        startDatePicker.setValue(LocalDate.now());

        recurringExpenseListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        amountField.setText(String.valueOf(newValue.getAmount()));
                        categoryComboBox.setValue(newValue.getCategory());
                        startDatePicker.setValue(newValue.getStartDate());
                        frequencyComboBox.setValue(newValue.getFrequency());
                        descriptionArea.setText(newValue.getDescription());
                    } else {
                        clearFields();
                    }
                }
        );
    }

    public void setRecurringExpenseManager(RecurringExpenseManager recurringExpenseManager) {
        this.recurringExpenseManager = recurringExpenseManager;
        recurringExpenseListView.setItems(recurringExpenseManager.getRecurringExpenses());
    }

    public void setCategoryManager(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
        categoryComboBox.setItems(categoryManager.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    public void setExpenseTrackerController(ExpenseTrackerController expenseTrackerController) {
        this.expenseTrackerController = expenseTrackerController;
    }

    @FXML
    private void handleAddRecurringExpense() {
        if (!validateInput()) return;

        double amount = Double.parseDouble(amountField.getText());
        String category = categoryComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        String frequency = frequencyComboBox.getValue();
        String description = descriptionArea.getText().trim();

        RecurringExpense newRecurringExpense = new RecurringExpense(amount, category, startDate, frequency, description);
        recurringExpenseManager.addRecurringExpense(newRecurringExpense);
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Recurring expense added successfully!");
        if (expenseTrackerController != null) {
            expenseTrackerController.updateCategoryComboBoxes(); // Refresh main view if needed
        }
    }

    @FXML
    private void handleUpdateRecurringExpense() {
        RecurringExpense selectedRecurringExpense = recurringExpenseListView.getSelectionModel().getSelectedItem();
        if (selectedRecurringExpense == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a recurring expense to update.");
            return;
        }

        if (!validateInput()) return;

        double amount = Double.parseDouble(amountField.getText());
        String category = categoryComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        String frequency = frequencyComboBox.getValue();
        String description = descriptionArea.getText().trim();

        RecurringExpense updatedRecurringExpense = new RecurringExpense(
                selectedRecurringExpense.getId(),
                amount,
                category,
                startDate,
                frequency,
                description
        );
        recurringExpenseManager.updateRecurringExpense(selectedRecurringExpense, updatedRecurringExpense);
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Recurring expense updated successfully!");
        if (expenseTrackerController != null) {
            expenseTrackerController.updateCategoryComboBoxes(); // Refresh main view if needed
        }
    }

    @FXML
    private void handleDeleteRecurringExpense() {
        RecurringExpense selectedRecurringExpense = recurringExpenseListView.getSelectionModel().getSelectedItem();
        if (selectedRecurringExpense == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a recurring expense to delete.");
            return;
        }

        recurringExpenseManager.deleteRecurringExpense(selectedRecurringExpense);
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Recurring expense deleted successfully!");
        if (expenseTrackerController != null) {
            expenseTrackerController.updateCategoryComboBoxes(); // Refresh main view if needed
        }
    }

    private boolean validateInput() {
        String amountText = amountField.getText().trim();
        String category = categoryComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        String frequency = frequencyComboBox.getValue();

        if (amountText.isEmpty() || category == null || category.isEmpty() || startDate == null || frequency == null || frequency.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields (Amount, Category, Start Date, Frequency).");
            return false;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Amount must be positive.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Amount must be a valid number.");
            return false;
        }
        return true;
    }

    private void clearFields() {
        amountField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        startDatePicker.setValue(LocalDate.now());
        frequencyComboBox.setValue("MONTHLY");
        descriptionArea.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
