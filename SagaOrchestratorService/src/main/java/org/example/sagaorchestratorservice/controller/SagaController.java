package org.example.sagaorchestratorservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.sagaorchestratorservice.service.SagaOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saga/orders")
@RequiredArgsConstructor
public class SagaController {

    private final SagaOrchestratorService sagaService;

    @PostMapping("/process/{orderId}")
    public String processOrder(@PathVariable Long orderId) {
        sagaService.processOrder(orderId);
        return "Saga process started for order " + orderId;
    }

//    @PostMapping("/create")
//    public ResponseEntity<?> createOrderSaga(@RequestBody OrderRequestDTO request) {
//        return sagaOrchestratorService.createOrderSaga(request);
//    }
}
