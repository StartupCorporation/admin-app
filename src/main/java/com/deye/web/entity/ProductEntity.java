package com.deye.web.entity;

import com.deye.web.exception.ActionNotAllowedException;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "PRODUCT")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private Float price;
    private Integer stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private CategoryEntity category;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true, mappedBy = "product")
    private Set<FileEntity> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AttributeProductValuesEntity> attributesValuesForProduct = new HashSet<>();
    private int reservedQuantity;

    public void setImages(Set<String> fileNames) {
        for (String fileName : fileNames) {
            FileEntity productImage = new FileEntity();
            productImage.setName(fileName);
            productImage.setProduct(this);
            this.images.add(productImage);
        }
    }

    public void removeImages(List<String> fileNames) {
        Set<FileEntity> toRemove = images.stream()
                .filter(image -> fileNames.contains(image.getName()))
                .collect(Collectors.toSet());
        images.removeAll(toRemove);
    }

    public Set<String> getImagesNames() {
        return images.stream()
                .map(FileEntity::getName)
                .collect(Collectors.toSet());
    }

    public void addAttributeValue(AttributeEntity attribute, Object value) {
        AttributeProductValuesEntity attributeProductValue = new AttributeProductValuesEntity();
        attributeProductValue.setProduct(this);
        attributeProductValue.setAttribute(attribute);
        attributeProductValue.setValue(value);
        AttributeProductValuesEntity existedAttributeValue = attributesValuesForProduct.stream()
                .filter(attributeValueForProduct -> attributeValueForProduct.equals(attributeProductValue))
                .findAny()
                .orElse(null);
        if (existedAttributeValue != null && !existedAttributeValue.getValue().equals(value)) {
            existedAttributeValue.setValue(attributeProductValue.getValue());
        } else {
            attributesValuesForProduct.add(attributeProductValue);
        }
    }

    public void removeAttributeValue(AttributeEntity attribute) {
        Optional<CategoryAttributeEntity> categoryAttributeOpt = this.category.getCategoryAttributes().stream()
                .filter(categoryAttr -> categoryAttr.getAttribute().equals(attribute))
                .findAny();
        if (categoryAttributeOpt.isEmpty()) {
            throw new EntityNotFoundException(ErrorCodeUtils.ATTRIBUTE_NOT_FOUND_ERROR_CODE, ErrorMessageUtils.ATTRIBUTE_TO_DELETE_WAS_NOT_FOUND_ERROR_MESSAGE);
        }
        CategoryAttributeEntity categoryAttribute = categoryAttributeOpt.get();
        if (categoryAttribute.isRequired()) {
            throw new ActionNotAllowedException(ErrorCodeUtils.ATTRIBUTE_DELETION_ACTION_NOT_ALLOWED_ERROR_CODE, ErrorMessageUtils.ATTRIBUTE_DELETE_ACTION_NOT_ALLOWED_ERROR_MESSAGE);
        }
        AttributeProductValuesEntity attributeProductValue = attributesValuesForProduct.stream()
                .filter(attributeProduct -> attributeProduct.getAttribute().equals(attribute))
                .findAny()
                .orElse(null);
        if (attributeProductValue != null) {
            attributesValuesForProduct.remove(attributeProductValue);
        }
    }
}
