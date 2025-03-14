package com.deye.web.util.mapper;

import com.deye.web.controller.dto.response.AttributeResponseDto;
import com.deye.web.controller.dto.response.CategoryAttributeResponseDto;
import com.deye.web.entity.AttributeEntity;
import com.deye.web.entity.AttributeProductValuesEntity;
import org.springframework.stereotype.Component;

@Component
public class AttributeMapper {

    public AttributeResponseDto toAttributeView(AttributeEntity attribute) {
        AttributeResponseDto attributeResponseDto = new AttributeResponseDto();
        attributeResponseDto.setDefinition(attribute.getDefinition());
        attributeResponseDto.setName(attribute.getName());
        attributeResponseDto.setDescription(attribute.getDescription());
        attributeResponseDto.setId(attribute.getId());
        return attributeResponseDto;
    }

    public AttributeResponseDto toAttributeView(AttributeProductValuesEntity attributeProductValues) {
        AttributeEntity attribute = attributeProductValues.getAttribute();
        AttributeResponseDto attributeResponseDto = new AttributeResponseDto();
        attributeResponseDto.setDefinition(attribute.getDefinition());
        attributeResponseDto.setName(attribute.getName());
        attributeResponseDto.setDescription(attribute.getDescription());
        attributeResponseDto.setId(attribute.getId());
        attributeResponseDto.setValue(attributeProductValues.getValue());
        return attributeResponseDto;
    }

    public CategoryAttributeResponseDto toCategoryAttributeView(AttributeEntity attribute) {
        CategoryAttributeResponseDto attributeView = new CategoryAttributeResponseDto();
        attributeView.setDefinition(attribute.getDefinition());
        attributeView.setName(attribute.getName());
        attributeView.setId(attribute.getId());
        return attributeView;
    }
}
