package com.deye.web.controller;

import com.deye.web.controller.dto.CreateAttributeDto;
import com.deye.web.controller.dto.response.AttributeResponseDto;
import com.deye.web.service.impl.AttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
public class AttributeController {
    private final AttributeService attributeService;

    @GetMapping
    public List<AttributeResponseDto> getAttributes() {
        return attributeService.getAll();
    }

    @GetMapping("/{id}")
    public AttributeResponseDto getAttributeById(@PathVariable("id") UUID id) {
        return attributeService.getById(id);
    }

    @PostMapping
    public void saveAttribute(@RequestBody @Valid CreateAttributeDto attribute) {
        attributeService.save(attribute);
    }

    @DeleteMapping("/{id}")
    public void deleteAttributeById(@PathVariable("id") UUID id) {
        attributeService.deleteByID(id);
    }
}
