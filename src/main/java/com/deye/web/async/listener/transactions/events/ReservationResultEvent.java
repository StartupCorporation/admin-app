package com.deye.web.async.listener.transactions.events;

import com.deye.web.async.util.RabbitMqEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ReservationResultEvent {
    private UUID orderId;
    private RabbitMqEvent rabbitMqEvent;
}
