package com.deye.web.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class ProductResponseDto {
    private UUID id;
    private String name;
    private String description;
    private Float price;
    private Integer stockQuantity;
    private UUID categoryId;
    private String categoryName;
    private Set<String> images;
    private List<AttributeResponseDto> attributes;
}
