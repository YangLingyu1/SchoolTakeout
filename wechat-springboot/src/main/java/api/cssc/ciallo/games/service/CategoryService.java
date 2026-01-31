package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Category;
import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    Category getCategoryById(Integer id);
    List<Category> getAllCategories();
    Category updateCategory(Category category);
    void deleteCategory(Integer id);
}
