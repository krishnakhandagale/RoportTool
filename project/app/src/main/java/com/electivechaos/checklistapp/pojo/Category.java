package com.electivechaos.checklistapp.pojo;

public class Category {
    String categoryName;
    String categoryDescription;
    int categoryId;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    @Override
    public String toString() {
        return String.valueOf(categoryName);
    }

    @Override
    public boolean equals(Object obj) {
        Category category = (Category) obj;
        return this.categoryName.trim().equals(category.getCategoryName().trim());
    }
}
