package com.deye.web.utils.rabbitmq;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RabbitMqUtil {
    public static final String EXCHANGE_NAME = "deye_web.direct";
    public static final String CATEGORY_CONSUMER_ROUTING_KEY = "customer.category.queue";
    public static final String CATEGORY_SAVED_EVENT = "CATEGORY_SAVED";
    public static final String CATEGORY_DELETED_EVENT = "CATEGORY_DELETED";
    public static final String ADMIN_CALLBACK_REQUEST_QUEUE = "admin.callback_request.queue";
}
