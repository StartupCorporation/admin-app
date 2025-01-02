package com.deye.web.service.impl;

import com.deye.web.controller.dto.CreateCategoryDto;
import com.deye.web.controller.dto.UpdateCategoryDto;
import com.deye.web.controller.view.CategoryView;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.mapper.CategoryMapper;
import com.deye.web.repository.CategoryRepository;
import com.deye.web.service.FileService;
import com.deye.web.service.PublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.deye.web.utils.error.ErrorCodeUtils.CATEGORY_NOT_FOUND_ERROR_CODE;
import static com.deye.web.utils.error.ErrorMessageUtils.CATEGORY_NOT_FOUND_ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final FileService fileService;
    private final PublisherService publisherService;

    /**
     * Method for creating category to add new product type
     *
     * @param categoryDto - category parameters
     */
    @Transactional
    public void create(CreateCategoryDto categoryDto) {
        log.info("Creating category: name - {}, description - {} and image - {}", categoryDto.getName(), categoryDto.getDescription(), categoryDto.getImage().getName());
        CategoryEntity category = new CategoryEntity();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        String fileName = fileService.upload(categoryDto.getImage());
        category.setImage(fileName);
        categoryRepository.save(category);
        log.info("Category created: {}", category);
        publisherService.onCategorySaved(category);
    }

    @Transactional
    public Set<CategoryView> getAll() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toCategoryView)
                .collect(Collectors.toSet());
    }

    @Transactional
    public CategoryView getById(UUID id) {
        log.info("Getting category by id: {}", id);
        CategoryEntity category = getCategoryEntityById(id);
        log.info("Category found: {}", category);
        return categoryMapper.toCategoryView(category);
    }

    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting category by id: {}", id);
        categoryRepository.deleteById(id);
        log.info("Category deleted: {}", id);
        publisherService.onCategoryDeleted(id);
    }

    @Transactional
    public void update(UUID id, UpdateCategoryDto categoryDto) {
        log.info("Updating category with id: {}", id);
        CategoryEntity category = getCategoryEntityById(id);
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
            log.info("Category new name is set");
        }
        if (categoryDto.getDescription() != null) {
            category.setDescription(categoryDto.getDescription());
            log.info("Category new description is set");
        }
        if (categoryDto.getImage() != null) {
            fileService.delete(category.getImage().getName());
            String fileName = fileService.upload(categoryDto.getImage());
            category.setImage(fileName);
            log.info("Category new image is set");
        }
        categoryRepository.save(category);
        log.info("Category updated: {}", category);
        publisherService.onCategorySaved(category);
    }

    private CategoryEntity getCategoryEntityById(UUID id) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            log.error("Category with id: {} not found", id);
            throw new EntityNotFoundException(CATEGORY_NOT_FOUND_ERROR_CODE, CATEGORY_NOT_FOUND_ERROR_MESSAGE);
        }
        return categoryOptional.get();
    }
}
