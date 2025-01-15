package com.deye.web.async.message;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeletedCategoryMessage extends RabbitMqMessage {
    private DeleteCategoryPayload data;

    @Getter
    @Setter
    public static class DeleteCategoryPayload {
        private UUID id;
    }
}
