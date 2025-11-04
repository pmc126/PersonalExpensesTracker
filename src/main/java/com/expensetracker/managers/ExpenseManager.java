package com.expensetracker.managers;

import com.expensetracker.file.FileManager;
import com.expensetracker.model.Expense;
import javafx.collections.ObservableList;

public class ExpenseManager {
    private final ObservableList<Expense> expenses;
    private final FileManager fileManager;

    public ExpenseManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.expenses = fileManager.loadExpenses();
    }

    public ObservableList<Expense> getExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        fileManager.saveExpenses(expenses);
    }

    public void deleteExpense(Expense expense) {
        expenses.remove(expense);
        fileManager.saveExpenses(expenses);
    }

    public void updateExpense(Expense oldExpense, Expense newExpense) {
        int index = expenses.indexOf(oldExpense);
        if (index != -1) {
            expenses.set(index, newExpense);
            fileManager.saveExpenses(expenses);
        }
    }
}
