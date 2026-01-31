package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Product;
import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product getProductById(Integer id);
    List<Product> getProductsByCategoryId(Integer categoryId);
    List<Product> searchProductsByName(String name);
    List<Product> getAllProducts();
    Product updateProduct(Product product);
    void deleteProduct(Integer id);
}
