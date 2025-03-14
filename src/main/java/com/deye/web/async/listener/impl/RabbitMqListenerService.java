package com.deye.web.async.listener.impl;

import com.deye.web.async.listener.ListenerService;
import com.deye.web.async.message.OrderCreatedMessage;
import com.deye.web.async.message.OrderSubmittedMessage;
import com.deye.web.async.message.RabbitMqMessage;
import com.deye.web.async.util.RabbitMqEvent;
import com.deye.web.controller.dto.ReservationDto;
import com.deye.web.exception.ActionNotAllowedException;
import com.deye.web.exception.dlq.SkipDLQException;
import com.deye.web.service.impl.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.deye.web.async.message.OrderCreatedMessage.OrderCreatedPayload;
import static com.deye.web.async.message.OrderSubmittedMessage.OrderSubmittedPayload;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqListenerService implements ListenerService {
    private final ProductService productService;

    @Override
    @RabbitListener(queues = "${rabbitmq.reservation.queue}")
    public void onOrderEvent(RabbitMqMessage message) {
        try {
            if (RabbitMqEvent.ORDER_CREATED.name().equals(message.getEvent_type())) {
                OrderCreatedMessage orderCreatedMessage = (OrderCreatedMessage) message;
                reserveProducts(orderCreatedMessage);
                return;
            }
            if (RabbitMqEvent.ORDER_SUBMITTED_FOR_PROCESSING.name().equals(message.getEvent_type())) {
                OrderSubmittedMessage orderSubmittedMessage = (OrderSubmittedMessage) message;
                finishReservation(orderSubmittedMessage);
                return;
            }
            throw new ActionNotAllowedException("Possible events for this queue are: " + RabbitMqEvent.ORDER_CREATED.name() + " ," + RabbitMqEvent.ORDER_SUBMITTED_FOR_PROCESSING.name());
        } catch (SkipDLQException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    private void reserveProducts(OrderCreatedMessage orderCreatedMessage) {
        OrderCreatedPayload orderCreatedPayload = orderCreatedMessage.getData();
        UUID orderId = orderCreatedPayload.getOrder_id();
        log.info("Starting reservations for order: {}", orderId);
        ReservationDto reservationDto = new ReservationDto(orderCreatedPayload);
        productService.reserveProducts(reservationDto);
        log.info("Finished reservations for order: {}", orderId);
    }

    private void finishReservation(OrderSubmittedMessage orderSubmittedMessage) {
        log.info("Order is submitted. Trying to decrease the products stock quantity");
        List<OrderSubmittedPayload> orderSubmittedPayload = orderSubmittedMessage.getData();
        ReservationDto reservationDto = new ReservationDto(orderSubmittedPayload);
        productService.finishReservation(reservationDto);
        log.info("Products stock quantity decreasing is finished");
    }
}
