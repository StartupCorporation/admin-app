package com.deye.web.util.mapper;

import com.deye.web.controller.dto.response.AttributeResponseDto;
import com.deye.web.controller.dto.response.ProductResponseDto;
import com.deye.web.entity.ProductEntity;
import com.deye.web.service.impl.MinioConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final MinioConfigService minioConfigService;
    private final AttributeMapper attributeMapper;

    public ProductResponseDto toProductView(ProductEntity product) {
        List<AttributeResponseDto> attributes = product.getAttributesValuesForProduct().stream()
                .map(attributeMapper::toAttributeView)
                .toList();
        String bucketUrl = minioConfigService.getBucketName() + "/";
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .images(product.getImagesNames().stream().map(image -> bucketUrl + image).collect(Collectors.toSet()))
                .attributes(attributes)
                .build();
    }
}
