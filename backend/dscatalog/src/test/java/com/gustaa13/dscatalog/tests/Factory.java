package com.gustaa13.dscatalog.tests;

import java.time.Instant;

import com.gustaa13.dscatalog.dto.ProductDTO;
import com.gustaa13.dscatalog.entities.Category;
import com.gustaa13.dscatalog.entities.Product;

public class Factory {

    public static Product createProduct() {

        Product product = new Product(1l, "Phone", "Good Phone", 400.0, "img", Instant.parse("2020-10-20T03:00:00Z"));

        product.getCategories().add(createCategory());

        return product;
    }

    public static ProductDTO createProductDTO() {

        Product product = createProduct();
        
        return new ProductDTO(product, product.getCategories());
    }

    public static Category createCategory() {

        Category category = new Category(1l, "Books");

        return category;
    }
}
