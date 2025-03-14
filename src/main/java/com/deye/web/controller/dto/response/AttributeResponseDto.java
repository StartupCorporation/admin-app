package com.deye.web.controller.dto.response;

import com.deye.web.entity.attribute.definition.AttributeDefinition;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AttributeResponseDto {
    private UUID id;
    private String name;
    private String description;
    private AttributeDefinition definition;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object value;
}
