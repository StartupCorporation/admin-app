package com.deye.web.mapper;

import com.deye.web.controller.dto.rabbitmq.DeleteCategoryMessage;
import com.deye.web.controller.dto.rabbitmq.UpsertCategoryMessage;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.utils.rabbitmq.RabbitMqUtil;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.deye.web.controller.dto.rabbitmq.DeleteCategoryMessage.DeleteCategoryPayload;
import static com.deye.web.controller.dto.rabbitmq.UpsertCategoryMessage.UpsertCategoryPayload;

@Component
@RequiredArgsConstructor
public class RabbitMqMessageMapper {
    private final Gson gson;

    public String toUpsertCategoryMessage(CategoryEntity category) {
        return gson.toJson(UpsertCategoryMessage.builder()
                .id(UUID.randomUUID())
                .created_at(LocalDateTime.now())
                .event_type(RabbitMqUtil.CATEGORY_SAVED_EVENT)
                .data(UpsertCategoryPayload.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .image(category.getImage().getName())
                        .build())
                .build());
    }

    public String toDeleteCategoryMessage(UUID categoryId) {
        return gson.toJson(DeleteCategoryMessage.builder()
                .id(UUID.randomUUID())
                .created_at(LocalDateTime.now())
                .event_type(RabbitMqUtil.CATEGORY_DELETED_EVENT)
                .data(DeleteCategoryPayload.builder()
                        .id(categoryId)
                        .build())
                .build());
    }
}
