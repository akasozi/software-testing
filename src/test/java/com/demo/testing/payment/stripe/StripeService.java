package com.demo.testing.payment.stripe;

import com.demo.testing.payment.CardPaymentCharge;
import com.demo.testing.payment.CardPaymentCharger;
import com.demo.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService implements CardPaymentCharger {

    private static final RequestOptions requestOptions = RequestOptions.builder()
            .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
            .build();

    @Override
    public CardPaymentCharge chargeCard(BigDecimal amount, Currency currency, String cardSource, String description) {

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("source", cardSource);
        params.put(
                "description",
                description
        );

        try {
            Charge charge = Charge.create(params, requestOptions);
            Boolean chargePaid = charge.getPaid();
            return new CardPaymentCharge(chargePaid);
        } catch (StripeException e) {
            throw new IllegalStateException("Cannot make stripe charge", e);

        }
    }
}
