package com.gustaa13.dscatalog.resources;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustaa13.dscatalog.entities.Category;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

    @GetMapping
    public ResponseEntity<List<Category>> finaAll() {
        List<Category> list = new ArrayList<>();
        list.add(new Category(1l, "Books"));
        list.add(new Category(2l, "Electronics"));

        return ResponseEntity.ok().body(list);
    } 
}
