package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Product;
import api.cssc.ciallo.games.service.ProductService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    @Autowired
    private ProductService productService;

    // 获取所有产品
    @GetMapping
    public Response<?> getProducts(@RequestParam(required = false) Integer categoryId, @RequestParam(required = false) String keyword) {
        List<Product> products;
        if (categoryId != null) {
            products = productService.getProductsByCategoryId(categoryId);
        } else if (keyword != null) {
            products = productService.searchProductsByName(keyword);
        } else {
            products = productService.getAllProducts();
        }
        return Response.success(products);
    }

    // 获取单个产品详情
    @GetMapping("/{id}")
    public Response<?> getProductById(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return Response.notFound("产品不存在");
        }
        return Response.success(product);
    }

    // 添加产品
    @PostMapping
    public Response<?> addProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return Response.success(createdProduct);
    }

    // 更新产品
    @PutMapping
    public Response<?> updateProduct(@RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(product);
        return Response.success(updatedProduct);
    }

    // 删除产品
    @DeleteMapping
    public Response<?> deleteProduct(@RequestParam Integer id) {
        productService.deleteProduct(id);
        return Response.success(null);
    }
}
