package com.deye.web.controller.dto.rabbitmq;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class DeleteCategoryMessage {
    private UUID id;
    private LocalDateTime created_at;
    private String event_type;
    private DeleteCategoryPayload data;

    @Getter
    @Setter
    @Builder
    public static class DeleteCategoryPayload {
        private UUID id;
    }
}
