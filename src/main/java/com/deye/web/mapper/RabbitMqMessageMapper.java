package com.deye.web.mapper;

import com.deye.web.async.message.AskedCallbackRequestMessage;
import com.deye.web.async.message.DeletedCategoryMessage;
import com.deye.web.async.message.SavedCategoryMessage;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.exception.EventMessageException;
import com.deye.web.utils.error.ErrorCodeUtils;
import com.deye.web.utils.error.ErrorMessageUtils;
import com.deye.web.utils.rabbitmq.RabbitMqUtil;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.deye.web.async.message.DeletedCategoryMessage.DeleteCategoryPayload;
import static com.deye.web.async.message.SavedCategoryMessage.SavedCategoryPayload;

@Component
@RequiredArgsConstructor
public class RabbitMqMessageMapper {
    private static final Logger log = LoggerFactory.getLogger(RabbitMqMessageMapper.class);
    private final Gson gson;

    public String toSavedCategoryMessage(CategoryEntity category) {
        // payload
        SavedCategoryPayload savedCategoryPayload = new SavedCategoryPayload();
        savedCategoryPayload.setId(category.getId());
        savedCategoryPayload.setName(category.getName());
        savedCategoryPayload.setDescription(category.getDescription());
        savedCategoryPayload.setImage(category.getImage().getName());

        // message
        SavedCategoryMessage savedCategoryMessage = new SavedCategoryMessage();
        savedCategoryMessage.setId(UUID.randomUUID());
        savedCategoryMessage.setCreated_at(LocalDateTime.now());
        savedCategoryMessage.setEvent_type(RabbitMqUtil.CATEGORY_SAVED_EVENT);
        savedCategoryMessage.setData(savedCategoryPayload);

        return gson.toJson(savedCategoryMessage);
    }

    public String toDeleteCategoryMessage(UUID categoryId) {
        // payload
        DeleteCategoryPayload deleteCategoryPayload = new DeleteCategoryPayload();
        deleteCategoryPayload.setId(categoryId);

        // message
        DeletedCategoryMessage deletedCategoryMessage = new DeletedCategoryMessage();
        deletedCategoryMessage.setId(UUID.randomUUID());
        deletedCategoryMessage.setCreated_at(LocalDateTime.now());
        deletedCategoryMessage.setEvent_type(RabbitMqUtil.CATEGORY_DELETED_EVENT);
        deletedCategoryMessage.setData(deleteCategoryPayload);
        return gson.toJson(deletedCategoryMessage);
    }

    public AskedCallbackRequestMessage toAskedCallbackRequestMessage(String message) {
        try {
            return gson.fromJson(message, AskedCallbackRequestMessage.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new EventMessageException(ErrorCodeUtils.EVENT_MESSAGE_NOT_PROCEED_ERROR_CODE, ErrorMessageUtils.EVENT_MESSAGE_NOT_PROCEED_ERROR_MESSAGE);
        }
    }
}
