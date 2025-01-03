package com.deye.web.controller.view;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CategoryView {
    private UUID id;
    private String name;
    private String description;
    private String image;
}
