package com.deye.web.async.listener.transactions.events;

import com.deye.web.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class SavedCategoryEvent {
    private CategoryEntity category;
    private MultipartFile image;
    private String previousImageName;

    public SavedCategoryEvent(CategoryEntity category, MultipartFile image) {
        this.category = category;
        this.image = image;
    }
}
