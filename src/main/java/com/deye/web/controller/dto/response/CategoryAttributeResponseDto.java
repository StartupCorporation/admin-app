package com.deye.web.controller.dto.response;

import com.deye.web.entity.attribute.definition.AttributeDefinition;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CategoryAttributeResponseDto {
    private UUID id;
    private String name;
    private AttributeDefinition definition;
}
