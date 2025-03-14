package com.deye.web.async.publisher.impl;

import com.deye.web.async.RabbitMqProducer;
import com.deye.web.async.publisher.PublisherService;
import com.deye.web.async.util.RabbitMqEvent;
import com.deye.web.async.util.mapper.RabbitMqMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMqPublisherService implements PublisherService {
    private final RabbitMqProducer rabbitMqProducer;
    private final RabbitMqMessageMapper rabbitMqMessageMapper;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.product.comment.queue.routing.key}")
    private String productCommentQueueRoutingKey;

    @Value("${rabbitmq.order.queue.routing.key}")
    private String orderQueueRoutingKey;

    @Override
    public void onProductsDeleted(Set<UUID> productsIds) {
        String message = rabbitMqMessageMapper.toDeletedProductMessage(productsIds);
        try {
            rabbitMqProducer.send(exchangeName, productCommentQueueRoutingKey, message);
            log.info("Message on product deleted successfully sent to RabbitMQ, message:{}", message);
        } catch (Exception e) {
            log.error("Unable to send message: {} to RabbitMQ", message, e);
            throw e;
        }
    }

    @Override
    public void onReservationResult(UUID orderId, RabbitMqEvent rabbitMqEvent) {
        String message = rabbitMqMessageMapper.toReservationResultMessage(orderId, rabbitMqEvent);
        try {
            rabbitMqProducer.send(exchangeName, orderQueueRoutingKey, message);
            log.info("Message on reservation result sent to RabbitMQ, message:{}", message);
        } catch (Exception e) {
            log.error("Unable to send message: {} to RabbitMQ", message, e);
            throw e;
        }
    }
}
