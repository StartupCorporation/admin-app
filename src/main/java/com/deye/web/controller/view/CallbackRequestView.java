package com.deye.web.controller.view;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CallbackRequestView {
    private UUID id;
    private String customerName;
    private String phoneNumber;
    private String comment;
    private Boolean messageCustomer;
    private LocalDateTime createdTime;
}
