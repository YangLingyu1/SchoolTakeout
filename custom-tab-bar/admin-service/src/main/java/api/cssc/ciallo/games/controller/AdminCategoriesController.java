package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Category;
import api.cssc.ciallo.games.service.CategoryService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoriesController {

    @Autowired
    private CategoryService categoryService;

    // 获取分类列表
    @GetMapping
    public Response<?> getCategories() {
        List<Category> categories = categoryService.getCategories();
        return Response.success(categories);
    }

    // 获取分类详情
    @GetMapping("/{id}")
    public Response<?> getCategoryById(@PathVariable Integer id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return Response.notFound("分类不存在");
        }
        return Response.success(category);
    }

    // 创建分类
    @PostMapping
    public Response<?> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return Response.success(createdCategory);
    }

    // 更新分类
    @PutMapping("/{id}")
    public Response<?> updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        category.setId(id);
        Category updatedCategory = categoryService.updateCategory(category);
        return Response.success(updatedCategory);
    }

    // 删除分类
    @DeleteMapping("/{id}")
    public Response<?> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return Response.success();
    }
}