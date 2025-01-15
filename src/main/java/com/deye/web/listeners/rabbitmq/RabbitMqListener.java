package com.deye.web.listeners.rabbitmq;

import com.deye.web.async.message.AskedCallbackRequestMessage;
import com.deye.web.listeners.MessageBrokerListener;
import com.deye.web.mapper.RabbitMqMessageMapper;
import com.deye.web.service.impl.CallbackRequestService;
import com.deye.web.utils.rabbitmq.RabbitMqUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMqListener implements MessageBrokerListener {
    private final CallbackRequestService callbackRequestService;
    private final RabbitMqMessageMapper rabbitMqMessageMapper;

    @Override
    @RabbitListener(queues = RabbitMqUtil.ADMIN_CALLBACK_REQUEST_QUEUE)
    public void askedCallbackRequest(String message) {
        log.info(RabbitMqUtil.ADMIN_CALLBACK_REQUEST_QUEUE + " queue listening, message: {}", message);
        AskedCallbackRequestMessage askedCallbackRequestMessage = rabbitMqMessageMapper.toAskedCallbackRequestMessage(message);
        callbackRequestService.save(askedCallbackRequestMessage);
    }
}
