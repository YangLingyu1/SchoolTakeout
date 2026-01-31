package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Product;
import api.cssc.ciallo.games.service.ProductService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductsController {

    @Autowired
    private ProductService productService;

    // 获取商品列表
    @GetMapping
    public Response<?> getProducts(@RequestParam(required = false) Integer categoryId) {
        List<Product> products;
        if (categoryId != null) {
            products = productService.getProductsByCategoryId(categoryId);
        } else {
            products = productService.getProducts();
        }
        return Response.success(products);
    }

    // 获取商品详情
    @GetMapping("/{id}")
    public Response<?> getProductById(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return Response.notFound("商品不存在");
        }
        return Response.success(product);
    }

    // 创建商品
    @PostMapping
    public Response<?> createProduct(@RequestBody Product product) {
        System.out.println("创建商品数据: " + product);
        System.out.println("商品图片URL: " + product.getImageUrl());
        Product createdProduct = productService.createProduct(product);
        return Response.success(createdProduct);
    }

    // 更新商品
    @PutMapping("/{id}")
    public Response<?> updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        product.setId(id);
        System.out.println("更新商品数据: " + product);
        System.out.println("商品图片URL: " + product.getImageUrl());
        Product updatedProduct = productService.updateProduct(product);
        return Response.success(updatedProduct);
    }

    // 删除商品
    @DeleteMapping("/{id}")
    public Response<?> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return Response.success();
    }
}