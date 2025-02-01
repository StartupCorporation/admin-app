package com.deye.web.service.impl;

import com.deye.web.controller.dto.CreateProductDto;
import com.deye.web.controller.view.ProductView;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.entity.ProductEntity;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.listeners.events.DeletedProductEvent;
import com.deye.web.listeners.events.SavedProductEvent;
import com.deye.web.mapper.ProductMapper;
import com.deye.web.repository.ProductRepository;
import com.deye.web.utils.error.ErrorCodeUtils;
import com.deye.web.utils.error.ErrorMessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ApplicationEventPublisher eventPublisher;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public void save(CreateProductDto createProductDto) {
        log.info("Starting to save product: {}", createProductDto.getName());

        CategoryEntity category = categoryService.getCategoryEntityById(createProductDto.getCategoryId());
        log.debug("Category found: {}", category.getName());

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
        productRepository.save(product);
        eventPublisher.publishEvent(new SavedProductEvent(product, images));
    }

    @Transactional
    public List<ProductView> getAll() {
        log.info("Fetching all products");
        List<ProductView> products = productRepository.findAll().stream()
                .map(productMapper::toProductView)
                .toList();
        log.info("Found {} products", products.size());
        return products;
    }

    @Transactional
    public ProductView getById(UUID id) {
        log.info("Fetching product by ID={}", id);
        ProductEntity product = getProductEntityById(id);
        log.info("Product found: {} ({})", product.getName(), product.getId());
        return productMapper.toProductView(product);
    }

    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting product by ID={}", id);
        ProductEntity product = getProductEntityById(id);
        log.info("Product found: {}", product.getId());
        productRepository.delete(product);
        eventPublisher.publishEvent(new DeletedProductEvent(id, product.getImagesNames()));
    }

    private ProductEntity getProductEntityById(UUID id) {
        log.info("Searching for product in database with ID={}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product with ID={} not found!", id);
                    return new EntityNotFoundException(ErrorCodeUtils.PRODUCT_NOT_FOUND_ERROR_CODE, ErrorMessageUtils.PRODUCT_NOT_FOUND_ERROR_MESSAGE);
                });
    }
}
