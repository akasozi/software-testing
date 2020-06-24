package com.demo.testing.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository underTest;

    @Test
    void itShouldSavePayment() {
        // Given
        long paymentId = 1L;
        Payment payment =
                new Payment(paymentId, UUID.randomUUID(),
                        new BigDecimal("10.00"),
                        Currency.GBP, "Card123", "Donation");
        // When
        underTest.save(payment);
        Optional<Payment> optionalPayment = underTest.findById(paymentId);
        // Then
        assertThat(optionalPayment).isPresent()
                .hasValueSatisfying(p -> {
//                    assertThat(p).isEqualToIgnoringGivenFields(payment, "paymentId");
                    assertThat(p).isEqualToComparingFieldByField(payment);
//                    assertThat(p).isEqualTo(payment);
                });

    }
}