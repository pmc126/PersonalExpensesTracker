package com.expensetracker;

import com.expensetracker.managers.CategoryManager;
// ...existing code...
import com.expensetracker.model.Category;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
// ...existing code...
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class CategoryManagementController {

    @FXML private TextField categoryNameField;
    @FXML private ListView<Category> categoryListView;

    private CategoryManager categoryManager;
    private ExpenseTrackerController expenseTrackerController; // Reference to the main controller

    @FXML
    public void initialize() {
        // The categoryManager will be set by the ExpenseTrackerController
        // categoryListView.setItems(categoryManager.getCategories()); // This will be called after categoryManager is set

        categoryListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        categoryNameField.setText(newValue.getName());
                    } else {
                        categoryNameField.clear();
                    }
                }
        );
    }

    // Setter for the CategoryManager
    public void setCategoryManager(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
        categoryListView.setItems(this.categoryManager.getCategories());
    }

    // Setter for the main controller
    public void setExpenseTrackerController(ExpenseTrackerController expenseTrackerController) {
        this.expenseTrackerController = expenseTrackerController;
    }

    @FXML
    private void handleAddCategory() {
        String categoryName = categoryNameField.getText().trim();
        if (categoryName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Category name cannot be empty.");
            return;
        }
        // For simplicity, using a default user ID, color, and icon.
        // In a real application, these would be user-defined or selected.
        Category newCategory = new Category("default_user", categoryName, "#FFFFFF", "default_icon");
        categoryManager.addCategory(newCategory);
        categoryNameField.clear();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Category added successfully!");
        if (expenseTrackerController != null) {
            expenseTrackerController.updateCategoryComboBoxes();
        }
    }

    @FXML
    private void handleUpdateCategory() {
        Category selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a category to update.");
            return;
        }

        String newCategoryName = categoryNameField.getText().trim();
        if (newCategoryName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Category name cannot be empty.");
            return;
        }

        Category updatedCategory = new Category(
                selectedCategory.getUserId(),
                newCategoryName,
                selectedCategory.getColor(),
                selectedCategory.getIcon()
        );
        categoryManager.updateCategory(selectedCategory, updatedCategory);
        categoryNameField.clear();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Category updated successfully!");
        if (expenseTrackerController != null) {
            expenseTrackerController.updateCategoryComboBoxes();
        }
    }

    @FXML
    private void handleDeleteCategory() {
        Category selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a category to delete.");
            return;
        }

        categoryManager.deleteCategory(selectedCategory);
        categoryNameField.clear();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Category deleted successfully!");
        if (expenseTrackerController != null) {
            expenseTrackerController.updateCategoryComboBoxes();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
