package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Category;
import api.cssc.ciallo.games.service.CategoryService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    // 获取所有分类
    @GetMapping
    public Response<?> getCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Response.success(categories);
    }

    // 添加分类
    @PostMapping
    public Response<?> addCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return Response.success(createdCategory);
    }

    // 更新分类
    @PutMapping
    public Response<?> updateCategory(@RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(category);
        return Response.success(updatedCategory);
    }

    // 删除分类
    @DeleteMapping
    public Response<?> deleteCategory(@RequestParam Integer id) {
        categoryService.deleteCategory(id);
        return Response.success(null);
    }
}
