package com.expensetracker.managers;

import com.expensetracker.file.FileManager;
import com.expensetracker.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CategoryManager {
    private final ObservableList<Category> categories;
    private final FileManager fileManager;

    public CategoryManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.categories = fileManager.loadCategories();
    }

    public ObservableList<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        categories.add(category);
        fileManager.saveCategories(categories);
    }

    public void deleteCategory(Category category) {
        categories.remove(category);
        fileManager.saveCategories(categories);
    }

    public void updateCategory(Category oldCategory, Category newCategory) {
        int index = categories.indexOf(oldCategory);
        if (index != -1) {
            categories.set(index, newCategory);
            fileManager.saveCategories(categories);
        }
    }
}
