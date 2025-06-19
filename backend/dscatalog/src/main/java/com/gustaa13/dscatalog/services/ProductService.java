package com.gustaa13.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gustaa13.dscatalog.dto.CategoryDTO;
import com.gustaa13.dscatalog.dto.ProductDTO;
import com.gustaa13.dscatalog.entities.Category;
import com.gustaa13.dscatalog.entities.Product;
import com.gustaa13.dscatalog.repositories.CategoryRepository;
import com.gustaa13.dscatalog.repositories.ProductRepository;
import com.gustaa13.dscatalog.services.exceptions.DatabaseException;
import com.gustaa13.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable) {
        Page<Product> list = repository.findAll(pageable);
        
        return list.map(product -> new ProductDTO(product));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));

        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        copyDTOtoEntity(dto, entity);
        entity = repository.save(entity);
        
        return new ProductDTO(entity);
    }
        
    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {

        try {
            Product entity = repository.getReferenceById(id);
            copyDTOtoEntity(dto, entity);
            entity = repository.save(entity);

            return new ProductDTO(entity);
        } catch(EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
        
    }

    public void delete(Long id) {
        if(!repository.existsById(id)) throw new ResourceNotFoundException("Id not found " + id);

        try {
            repository.deleteById(id);
        } catch(DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }

    }

    private void copyDTOtoEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setImgUrl(dto.getImgUrl());

        entity.getCategories().clear();
        for (CategoryDTO catDTO : dto.getCategories()) {
            Category category = categoryRepository.getReferenceById(catDTO.getId());
            entity.getCategories().add(category);
        }
    }
}
