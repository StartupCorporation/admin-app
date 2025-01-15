package com.deye.web.async.message;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SavedCategoryMessage extends RabbitMqMessage {
    private SavedCategoryPayload data;

    @Getter
    @Setter
    public static class SavedCategoryPayload {
        private UUID id;
        private String name;
        private String description;
        private String image;
    }
}
