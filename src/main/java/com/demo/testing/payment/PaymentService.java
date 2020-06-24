package com.demo.testing.payment;

import com.demo.testing.customer.Customer;
import com.demo.testing.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaymentService {

    private final CustomerRepository customerRepository;
    private  final  PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;

    private static final List<Currency> supportedCurrencies =
            Arrays.asList(Currency.GBP, Currency.USD);

    @Autowired
    public PaymentService(CustomerRepository customerRepository, PaymentRepository paymentRepository,
                          CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }



    public void chargCard(UUID customerId, PaymentRequest request) {

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            throw new IllegalStateException("Customer " + customerId + " was not found");
        }

        Currency currency = request.getPayment().getCurrency();
        if(!supportedCurrencies.contains(currency)) {
            throw new IllegalStateException("Currency " + currency + " is not supported");
        }

        Payment payment =request.getPayment();
        CardPaymentCharge cardPaymentCharge = cardPaymentCharger
                .chargeCard(payment.getAmount(),
                            payment.getCurrency(),
                            payment.getSource(),
                            payment.getDescription());

        if (!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException("Card '" + payment.getSource() + "' was not charged");
        }

        payment.setCustomerId(customerId);
        paymentRepository.save(payment);
    }
}
