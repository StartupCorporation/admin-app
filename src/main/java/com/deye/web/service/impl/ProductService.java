package com.deye.web.service.impl;

import com.deye.web.async.listener.events.DeletedProductEvent;
import com.deye.web.async.listener.events.SavedProductEvent;
import com.deye.web.controller.dto.CreateProductDto;
import com.deye.web.controller.dto.ProductFilterDto;
import com.deye.web.controller.dto.UpdateProductDto;
import com.deye.web.controller.view.ProductView;
import com.deye.web.entity.*;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.exception.TransactionConsistencyException;
import com.deye.web.repository.ProductRepository;
import com.deye.web.service.spricification.ProductFilterSpecification;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import com.deye.web.util.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ApplicationEventPublisher eventPublisher;
    private final CategoryService categoryService;
    private final ConfigService configService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductFilterSpecification productFilterSpecification;

    @Transactional(rollbackFor = TransactionConsistencyException.class)
    public void save(CreateProductDto createProductDto) {
        log.info("Starting to save product: {}", createProductDto.getName());

        CategoryEntity category = categoryService.getCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts(createProductDto.getCategoryId());
        log.debug("Category found: {}", category.getName());

        ProductEntity product = createProduct(createProductDto, category);
        productRepository.saveAndFlush(product);
        eventPublisher.publishEvent(new SavedProductEvent(product, createProductDto.getImages()));
    }

    private ProductEntity createProduct(CreateProductDto createProductDto, CategoryEntity category) {
        ProductEntity product = new ProductEntity();
        product.setName(createProductDto.getName());
        product.setDescription(createProductDto.getDescription());
        product.setPrice(createProductDto.getPrice());
        product.setStockQuantity(createProductDto.getStockQuantity());
        product.setCategory(category);

        MultipartFile[] images = createProductDto.getImages();
        Set<String> imagesNames = Arrays.stream(images)
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.toSet());

        product.setImages(imagesNames);
        Map<UUID, Object> attributesValuesToSave = createProductDto.getAttributesValuesToSave();
        addAttributesValues(product, attributesValuesToSave);
        return product;
    }

    private void addAttributesValues(ProductEntity product, Map<UUID, Object> attributesValuesToSave) {
        categoryService.validateCategoryAttributesValues(product, attributesValuesToSave);
        CategoryEntity category = product.getCategory();
        if (attributesValuesToSave != null) {
            for (UUID attributeId : attributesValuesToSave.keySet()) {
                Object value = attributesValuesToSave.get(attributeId);
                AttributeEntity attribute = category.getCategoryAttributes().stream()
                        .map(CategoryAttributeEntity::getAttribute)
                        .filter(attr -> attr.getId().equals(attributeId))
                        .findAny()
                        .get();
                product.addAttributeValue(attribute, value);
            }
        }
    }

    @Transactional
    public Page<ProductView> getAll(ProductFilterDto productFilterDto, Pageable pageable) {
        log.info("Fetching all products");
        Page<ProductEntity> products = productRepository.findAll(productFilterSpecification.filterBy(productFilterDto), pageable);
        log.info("Found {} products", products.getTotalElements());
        return products.map(productMapper::toProductView);
    }

    @Transactional
    public ProductView getById(UUID id) {
        log.info("Fetching product by ID={}", id);
        ProductEntity product = getProductEntityById(id);
        log.info("Product found: {} ({})", product.getName(), product.getId());
        return productMapper.toProductView(product);
    }

    @Transactional(rollbackFor = TransactionConsistencyException.class)
    public void deleteById(UUID id) {
        log.info("Deleting product by ID={}", id);
        ProductEntity product = getProductEntityById(id);
        log.info("Product found: {}", product.getId());
        productRepository.delete(product);
        eventPublisher.publishEvent(new DeletedProductEvent(id, product.getImagesNames()));
    }

    @Transactional(rollbackFor = TransactionConsistencyException.class)
    public void update(UUID id, UpdateProductDto updateProductDto) {
        log.info("Updating product by ID={}", id);
        ProductEntity product = getProductEntityById(id);
        MultipartFile[] imagesToAdd = updateProductDto.getImagesToAdd();
        List<String> imagesNamesToRemove = updateProductDto.getImagesToRemove();
        Map<UUID, Object> attributesValuesToSave = updateProductDto.getAttributesValuesToSave();
        Set<UUID> attributesIdsToRemove = updateProductDto.getAttributesIdsToRemove();
        if (updateProductDto.getName() != null && !updateProductDto.getName().equals(product.getName())) {
            product.setName(updateProductDto.getName());
        }
        if (updateProductDto.getDescription() != null && !updateProductDto.getDescription().equals(product.getDescription())) {
            product.setDescription(updateProductDto.getDescription());
        }
        if (updateProductDto.getPrice() != null && !updateProductDto.getPrice().equals(product.getPrice())) {
            product.setPrice(updateProductDto.getPrice());
        }
        if (updateProductDto.getStockQuantity() != null && !updateProductDto.getStockQuantity().equals(product.getStockQuantity())) {
            product.setStockQuantity(updateProductDto.getStockQuantity());
        }
        if (imagesToAdd != null) {
            Set<String> imagesNamesToAdd = Arrays.stream(imagesToAdd)
                    .map(MultipartFile::getOriginalFilename)
                    .collect(Collectors.toSet());
            product.setImages(imagesNamesToAdd);
        }
        if (imagesNamesToRemove != null) {
            String bucketName = configService.getMinioBucketName();
            imagesNamesToRemove = imagesNamesToRemove.stream()
                    .map(fileName -> {
                        if (StringUtils.contains(fileName, bucketName + "/")) {
                            fileName = StringUtils.substringAfter(fileName, bucketName + "/");
                        }
                        return fileName;
                    })
                    .toList();
            product.removeImages(imagesNamesToRemove);
        }
        if (attributesValuesToSave != null) {
            addAttributesValues(product, attributesValuesToSave);
        }
        if (attributesIdsToRemove != null) {
            product.getAttributesValuesForProduct().stream()
                    .map(AttributeProductValuesEntity::getAttribute)
                    .filter(attribute -> attributesIdsToRemove.contains(attribute.getId()))
                    .collect(Collectors.toSet())
                    .forEach(product::removeAttributeValue);
        }
        productRepository.saveAndFlush(product);
        eventPublisher.publishEvent(new SavedProductEvent(product, imagesToAdd, imagesNamesToRemove));
    }

    private ProductEntity getProductEntityById(UUID id) {
        log.info("Searching for product in database with ID={}", id);
        return productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(id)
                .orElseThrow(() -> {
                    log.error("Product with ID={} not found!", id);
                    return new EntityNotFoundException(ErrorCodeUtils.PRODUCT_NOT_FOUND_ERROR_CODE, ErrorMessageUtils.PRODUCT_NOT_FOUND_ERROR_MESSAGE);
                });
    }
}
