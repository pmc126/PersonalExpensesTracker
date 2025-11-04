package com.expensetracker.file;

import com.expensetracker.model.Budget;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.RecurringExpense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
// ...existing code...

public class CsvFileManager implements FileManager {
    private static final String EXPENSES_FILE = "expenses.csv";
    private static final String CATEGORIES_FILE = "categories.csv";
    private static final String RECURRING_EXPENSES_FILE = "recurring_expenses.csv";
    private static final String BUDGETS_FILE = "budgets.csv";

    private static final CSVFormat EXPENSES_FORMAT = CSVFormat.DEFAULT.builder()
        .setHeader("id", "amount", "category", "date", "description")
        .setSkipHeaderRecord(true)
        .build();

    private static final CSVFormat CATEGORIES_FORMAT = CSVFormat.DEFAULT.builder()
        .setHeader("userId", "name", "color", "icon")
        .setSkipHeaderRecord(true)
        .build();

    private static final CSVFormat RECURRING_EXPENSES_FORMAT = CSVFormat.DEFAULT.builder()
        .setHeader("id", "amount", "category", "startDate", "frequency", "description")
        .setSkipHeaderRecord(true)
        .build();

    private static final CSVFormat BUDGETS_FORMAT = CSVFormat.DEFAULT.builder()
        .setHeader("id", "category", "budgetAmount", "period")
        .setSkipHeaderRecord(true)
        .build();

    @Override
    public void saveExpenses(List<Expense> expenses) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(EXPENSES_FILE), StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, EXPENSES_FORMAT.withHeader("id", "amount", "category", "date", "description"))) {
            for (Expense expense : expenses) {
                csvPrinter.printRecord(
                        expense.getId(),
                        String.format("%.2f", expense.getAmount()),
                        expense.getCategory(),
                        expense.getDate().toString(),
                        expense.getDescription() == null ? "" : expense.getDescription()
                );
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ObservableList<Expense> loadExpenses() {
        ObservableList<Expense> expenses = FXCollections.observableArrayList();
        File f = new File(EXPENSES_FILE);
        if (!f.exists()) return expenses;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(EXPENSES_FILE), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, EXPENSES_FORMAT)) {
            for (CSVRecord record : parser) {
                try {
                    String id = record.get("id");
                    double amount = Double.parseDouble(record.get("amount"));
                    String category = record.get("category");
                    LocalDate date = LocalDate.parse(record.get("date"));
                    String description = record.isMapped("description") ? record.get("description") : "";
                    expenses.add(new Expense(id, amount, category, date, description));
                } catch (Exception ex) {
                    // Skip malformed record but continue parsing others
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    @Override
    public void saveCategories(List<Category> categories) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CATEGORIES_FILE), StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CATEGORIES_FORMAT.withHeader("userId", "name", "color", "icon"))) {
            for (Category category : categories) {
                csvPrinter.printRecord(
                        category.getUserId(),
                        category.getName(),
                        category.getColor(),
                        category.getIcon()
                );
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ObservableList<Category> loadCategories() {
        ObservableList<Category> categories = FXCollections.observableArrayList();
        File f = new File(CATEGORIES_FILE);
        if (!f.exists()) return categories;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CATEGORIES_FILE), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, CATEGORIES_FORMAT)) {
            for (CSVRecord record : parser) {
                try {
                    String userId = record.get("userId");
                    String name = record.get("name");
                    String color = record.get("color");
                    String icon = record.get("icon");
                    categories.add(new Category(userId, name, color, icon));
                } catch (Exception ex) {
                    // Skip malformed record
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public void saveRecurringExpenses(List<RecurringExpense> recurringExpenses) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(RECURRING_EXPENSES_FILE), StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, RECURRING_EXPENSES_FORMAT.withHeader("id", "amount", "category", "startDate", "frequency", "description"))) {
            for (RecurringExpense recurringExpense : recurringExpenses) {
                csvPrinter.printRecord(
                        recurringExpense.getId(),
                        String.format("%.2f", recurringExpense.getAmount()),
                        recurringExpense.getCategory(),
                        recurringExpense.getStartDate().toString(),
                        recurringExpense.getFrequency(),
                        recurringExpense.getDescription() == null ? "" : recurringExpense.getDescription()
                );
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ObservableList<RecurringExpense> loadRecurringExpenses() {
        ObservableList<RecurringExpense> recurringExpenses = FXCollections.observableArrayList();
        File f = new File(RECURRING_EXPENSES_FILE);
        if (!f.exists()) return recurringExpenses;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(RECURRING_EXPENSES_FILE), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, RECURRING_EXPENSES_FORMAT)) {
            for (CSVRecord record : parser) {
                try {
                    String id = record.get("id");
                    double amount = Double.parseDouble(record.get("amount"));
                    String category = record.get("category");
                    LocalDate startDate = LocalDate.parse(record.get("startDate"));
                    String frequency = record.get("frequency");
                    String description = record.isMapped("description") ? record.get("description") : "";
                    recurringExpenses.add(new RecurringExpense(id, amount, category, startDate, frequency, description));
                } catch (Exception ex) {
                    // Skip malformed record but continue parsing others
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recurringExpenses;
    }

    @Override
    public void saveBudgets(List<Budget> budgets) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(BUDGETS_FILE), StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, BUDGETS_FORMAT.withHeader("id", "category", "budgetAmount", "period"))) {
            for (Budget budget : budgets) {
                csvPrinter.printRecord(
                        budget.getId(),
                        budget.getCategory(),
                        String.format("%.2f", budget.getBudgetAmount()),
                        budget.getPeriod()
                );
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ObservableList<Budget> loadBudgets() {
        ObservableList<Budget> budgets = FXCollections.observableArrayList();
        File f = new File(BUDGETS_FILE);
        if (!f.exists()) return budgets;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(BUDGETS_FILE), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, BUDGETS_FORMAT)) {
            for (CSVRecord record : parser) {
                try {
                    String id = record.get("id");
                    String category = record.get("category");
                    double budgetAmount = Double.parseDouble(record.get("budgetAmount"));
                    String period = record.get("period");
                    budgets.add(new Budget(id, category, budgetAmount, period));
                } catch (Exception ex) {
                    // Skip malformed record but continue parsing others
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return budgets;
    }
}
