package com.expensetracker.managers;

import com.expensetracker.file.FileManager;
import com.expensetracker.model.Budget;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class BudgetManager {
    private final ObservableList<Budget> budgets;
    private final FileManager fileManager;

    public BudgetManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.budgets = fileManager.loadBudgets();
    }

    public ObservableList<Budget> getBudgets() {
        return budgets;
    }

    public void addBudget(Budget budget) {
        budgets.add(budget);
        fileManager.saveBudgets(budgets);
    }

    public void deleteBudget(Budget budget) {
        budgets.remove(budget);
        fileManager.saveBudgets(budgets);
    }

    public void updateBudget(Budget oldBudget, Budget newBudget) {
        int index = budgets.indexOf(oldBudget);
        if (index != -1) {
            budgets.set(index, newBudget);
            fileManager.saveBudgets(budgets);
        }
    }
}
