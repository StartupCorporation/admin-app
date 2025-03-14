package com.deye.web.async.message;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderCreatedMessage extends RabbitMqMessage {
    private OrderCreatedPayload data;

    @Getter
    @Setter
    public static class OrderCreatedPayload {
        private UUID order_id;
        private List<Product> products;

        @Getter
        @Setter
        public static class Product {
            private UUID product_id;
            private int quantity;
        }
    }
}
