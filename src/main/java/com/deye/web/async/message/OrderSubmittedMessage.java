package com.deye.web.async.message;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderSubmittedMessage extends RabbitMqMessage {
    private List<OrderSubmittedPayload> data;

    @Getter
    @Setter
    public static class OrderSubmittedPayload {
        private UUID product_id;
        private int quantity;
    }
}
