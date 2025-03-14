package com.deye.web.async.publisher;

import com.deye.web.async.util.RabbitMqEvent;

import java.util.Set;
import java.util.UUID;

public interface PublisherService {
    void onProductsDeleted(Set<UUID> productId);

    void onReservationResult(UUID orderId, RabbitMqEvent rabbitMqEvent);
}
