package com.deye.web.service.impl;

import com.deye.web.async.RabbitMqProducer;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.mapper.RabbitMqMessageMapper;
import com.deye.web.service.PublisherService;
import com.deye.web.utils.rabbitmq.RabbitMqUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMqPublisherService implements PublisherService {
    private final RabbitMqProducer rabbitMqProducer;
    private final RabbitMqMessageMapper rabbitMqMessageMapper;

    @Override
    public void onCategorySaved(CategoryEntity category) {
        String message = rabbitMqMessageMapper.toSavedCategoryMessage(category);
        try {
            rabbitMqProducer.send(RabbitMqUtil.EXCHANGE_NAME, RabbitMqUtil.CATEGORY_CONSUMER_ROUTING_KEY, message);
            log.info("Message on category saved successfully sent to RabbitMQ, message:{}", message);
        } catch (Exception e) {
            log.error("Unable to send message: {} to RabbitMQ", message, e);
        }
    }

    @Override
    public void onCategoryDeleted(UUID categoryId) {
        String message = rabbitMqMessageMapper.toDeleteCategoryMessage(categoryId);
        try {
            rabbitMqProducer.send(RabbitMqUtil.EXCHANGE_NAME, RabbitMqUtil.CATEGORY_CONSUMER_ROUTING_KEY, message);
            log.info("Message on category deleted successfully sent to RabbitMQ, message:{}", message);
        } catch (Exception e) {
            log.error("Unable to send message: {} to RabbitMQ", message, e);
        }
    }
}
