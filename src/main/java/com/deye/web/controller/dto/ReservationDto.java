package com.deye.web.controller.dto;

import com.deye.web.exception.ActionNotAllowedException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.deye.web.async.message.OrderCreatedMessage.OrderCreatedPayload;
import static com.deye.web.async.message.OrderCreatedMessage.OrderCreatedPayload.Product;
import static com.deye.web.async.message.OrderSubmittedMessage.OrderSubmittedPayload;

@Getter
@Setter
public class ReservationDto {
    private UUID orderId;
    private Map<UUID, Integer> productsIdsAndQuantity;

    public ReservationDto(OrderCreatedPayload orderCreatedPayload) {
        this.orderId = orderCreatedPayload.getOrder_id();
        this.productsIdsAndQuantity = new HashMap<>();
        List<Product> products = orderCreatedPayload.getProducts();
        int initProductSize = products.size();
        for (Product product : products) {
            this.productsIdsAndQuantity.put(product.getProduct_id(), product.getQuantity());
        }
        if (initProductSize != this.productsIdsAndQuantity.size()) {
            throw new ActionNotAllowedException("Products list must contain only unique products");
        }
    }

    public ReservationDto(List<OrderSubmittedPayload> orderSubmittedPayload) {
        this.productsIdsAndQuantity = new HashMap<>();
        int initProductSize = orderSubmittedPayload.size();
        for (OrderSubmittedPayload payload : orderSubmittedPayload) {
            this.productsIdsAndQuantity.put(payload.getProduct_id(), payload.getQuantity());
        }
        if (initProductSize != this.productsIdsAndQuantity.size()) {
            throw new ActionNotAllowedException("Products list must contain only unique products");
        }
    }
}
