package com.deye.web.service.impl;

import com.deye.web.async.listener.transactions.events.DeletedCategoryEvent;
import com.deye.web.async.listener.transactions.events.SavedCategoryEvent;
import com.deye.web.controller.AttributeController;
import com.deye.web.controller.dto.CategoryAttributeDto;
import com.deye.web.controller.dto.CreateCategoryDto;
import com.deye.web.controller.dto.UpdateCategoryDto;
import com.deye.web.controller.dto.response.CategoryResponseDto;
import com.deye.web.entity.AttributeEntity;
import com.deye.web.entity.CategoryAttributeEntity;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.entity.ProductEntity;
import com.deye.web.entity.attribute.definition.AttributeDefinition;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.exception.TransactionConsistencyException;
import com.deye.web.exception.WrongRequestBodyException;
import com.deye.web.repository.AttributeRepository;
import com.deye.web.repository.CategoryRepository;
import com.deye.web.util.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.deye.web.util.error.ErrorCodeUtils.ATTRIBUTES_VALUES_ERROR_CODE;
import static com.deye.web.util.error.ErrorCodeUtils.CATEGORY_NOT_FOUND_ERROR_CODE;
import static com.deye.web.util.error.ErrorMessageUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AttributeRepository attributeRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AttributeController attributeController;

    /**
     * Method for creating category to add new product type
     *
     * @param categoryDto - category parameters
     */
    @Transactional
    public void create(CreateCategoryDto categoryDto) {
        MultipartFile image = categoryDto.getImage();
        log.info("Creating category: name - {}, description - {} and image - {}", categoryDto.getName(), categoryDto.getDescription(), image.getOriginalFilename());
        CategoryEntity category = createCategory(categoryDto);
        applicationEventPublisher.publishEvent(new SavedCategoryEvent(category, image));
    }

    private CategoryEntity createCategory(CreateCategoryDto categoryDto) {
        CategoryEntity category = new CategoryEntity();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setImage(categoryDto.getImage().getOriginalFilename());
        addAttributesToCategory(category, categoryDto.getAttributes());
        return categoryRepository.saveAndFlush(category);
    }

    @Transactional
    public Set<CategoryResponseDto> getAll() {
        List<CategoryEntity> categories = categoryRepository.findAllWithFetchedAttributesAndImage();
        return categories.stream()
                .map(categoryMapper::toCategoryView)
                .collect(Collectors.toSet());
    }

    @Transactional
    public CategoryResponseDto getById(UUID id) {
        log.info("Getting category by id: {}", id);
        CategoryEntity category = getCategoryEntityByIdWithFetchedAttributesInformationAndImage(id);
        log.info("Category found: {}", id);
        return categoryMapper.toCategoryView(category);
    }

    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting category by id: {}", id);
        CategoryEntity category = getCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts(id);
        log.info("Category with id: {} found", id);
        List<String> filesNamesToRemove = new ArrayList<>();
        filesNamesToRemove.add(category.getImage().getName());
        category.getProducts()
                .forEach(product -> filesNamesToRemove.addAll(product.getImagesNames()));
        Set<UUID> removedProductsIds = category.getProducts().stream()
                .map(ProductEntity::getId)
                .collect(Collectors.toSet());
        categoryRepository.delete(category);
        applicationEventPublisher.publishEvent(new DeletedCategoryEvent(id, filesNamesToRemove, removedProductsIds));
    }

    @Transactional
    public void update(UUID id, UpdateCategoryDto categoryDto) {
        log.info("Updating category with id: {}", id);
        CategoryEntity category = getCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts(id);
        if (categoryDto.getName() != null && !categoryDto.getName().equals(category.getName())) {
            category.setName(categoryDto.getName());
            log.info("Category new name is set");
        }
        if (categoryDto.getDescription() != null && categoryDto.getDescription().equals(category.getName())) {
            category.setDescription(categoryDto.getDescription());
            log.info("Category new description is set");
        }
        MultipartFile newImage = categoryDto.getImage();
        String previousImageName = "";
        if (newImage != null) {
            previousImageName = category.getImage().getName();
            category.setImage(newImage.getOriginalFilename());
            log.info("Category new image is set");
        }
        List<UUID> attributesIdsToRemove = categoryDto.getAttributesIdsToRemove();
        if (attributesIdsToRemove != null && !attributesIdsToRemove.isEmpty()) {
            List<CategoryAttributeEntity> categoryAttributesToRemove = category.getCategoryAttributes().stream()
                    .filter(categoryAttribute -> attributesIdsToRemove.contains(categoryAttribute.getAttribute().getId()))
                    .toList();
            Set<AttributeEntity> attributesToRemoveFromProducts = categoryAttributesToRemove.stream()
                    .map(CategoryAttributeEntity::getAttribute)
                    .collect(Collectors.toSet());
            categoryAttributesToRemove.forEach(category.getCategoryAttributes()::remove);
            for (AttributeEntity attributeEntity : attributesToRemoveFromProducts) {
                category.getProducts()
                        .forEach(product -> product.removeAttributeValue(attributeEntity));
            }
        }
        categoryRepository.saveAndFlush(category);
        applicationEventPublisher.publishEvent(new SavedCategoryEvent(category, newImage, previousImageName));
    }

    private void addAttributesToCategory(CategoryEntity category, List<CategoryAttributeDto> attributesDto) {
        log.info("Adding attributes to category: {}", category.getName());
        if (attributesDto != null && !attributesDto.isEmpty()) {
            List<UUID> attributesIds = attributesDto.stream()
                    .map(CategoryAttributeDto::getId)
                    .toList();

            List<AttributeEntity> attributes = attributeRepository.findAllById(attributesIds);

            for (AttributeEntity attribute : attributes) {
                CategoryAttributeDto attributeDto = attributesDto.stream()
                        .filter(attrDto -> attrDto.getId().equals(attribute.getId()))
                        .findAny()
                        .get();
                CategoryAttributeEntity categoryAttribute = new CategoryAttributeEntity();
                categoryAttribute.setCategory(category);
                categoryAttribute.setAttribute(attribute);
                categoryAttribute.setFilterable(attributeDto.getIsFilterable());
                categoryAttribute.setRequired(attributeDto.getIsRequired());
                category.addAttribute(categoryAttribute);
            }
            log.info("Added attributes to category: {}", category.getName());
        }
    }

    private CategoryEntity getCategoryEntityByIdWithFetchedAttributesInformationAndImage(UUID id) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findByIdWithFetchedAttributesAndImage(id);
        if (categoryOptional.isEmpty()) {
            log.error("Category with id: {} not found", id);
            throw new EntityNotFoundException(CATEGORY_NOT_FOUND_ERROR_CODE, CATEGORY_NOT_FOUND_ERROR_MESSAGE);
        }
        return categoryOptional.get();
    }

    @Transactional
    public CategoryEntity getCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts(UUID id) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findByIdWithFetchedAttributesAndImagesAndProducts(id);
        if (categoryOptional.isEmpty()) {
            log.error("Category with id: {} not found", id);
            throw new EntityNotFoundException(CATEGORY_NOT_FOUND_ERROR_CODE, CATEGORY_NOT_FOUND_ERROR_MESSAGE);
        }
        return categoryOptional.get();
    }

    public void validateCategoryAttributesValues(ProductEntity product, Map<UUID, Object> attributesValues) {
        CategoryEntity category = product.getCategory();
        validateThatAllRequiredAttributesValuesAreProvided(category, attributesValues, product);
        if (attributesValues != null) {
            for (UUID attributeId : attributesValues.keySet()) {
                CategoryAttributeEntity categoryAttribute = category.getCategoryAttribute(attributeId);
                validateThatProvidedValueIsValid(categoryAttribute, attributesValues, attributeId);
            }
        }
    }

    private void validateThatProvidedValueIsValid(CategoryAttributeEntity categoryAttribute, Map<UUID, Object> attributesValues, UUID attributeId) {
        AttributeDefinition attributeDefinition = categoryAttribute.getAttribute().getDefinition();
        boolean isValidAttributeValue = attributeDefinition.validateAttributeValue(attributesValues.get(attributeId), categoryAttribute.isRequired());
        if (!isValidAttributeValue) {
            throw new WrongRequestBodyException(ATTRIBUTES_VALUES_ERROR_CODE, WRONG_ATTRIBUTE_VALUES_ERROR_MESSAGE);
        }
    }

    private void validateThatAllRequiredAttributesValuesAreProvided(CategoryEntity category, Map<UUID, Object> attributesValues, ProductEntity product) {
        Set<UUID> requiredAttributesIds = category.getCategoryAttributes().stream()
                .filter(CategoryAttributeEntity::isRequired)
                .map(categoryAttribute -> categoryAttribute.getAttribute().getId())
                .collect(Collectors.toSet());
        Set<UUID> attributesIdsTharAreAlreadySet = product.getAttributesValuesForProduct().stream()
                .map(attributeProductValue -> attributeProductValue.getAttribute().getId())
                .collect(Collectors.toSet());
        requiredAttributesIds.removeAll(attributesIdsTharAreAlreadySet);
        if (!requiredAttributesIds.isEmpty()) {
            if (attributesValues == null) {
                throw new WrongRequestBodyException(ATTRIBUTES_VALUES_ERROR_CODE, REQUIRED_ATTRIBUTE_VALUE_NOT_PROVIDED_ERROR_MESSAGE);
            }
            boolean allRequiredAttributesAreSet = attributesValues.keySet().containsAll(requiredAttributesIds);
            if (!allRequiredAttributesAreSet) {
                throw new WrongRequestBodyException(ATTRIBUTES_VALUES_ERROR_CODE, REQUIRED_ATTRIBUTE_VALUE_NOT_PROVIDED_ERROR_MESSAGE);
            }
        }
    }

}
