package com.demo.testing.payment;

import java.math.BigDecimal;

public interface CardPaymentCharger {

    public CardPaymentCharge chargeCard(BigDecimal amount, Currency currency, String cardSource,
                                 String description);
}
