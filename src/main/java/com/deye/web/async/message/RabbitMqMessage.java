package com.deye.web.async.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "event_type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderCreatedMessage.class, name = "ORDER_CREATED"),
        @JsonSubTypes.Type(value = OrderSubmittedMessage.class, name = "ORDER_SUBMITTED_FOR_PROCESSING")
})
public class RabbitMqMessage {
    private UUID id;
    private LocalDateTime created_at;
    private String event_type;
}