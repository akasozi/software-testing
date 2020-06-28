package com.demo.testing.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments/charge-card")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(path = "{customerId}")
    public void chargeCard(@PathVariable("customerId") UUID customerId,
                           @RequestBody PaymentRequest paymentRequest) {

        paymentService.chargCard(customerId, paymentRequest);
    }
}
