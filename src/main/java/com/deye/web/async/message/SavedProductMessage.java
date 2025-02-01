package com.deye.web.async.message;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class SavedProductMessage extends RabbitMqMessage {
    private SavedProductPayload data;

    @Getter
    @Setter
    public static class SavedProductPayload {
        private UUID id;
        private String name;
        private String description;
        private Float price;
        private Integer stock_quantity;
        private UUID category_id;
        private Set<String> images;
    }
}
