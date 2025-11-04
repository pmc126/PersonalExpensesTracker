package com.expensetracker;

import com.expensetracker.file.CsvFileManager;
import com.expensetracker.file.FileManager;
import com.expensetracker.managers.BudgetManager;
import com.expensetracker.managers.CategoryManager;
import com.expensetracker.managers.ExpenseManager;
import com.expensetracker.managers.RecurringExpenseManager;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.util.ExpenseFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class ExpenseTrackerController {

    private static final Logger LOGGER = Logger.getLogger(ExpenseTrackerController.class.getName());

    // FXML Injections for Add Expense Form
    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextArea descriptionArea;
    @FXML private Button saveButton; // Added saveButton FXML injection

    // FXML Injections for View Expenses/Summary
    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, Double> amountColumn;
    @FXML private TableColumn<Expense, String> categoryColumn;
    @FXML private TableColumn<Expense, String> dateColumn;
    @FXML private TableColumn<Expense, String> descriptionColumn;
    @FXML private Label totalSpentLabel;

    // FXML Injections for Filtering
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> filterCategoryComboBox;

    // FXML Injections for Search
    @FXML private TextField searchField;

    private ExpenseManager expenseManager;
    private CategoryManager categoryManager;
    private RecurringExpenseManager recurringExpenseManager;
    private BudgetManager budgetManager;
    private ExpenseFilter expenseFilter;
    private Expense editingExpense; // Field to hold the expense being edited

    /**
     * Initializes the controller class. Automatically called after the FXML is loaded.
     */
    @FXML
    public void initialize() {
        // Initialize File Manager and Managers
        FileManager fileManager = new CsvFileManager();
        expenseManager = new ExpenseManager(fileManager);
        categoryManager = new CategoryManager(fileManager);
        recurringExpenseManager = new RecurringExpenseManager(fileManager, expenseManager);
        budgetManager = new BudgetManager(fileManager);

        // Generate any due recurring expenses
        recurringExpenseManager.generateDueExpenses();

        // 1. Setup Data
        expenseFilter = new ExpenseFilter(expenseManager.getExpenses());

        // 2. Setup Add Expense Form
        updateCategoryComboBoxes();
        datePicker.setValue(LocalDate.now()); // Set default date

        // 3. Setup TableView Columns
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Bind the TableView to the ExpenseFilter's sorted list
        expenseTable.setItems(expenseFilter.getSortedList());
        expenseFilter.bindSort(expenseTable);

        // Update summary
        calculateTotalSpent();

        // Format amount column to currency (optional clean design enhancement)
        amountColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("K %.2f", amount)); // Use K as a placeholder currency
                }
            }
        });

        // Add listeners to filter controls to re-apply filter when they change
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> filterExpenses());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> filterExpenses());
        filterCategoryComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filterExpenses());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> handleSearchExpenses());
    }

    public void updateCategoryComboBoxes() {
        ObservableList<String> categoryNames = categoryManager.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        if (categoryNames.isEmpty()) {
            // Add some default categories if none exist
            categoryManager.addCategory(new Category("default_user", "Food", "#FF0000", "food_icon"));
            categoryManager.addCategory(new Category("default_user", "Transport", "#00FF00", "transport_icon"));
            categoryManager.addCategory(new Category("default_user", "School", "#0000FF", "school_icon"));
            categoryManager.addCategory(new Category("default_user", "Utilities", "#FFA500", "utilities_icon"));
            categoryManager.addCategory(new Category("default_user", "Entertainment", "#800080", "entertainment_icon"));
            categoryNames = categoryManager.getCategories().stream()
                    .map(Category::getName)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }
        categoryComboBox.getItems().setAll(categoryNames); // Use setAll to refresh
        
        filterCategoryComboBox.getItems().setAll("All Categories"); // Clear and add "All Categories"
        filterCategoryComboBox.getItems().addAll(categoryNames);
        filterCategoryComboBox.setValue("All Categories");
    }

    /**
     * Handles the saving of a new expense.
     */
    @FXML
    private void saveExpense() {
        // Basic Validation
        if (amountField.getText().isEmpty() || categoryComboBox.getValue() == null || datePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in Amount, Category, and Date.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Amount must be a valid number.");
            return;
        }

        String category = categoryComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String description = descriptionArea.getText().trim();

        if (editingExpense == null) {
            // Create, Save, and Update UI for new expense
            Expense newExpense = new Expense(amount, category, date, description);
            expenseManager.addExpense(newExpense); // Save to file
            showAlert(Alert.AlertType.INFORMATION, "Success", "Expense recorded successfully!");
        } else {
            // Update existing expense
            Expense updatedExpense = new Expense(editingExpense.getId(), amount, category, date, description);
            expenseManager.updateExpense(editingExpense, updatedExpense);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Expense updated successfully!");
            editingExpense = null; // Clear editing state
            saveButton.setText("➕ Save Expense"); // Reset button text
        }

        // Reset form fields
        amountField.clear();
        descriptionArea.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(LocalDate.now());

        // Update summary
        calculateTotalSpent();
    }

    /**
     * Handles deleting a selected expense.
     */
    @FXML
    private void deleteExpense() {
        Expense selectedExpense = expenseTable.getSelectionModel().getSelectedItem();

        if (selectedExpense == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an expense to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Expense");
        confirmation.setContentText("Are you sure you want to delete this expense?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            expenseManager.deleteExpense(selectedExpense);
            calculateTotalSpent();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Expense deleted successfully!");
        }
    }

    /**
     * Handles editing a selected expense.
     */
    @FXML
    private void handleEditExpense() {
        Expense selectedExpense = expenseTable.getSelectionModel().getSelectedItem();

        if (selectedExpense == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an expense to edit.");
            return;
        }

        editingExpense = selectedExpense;

        // Populate the form fields with the selected expense's data
        amountField.setText(String.valueOf(selectedExpense.getAmount()));
        categoryComboBox.setValue(selectedExpense.getCategory());
        datePicker.setValue(selectedExpense.getDate());
        descriptionArea.setText(selectedExpense.getDescription());

        saveButton.setText("✔ Update Expense"); // Change button text
    }

    /**
     * Filters the expenses based on the selected criteria.
     */
    @FXML
    private void filterExpenses() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String category = filterCategoryComboBox.getValue();

        expenseFilter.setDateRange(startDate, endDate);
        expenseFilter.setCategory(category.equals("All Categories") ? null : category);

        // Re-apply search filter after other filters
        handleSearchExpenses();
        calculateTotalSpent();
    }

    @FXML
    private void handleManageCategories() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/CategoryManagement.fxml"));
            Parent root = loader.load();

            CategoryManagementController categoryManagementController = loader.getController();
            // Pass the existing categoryManager to the new controller
            // This ensures that both controllers operate on the same data instance
            categoryManagementController.setCategoryManager(this.categoryManager);
            categoryManagementController.setExpenseTrackerController(this); // Pass reference to main controller

            Stage stage = new Stage();
            stage.setTitle("Manage Categories");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Block interaction with main window
            stage.showAndWait(); // Wait for the category management window to be closed

            // After the category management window is closed, refresh the category combo boxes
            updateCategoryComboBoxes();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not load category management window.", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load category management window.");
        }
    }

    @FXML
    private void handleGenerateReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/ReportView.fxml"));
            Parent root = loader.load();

            ReportViewController reportViewController = loader.getController();
            reportViewController.setExpenses(expenseFilter.getSortedList()); // Pass filtered expenses

            Stage stage = new Stage();
            stage.setTitle("Expense Report");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Block interaction with main window
            stage.showAndWait(); // Wait for the report window to be closed
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not load report window.", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load report window.");
        }
    }

    @FXML
    private void handleImportExpenses() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Expenses CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(expenseTable.getScene().getWindow());
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file));
                 CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader("id", "amount", "category", "date", "description").setSkipHeaderRecord(true).setTrim(true).build())) {

                for (CSVRecord record : parser) {
                    try {
                        String id = record.isMapped("id") ? record.get("id") : java.util.UUID.randomUUID().toString();
                        double amount = Double.parseDouble(record.get("amount"));
                        String category = record.get("category");
                        LocalDate date = LocalDate.parse(record.get("date"));
                        String description = record.isMapped("description") ? record.get("description") : "";
                        expenseManager.addExpense(new Expense(id, amount, category, date, description));
                    } catch (NumberFormatException | DateTimeParseException ex) {
                        LOGGER.log(Level.WARNING, "Skipping malformed record during import: " + ex.getMessage());
                        showAlert(Alert.AlertType.ERROR, "Import Error", "Skipping malformed record: " + ex.getMessage());
                    } catch (IllegalArgumentException ex) {
                        // Handle cases where a required header is missing
                        LOGGER.log(Level.SEVERE, "Missing required CSV column during import: " + ex.getMessage());
                        showAlert(Alert.AlertType.ERROR, "Import Error", "Missing required CSV column: " + ex.getMessage());
                        break; // Stop parsing if a critical column is missing
                    }
                }
                calculateTotalSpent();
                updateCategoryComboBoxes(); // Update category dropdowns in case new categories were imported
                showAlert(Alert.AlertType.INFORMATION, "Import Success", "Expenses imported successfully from " + file.getName());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error reading import file: " + e.getMessage(), e);
                showAlert(Alert.AlertType.ERROR, "Import Error", "Error reading file: " + e.getMessage());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An unexpected error occurred during import.", e);
                showAlert(Alert.AlertType.ERROR, "Import Error", "An unexpected error occurred during import: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleExportExpenses() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Expenses CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("expenses_export.csv");
        File file = fileChooser.showSaveDialog(expenseTable.getScene().getWindow());
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader("id", "amount", "category", "date", "description").setTrim(true).build())) {

                for (Expense expense : expenseManager.getExpenses()) {
                    csvPrinter.printRecord(
                            expense.getId(),
                            String.format("%.2f", expense.getAmount()),
                            expense.getCategory(),
                            expense.getDate().toString(),
                            expense.getDescription()
                    );
                }
                csvPrinter.flush();
                showAlert(Alert.AlertType.INFORMATION, "Export Success", "Expenses exported successfully to " + file.getName());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error writing export file: " + e.getMessage(), e);
                showAlert(Alert.AlertType.ERROR, "Export Error", "Error writing file: " + e.getMessage());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An unexpected error occurred during export.", e);
                showAlert(Alert.AlertType.ERROR, "Export Error", "An unexpected error occurred during export: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSearchExpenses() {
        String searchText = searchField.getText().toLowerCase();
        expenseFilter.setSearchQuery(searchText);
        calculateTotalSpent();
    }

     @FXML
    private void handleManageBudgets() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/BudgetManagement.fxml"));
            Parent root = loader.load();

            BudgetManagementController budgetManagementController = loader.getController();
            budgetManagementController.setBudgetManager(budgetManager);
            budgetManagementController.setCategoryManager(categoryManager);
             budgetManagementController.setExpenseTrackerController(this);

            Stage stage = new Stage();
            stage.setTitle("Manage Budgets");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not load budget management window.", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load budget management window.");
        }
    }

    @FXML
    private void handleManageRecurringExpenses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/RecurringExpenseManagement.fxml"));
            Parent root = loader.load();

            RecurringExpenseManagementController recurringExpenseManagementController = loader.getController();
            recurringExpenseManagementController.setRecurringExpenseManager(recurringExpenseManager);
            recurringExpenseManagementController.setCategoryManager(categoryManager);
            recurringExpenseManagementController.setExpenseTrackerController(this);

            Stage stage = new Stage();
            stage.setTitle("Manage Recurring Expenses");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // After the recurring expense management window is closed, generate any new due expenses
            recurringExpenseManager.generateDueExpenses();
            // Refresh the main expense table and summary
            // expenseFilter.setExpenses(expenseManager.getExpenses()); // Removed this line
            calculateTotalSpent();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not load recurring expense management window.", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load recurring expense management window.");
        }
    }

    /**
     * Calculates and updates the total amount spent label based on filtered data.
     */
    private void calculateTotalSpent() {
        double total = expenseFilter.getSortedList().stream()
                .mapToDouble(Expense::getAmount)
                .sum();
        totalSpentLabel.setText(String.format("Total Spent: K %.2f", total));
    }

    /**
     * Helper method to display an Alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
