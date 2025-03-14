package com.deye.web.async.message;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReservationResultMessage extends RabbitMqMessage {
    private ReservationResultPayload data;

    @Getter
    @Setter
    public static class ReservationResultPayload {
        private UUID order_id;
    }
}
