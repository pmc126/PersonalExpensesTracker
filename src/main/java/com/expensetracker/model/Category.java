package com.expensetracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Category {
    private final String id;
    private String name;
    private String color;
    private final String userId;
    private Category parent;
    private List<Category> subcategories;
    private String icon;

    public Category(String userId, String name, String color, String icon) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.subcategories = new ArrayList<>();
    }

    public void addSubcategory(Category category) {
        if (category != null && !category.getId().equals(this.id)) {
            category.setParent(this);
            subcategories.add(category);
        }
    }

    public void removeSubcategory(Category category) {
        if (category != null) {
            category.setParent(null);
            subcategories.remove(category);
        }
    }

    public boolean isSubcategory() {
        return parent != null;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public String getUserId() { return userId; }
    public Category getParent() { return parent; }
    public List<Category> getSubcategories() { return new ArrayList<>(subcategories); }
    public String getIcon() { return icon; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setColor(String color) { this.color = color; }
    public void setParent(Category parent) { this.parent = parent; }
    public void setIcon(String icon) { this.icon = icon; }
}