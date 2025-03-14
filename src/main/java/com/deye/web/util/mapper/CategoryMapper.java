package com.deye.web.util.mapper;

import com.deye.web.controller.dto.response.CategoryResponseDto;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.service.impl.MinioConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {
    private final AttributeMapper attributeMapper;
    private final MinioConfigService minioConfigService;

    public CategoryResponseDto toCategoryView(CategoryEntity category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .image(minioConfigService.getBucketName() + "/" + category.getImage().getName())
                .attributes(category.getCategoryAttributes().stream()
                        .map(categoryAttributeEntity -> attributeMapper.toCategoryAttributeView(categoryAttributeEntity.getAttribute()))
                        .toList())
                .build();
    }
}
