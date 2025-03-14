package com.deye.web.async.util.mapper;

import com.deye.web.async.message.DeletedProductsMessage;
import com.deye.web.async.message.ReservationResultMessage;
import com.deye.web.async.util.RabbitMqEvent;
import com.deye.web.exception.ActionNotAllowedException;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMqMessageMapper {
    private final Gson gson;

    public String toDeletedProductMessage(Set<UUID> productsIds) {
        DeletedProductsMessage deletedProductsMessage = new DeletedProductsMessage();
        deletedProductsMessage.setId(UUID.randomUUID());
        deletedProductsMessage.setCreated_at(LocalDateTime.now());
        deletedProductsMessage.setEvent_type(RabbitMqEvent.PRODUCTS_DELETED.name());
        DeletedProductsMessage.DeletedProductPayload deletedProductsPayload = new DeletedProductsMessage.DeletedProductPayload();
        deletedProductsPayload.setIds(productsIds);
        deletedProductsMessage.setData(deletedProductsPayload);
        return gson.toJson(deletedProductsMessage);
    }

    public String toReservationResultMessage(UUID orderId, RabbitMqEvent rabbitMqEvent) {
        if (rabbitMqEvent != null && !RabbitMqEvent.getReservationResultEvents().contains(rabbitMqEvent)) {
            log.error("{} is not an reservation result event", rabbitMqEvent);
            throw new ActionNotAllowedException("The reservation result event is not allowed");
        }
        ReservationResultMessage reservationResultMessage = new ReservationResultMessage();
        reservationResultMessage.setId(UUID.randomUUID());
        reservationResultMessage.setCreated_at(LocalDateTime.now());
        reservationResultMessage.setEvent_type(rabbitMqEvent.name());
        ReservationResultMessage.ReservationResultPayload reservationResultPayload = new ReservationResultMessage.ReservationResultPayload();
        reservationResultPayload.setOrder_id(orderId);
        reservationResultMessage.setData(reservationResultPayload);
        return gson.toJson(reservationResultMessage);
    }
}
