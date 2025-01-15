package com.deye.web.controller;

import com.deye.web.controller.view.CallbackRequestView;
import com.deye.web.service.impl.CallbackRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/callback-requests")
@RequiredArgsConstructor
public class CallbackRequestController {
    private final CallbackRequestService callbackRequestService;

    @GetMapping
    public List<CallbackRequestView> getAll() {
        return callbackRequestService.getAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable UUID id) {
        callbackRequestService.deleteById(id);
    }
}
