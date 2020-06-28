package com.demo.testing.payment;

import com.demo.testing.customer.Customer;
import com.demo.testing.customer.CustomerRepository;
import com.demo.testing.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaymentService {

    private final CustomerRepository customerRepository;
    private  final  PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;
    private final SmsService smsSender;

    private static final List<Currency> supportedCurrencies =
            Arrays.asList(Currency.GBP, Currency.USD);

    private static final String SOURCE_MSISDN = "+254700000000";


    @Autowired
    public PaymentService(CustomerRepository customerRepository, PaymentRepository paymentRepository,
                          CardPaymentCharger cardPaymentCharger, SmsService smsSender) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
        this.smsSender = smsSender;
    }



    public void chargCard(UUID customerId, PaymentRequest request) {
        // 1. Does the customer exist if not throw
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            throw new IllegalStateException("Customer " + customerId + " was not found");
        }

        Customer myCustomer = optionalCustomer.get();

        // 2. Do we support the currency if not throw
        Currency currency = request.getPayment().getCurrency();
        if(!supportedCurrencies.contains(currency)) {
            throw new IllegalStateException("Currency " + currency + " is not supported");
        }
        // 3. Charge card
        Payment payment =request.getPayment();
        CardPaymentCharge cardPaymentCharge = cardPaymentCharger
                .chargeCard(payment.getAmount(),
                            payment.getCurrency(),
                            payment.getSource(),
                            payment.getDescription());
        // 4. If not debited throw
        if (!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException("Card '" + payment.getSource() + "' was not charged");
        }
        // 5. Insert payment
        payment.setCustomerId(customerId);
        paymentRepository.save(payment);
        // 6. TODO: send SMS
         smsSender.send(myCustomer.getPhoneNumber(),
                "Your card payment of " + payment.getAmount() + " has been received");
    }
}
