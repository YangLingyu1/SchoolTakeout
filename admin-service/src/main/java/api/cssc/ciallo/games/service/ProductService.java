package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Product;

import java.util.List;

public interface ProductService {
    Product getProductById(Integer id);
    List<Product> getProducts();
    List<Product> getProductsByCategoryId(Integer categoryId);
    Product createProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(Integer id);
    List<Product> searchProducts(String keyword);
}