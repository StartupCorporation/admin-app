package com.deye.web.async.listener.transactions;

import com.deye.web.async.listener.transactions.events.DeletedCategoryEvent;
import com.deye.web.async.listener.transactions.events.SavedCategoryEvent;
import com.deye.web.async.publisher.PublisherService;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.exception.TransactionConsistencyException;
import com.deye.web.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryEventListener {
    private final FileService fileService;
    private final PublisherService publisherService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onCategorySaved(SavedCategoryEvent savedCategoryEvent) {
        try {
            CategoryEntity category = savedCategoryEvent.getCategory();
            UUID categoryId = category.getId();
            log.info("Category with id: {} successfully saved to DB. Trying to upload image to the storage", categoryId);
            MultipartFile image = savedCategoryEvent.getImage();
            if (image != null) {
                uploadNewCategoryImage(image, categoryId);
            }
            String previousImageName = savedCategoryEvent.getPreviousImageName();
            if (StringUtils.isNotBlank(previousImageName)) {
                log.info("Previous image name is {}. Deleting this from file storage", previousImageName);
                deleteCategoryImage(previousImageName);
            }
        } catch (Exception e) {
            log.error("Transaction consistency exception, rollback it");
            throw new TransactionConsistencyException(e);
        }
    }

    private void uploadNewCategoryImage(MultipartFile image, UUID categoryId) {
        log.info("Saving category image to the file storage, categoryId: {}, fileName: {}", categoryId, image.getOriginalFilename());
        try {
            fileService.upload(image);
        } catch (Exception e) {
            log.error("Category image uploading failed. id: {}", categoryId);
            throw e;
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onCategoryDeleted(DeletedCategoryEvent deletedCategoryEvent) {
        try {
            UUID categoryId = deletedCategoryEvent.getCategoryId();
            log.info("Category with id: {} successfully deleted from DB. Trying to delete its image from storage", categoryId);
            List<String> filesNamesToRemove = deletedCategoryEvent.getFilesNamesToRemove();
            for (String fileName : filesNamesToRemove) {
                deleteCategoryImage(fileName);
            }
            publisherService.onProductsDeleted(deletedCategoryEvent.getRemovedProductsIds());
        } catch (Exception e) {
            log.error("Transaction consistency exception, rollback it");
            throw new TransactionConsistencyException(e);
        }
    }

    private void deleteCategoryImage(String fileName) {
        try {
            fileService.delete(fileName);
            log.info("Category image with name: {} successfully deleted from file storage", fileName);
        } catch (Exception e) {
            log.error("Category image with name: {} deletion failed.", fileName);
            throw e;
        }
    }
}
