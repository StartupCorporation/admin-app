package com.deye.web.async.listener;

import com.deye.web.async.message.RabbitMqMessage;

public interface ListenerService {

    /**
     * When order is created in order microservice - catalog service must try to reserve products for the order.
     * When order is submitted in order microservice - catalog service should decrease reserved quantity and quantity on stock.
     * So, this method causes the reservation or finish the reservation.
     * Reservation can be successful or not. Both cases cause new events - "FAILED_TO_RESERVE_PRODUCTS" and "PRODUCTS_RESERVED_FOR_ORDER"
     *
     * @param message
     */
    void onOrderEvent(RabbitMqMessage message);
}
