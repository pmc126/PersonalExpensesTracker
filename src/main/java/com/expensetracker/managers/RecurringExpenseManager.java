package com.expensetracker.managers;

import com.expensetracker.file.FileManager;
import com.expensetracker.model.Expense;
import com.expensetracker.model.RecurringExpense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class RecurringExpenseManager {
    private final ObservableList<RecurringExpense> recurringExpenses;
    private final FileManager fileManager;
    private final ExpenseManager expenseManager;

    public RecurringExpenseManager(FileManager fileManager, ExpenseManager expenseManager) {
        this.fileManager = fileManager;
        this.expenseManager = expenseManager;
        this.recurringExpenses = fileManager.loadRecurringExpenses();
    }

    public ObservableList<RecurringExpense> getRecurringExpenses() {
        return recurringExpenses;
    }

    public void addRecurringExpense(RecurringExpense recurringExpense) {
        recurringExpenses.add(recurringExpense);
        fileManager.saveRecurringExpenses(recurringExpenses);
    }

    public void deleteRecurringExpense(RecurringExpense recurringExpense) {
        recurringExpenses.remove(recurringExpense);
        fileManager.saveRecurringExpenses(recurringExpenses);
    }

    public void updateRecurringExpense(RecurringExpense oldRecurringExpense, RecurringExpense newRecurringExpense) {
        int index = recurringExpenses.indexOf(oldRecurringExpense);
        if (index != -1) {
            recurringExpenses.set(index, newRecurringExpense);
            fileManager.saveRecurringExpenses(recurringExpenses);
        }
    }

    public void generateDueExpenses() {
        LocalDate today = LocalDate.now();
        for (RecurringExpense re : recurringExpenses) {
            LocalDate nextDueDate = re.getStartDate();
            // Logic to determine if a recurring expense is due
            // This is a simplified example, a real implementation would be more complex
            // and track the last generated date for each recurring expense.
            if (nextDueDate.isBefore(today) || nextDueDate.isEqual(today)) {
                // Create a new Expense from the RecurringExpense template
                Expense newExpense = new Expense(
                        re.getAmount(),
                        re.getCategory(),
                        today, // Use today's date for the generated expense
                        re.getDescription()
                );
                expenseManager.addExpense(newExpense);
                // In a real app, you'd update the recurring expense's last generated date
                // or next due date to prevent duplicate generation.
            }
        }
    }
}
