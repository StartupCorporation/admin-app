package com.deye.web.async.listener.transactions;

import com.deye.web.async.listener.transactions.events.DeletedProductEvent;
import com.deye.web.async.listener.transactions.events.ReservationResultEvent;
import com.deye.web.async.listener.transactions.events.SavedProductEvent;
import com.deye.web.async.publisher.PublisherService;
import com.deye.web.async.util.RabbitMqEvent;
import com.deye.web.entity.ProductEntity;
import com.deye.web.exception.TransactionConsistencyException;
import com.deye.web.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {
    private final FileService fileService;
    private final PublisherService publisherService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onProductSaved(SavedProductEvent event) {
        try {
            ProductEntity product = event.getProduct();
            UUID productId = product.getId();
            MultipartFile[] imagesToAdd = event.getImagesToAdd();
            log.info("Product with id: {} successfully saved to db", productId);
            if (imagesToAdd != null) {
                log.info("Adding images to db, productId: {}", productId);
                for (MultipartFile image : imagesToAdd) {
                    uploadNewProductImage(image, productId);
                }
            }
            if (event.getImagesToRemove() != null) {
                for (String imageToRemove : event.getImagesToRemove()) {
                    log.info("Removing image from the storage as it was marked as to delete: {}", imageToRemove);
                    deleteProductImage(imageToRemove);
                }
            }
        } catch (Exception e) {
            log.error("Transaction consistency exception, rollback it");
            throw new TransactionConsistencyException(e);
        }
    }

    private void uploadNewProductImage(MultipartFile image, UUID productId) {
        log.info("Saving product image to the file storage, productId: {}, fileName: {}", productId, image.getOriginalFilename());
        try {
            fileService.upload(image);
        } catch (Exception e) {
            log.error("Product image uploading failed. id: {}", productId);
            throw e;
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onProductDeleted(DeletedProductEvent event) {
        try {
            UUID productId = event.getProductId();
            log.info("Product with ids: {} successfully deleted from DB. Trying to delete its images from storage", productId);
            for (String imageName : event.getImageNames()) {
                deleteProductImage(imageName);
            }
            publisherService.onProductsDeleted(Set.of(productId));
        } catch (Exception e) {
            log.error("Transaction consistency exception, rollback it");
            throw new TransactionConsistencyException(e);
        }
    }

    private void deleteProductImage(String fileName) {
        try {
            fileService.delete(fileName);
            log.info("Product image with name: {} successfully deleted from file storage", fileName);
        } catch (Exception e) {
            log.error("Product image with name: {} deletion failed.", fileName);
            throw e;
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onSuccessfulReservationResult(ReservationResultEvent event) {
        processReservationResult(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onFailedReservationResult(ReservationResultEvent event) {
        processReservationResult(event);
    }

    private void processReservationResult(ReservationResultEvent event) {
        try {
            UUID orderId = event.getOrderId();
            RabbitMqEvent rabbitMqEvent = event.getRabbitMqEvent();
            log.info("Reservation result: {}, for order id: {}. Publish message to message broker", rabbitMqEvent, orderId);
            publisherService.onReservationResult(orderId, rabbitMqEvent);
            log.info("Reservation result published successfully for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Transaction consistency exception, rollback it");
            throw new TransactionConsistencyException(e);
        }
    }
}
