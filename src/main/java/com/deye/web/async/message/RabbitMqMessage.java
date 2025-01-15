package com.deye.web.async.message;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RabbitMqMessage {
    private UUID id;
    private LocalDateTime created_at;
    private String event_type;
}
