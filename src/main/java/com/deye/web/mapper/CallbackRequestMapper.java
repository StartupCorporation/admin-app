package com.deye.web.mapper;

import com.deye.web.controller.view.CallbackRequestView;
import com.deye.web.entity.CallbackRequestEntity;
import com.deye.web.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CallbackRequestMapper {

    public CallbackRequestView toCallbackRequestView(CallbackRequestEntity callbackRequest) {
        CallbackRequestView callbackRequestView = new CallbackRequestView();
        callbackRequestView.setId(callbackRequest.getId());
        callbackRequestView.setComment(callbackRequest.getComment());
        callbackRequestView.setCreatedTime(callbackRequest.getCreatedTime());
        callbackRequestView.setMessageCustomer(callbackRequest.getMessageCustomer());

        CustomerEntity customer = callbackRequest.getCustomer();
        callbackRequestView.setCustomerName(customer.getName());
        callbackRequestView.setPhoneNumber(customer.getPhoneNumber());
        return callbackRequestView;
    }
}
