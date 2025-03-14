package com.deye.web.service.impl;

import com.deye.web.controller.dto.CreateAttributeDto;
import com.deye.web.controller.dto.response.AttributeResponseDto;
import com.deye.web.entity.AttributeEntity;
import com.deye.web.entity.attribute.definition.AttributeDefinition;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.util.mapper.AttributeMapper;
import com.deye.web.repository.AttributeRepository;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttributeService {
    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;

    @Transactional
    public void save(CreateAttributeDto createAttributeDto) {
        log.info("Saving new attribute: name={}, type={}", createAttributeDto.getName(), createAttributeDto.getDefinition().getAttributeType());

        AttributeDefinition attributeDefinition = createAttributeDto.getDefinition();
        AttributeEntity attribute = new AttributeEntity();
        attribute.setName(createAttributeDto.getName());
        attribute.setDescription(createAttributeDto.getDescription());
        attribute.setDefinition(attributeDefinition);

        attributeRepository.save(attribute);
        log.info("Attribute saved successfully: id={}", attribute.getId());
    }

    @Transactional
    public List<AttributeResponseDto> getAll() {
        log.info("Fetching all attributes");
        List<AttributeResponseDto> attributes = attributeRepository.findAll().stream()
                .map(attributeMapper::toAttributeView)
                .toList();
        log.info("Fetched {} attributes", attributes.size());
        return attributes;
    }

    @Transactional
    public AttributeResponseDto getById(UUID id) {
        log.info("Fetching attribute by ID: {}", id);
        AttributeResponseDto attributeResponseDto = attributeMapper.toAttributeView(attributeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attribute not found: id={}", id);
                    return new EntityNotFoundException(ErrorCodeUtils.ATTRIBUTE_NOT_FOUND_ERROR_CODE, ErrorMessageUtils.ATTRIBUTE_NOT_FOUND_ERROR_MESSAGE);
                }));
        log.info("Attribute found: id={}, name={}", id, attributeResponseDto.getName());
        return attributeResponseDto;
    }

    @Transactional
    public void deleteByID(UUID id) {
        log.info("Deleting attribute by ID: {}", id);
        if (!attributeRepository.existsById(id)) {
            log.warn("Attempted to delete non-existing attribute: id={}", id);
            throw new EntityNotFoundException(ErrorCodeUtils.ATTRIBUTE_NOT_FOUND_ERROR_CODE, ErrorMessageUtils.ATTRIBUTE_NOT_FOUND_ERROR_MESSAGE);
        }
        attributeRepository.deleteById(id);
        log.info("Attribute deleted successfully: id={}", id);
    }
}
