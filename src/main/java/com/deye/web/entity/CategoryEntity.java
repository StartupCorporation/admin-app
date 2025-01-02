package com.deye.web.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * Category services for products domain separation and sorting.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@Table(name = "CATEGORY")
@Entity
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String name;
    private String description;

    /**
     * We save images from client side to MinIO.
     * After saving, MinioFileService responds with an HTTP link to the image.
     * FE uses this link to fetch the image.
     */
    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private FileEntity image;

    public void setImage(String fileName) {
        FileEntity categoryImage = new FileEntity();
        categoryImage.setName(fileName);
        this.image = categoryImage;
    }
}
