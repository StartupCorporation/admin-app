package com.deye.web.controller.dto.rabbitmq;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UpsertCategoryMessage {
    private UUID id;
    private LocalDateTime created_at;
    private String event_type;
    private UpsertCategoryPayload data;

    @Getter
    @Setter
    @Builder
    public static class UpsertCategoryPayload {
        private UUID id;
        private String name;
        private String description;
        private String image;
    }
}
