package com.expensetracker.file;

import com.expensetracker.model.Budget;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.RecurringExpense;
import javafx.collections.ObservableList;
import java.util.List;

public interface FileManager {
    void saveExpenses(List<Expense> expenses);
    ObservableList<Expense> loadExpenses();
    void saveCategories(List<Category> categories);
    ObservableList<Category> loadCategories();
    void saveRecurringExpenses(List<RecurringExpense> recurringExpenses);
    ObservableList<RecurringExpense> loadRecurringExpenses();
    void saveBudgets(List<Budget> budgets);
    ObservableList<Budget> loadBudgets();
}
