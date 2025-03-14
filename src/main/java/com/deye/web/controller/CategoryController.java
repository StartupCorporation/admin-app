package com.deye.web.controller;

import com.deye.web.controller.dto.CreateCategoryDto;
import com.deye.web.controller.dto.UpdateCategoryDto;
import com.deye.web.controller.dto.response.CategoryResponseDto;
import com.deye.web.service.impl.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Set<CategoryResponseDto>> getCategories() {
        return new ResponseEntity<>(categoryService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategory(@PathVariable UUID id) {
        return new ResponseEntity<>(categoryService.getById(id), HttpStatus.OK);
    }

    @PostMapping
    public void createCategory(@ModelAttribute @Valid CreateCategoryDto category) {
        categoryService.create(category);
    }

    @PatchMapping("/{id}")
    public void updateCategory(@PathVariable UUID id, @ModelAttribute @Valid UpdateCategoryDto category) {
        categoryService.update(id, category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.deleteById(id);
    }
}
