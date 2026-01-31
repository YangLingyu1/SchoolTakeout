package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Category;

import java.util.List;

public interface CategoryService {
    Category getCategoryById(Integer id);
    List<Category> getCategories();
    Category createCategory(Category category);
    Category updateCategory(Category category);
    void deleteCategory(Integer id);
}