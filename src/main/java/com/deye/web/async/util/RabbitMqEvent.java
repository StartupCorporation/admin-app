package com.deye.web.async.util;

import java.util.Set;

public enum RabbitMqEvent {
    ORDER_SUBMITTED_FOR_PROCESSING,
    ORDER_CREATED,
    PRODUCTS_DELETED,
    FAILED_TO_RESERVE_PRODUCTS,
    PRODUCTS_RESERVED_FOR_ORDER;

    public static Set<RabbitMqEvent> getReservationResultEvents() {
        return Set.of(FAILED_TO_RESERVE_PRODUCTS, PRODUCTS_RESERVED_FOR_ORDER);
    }
}
