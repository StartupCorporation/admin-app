package com.deye.web.service.impl;

import com.deye.web.async.message.AskedCallbackRequestMessage;
import com.deye.web.controller.view.CallbackRequestView;
import com.deye.web.entity.CallbackRequestEntity;
import com.deye.web.entity.CustomerEntity;
import com.deye.web.mapper.CallbackRequestMapper;
import com.deye.web.repository.CallbackRequestRepository;
import com.deye.web.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.deye.web.async.message.AskedCallbackRequestMessage.AskedCallbackRequestPayload;
import static com.deye.web.async.message.AskedCallbackRequestMessage.AskedCallbackRequestPayload.CustomerInformation;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackRequestService {
    private final CallbackRequestRepository callbackRequestRepository;
    private final CallbackRequestMapper callbackRequestMapper;
    private final CustomerRepository customerRepository;

    @Transactional
    public void save(AskedCallbackRequestMessage askedCallbackRequestMessage) {
        log.info("Saving callback request to db");
        AskedCallbackRequestPayload payload = askedCallbackRequestMessage.getData();
        CallbackRequestEntity callbackRequest = new CallbackRequestEntity();
        callbackRequest.setComment(payload.getComment());
        callbackRequest.setCreatedTime(askedCallbackRequestMessage.getCreated_at());
        callbackRequest.setMessageCustomer(payload.getMessage_customer());

        setCustomerInformation(callbackRequest, payload.getCustomer());
        callbackRequestRepository.save(callbackRequest);
    }

    private void setCustomerInformation(CallbackRequestEntity callbackRequest, CustomerInformation customerInformation) {
        String phoneNumber = customerInformation.getPhone();
        Optional<CustomerEntity> customerOpt = customerRepository.findByPhoneNumber(phoneNumber);
        if (customerOpt.isPresent()) {
            log.info("Customer with phone: {} already exist", phoneNumber);
            callbackRequest.setCustomer(customerOpt.get());
        } else {
            log.info("Creating customer with phone: {}", phoneNumber);
            CustomerEntity customer = new CustomerEntity();
            customer.setPhoneNumber(phoneNumber);
            customer.setName(customerInformation.getName());
            callbackRequest.setCustomer(customer);
        }
    }

    @Transactional
    public List<CallbackRequestView> getAll() {
        log.info("Getting all callback requests");
        return callbackRequestRepository.findAll().stream()
                .map(callbackRequestMapper::toCallbackRequestView)
                .toList();
    }

    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting callback request with id: {}", id);
        callbackRequestRepository.deleteById(id);
    }
}
