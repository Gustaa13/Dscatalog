package com.gustaa13.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.gustaa13.dscatalog.entities.Product;
import com.gustaa13.dscatalog.tests.ProductFactory;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1l;
        nonExistingId = 0l;
        countTotalProducts = 25l;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {

        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {

        Product product = ProductFactory.createProduct();
        product.setId(null);

        product = repository.save(product);

        Assertions.assertNotNull(product);
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    public void findByIdShouldReturnOptionalProductNotEmptyWhenIdExists() {
        
        Optional<Product> product = repository.findById(existingId);

        Assertions.assertTrue(product.isPresent());
    }

    @Test
    public void findByIdShouldReturnOptionalProductEmptyWhenIdDoesNotExist() {
        Optional<Product> product = repository.findById(nonExistingId);

        Assertions.assertFalse(product.isPresent());
    }
}
