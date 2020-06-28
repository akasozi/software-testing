package com.demo.testing.payment.stripe;

import com.demo.testing.payment.CardPaymentCharge;
import com.demo.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class StripeServiceTest {

    @Mock
    private StripeApi stripeApi;

    private StripeService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new StripeService(stripeApi);
    }

    @Test
    void itShouldChargeCard() throws StripeException {
        // Given
        // ... amount
        BigDecimal amount = new BigDecimal("100.00");
        // ... currency
        Currency currency = Currency.GBP;
        // ... source
        String source = "card123xx";
        // ... description
        String description = "Donation Payment";

        // ... requestOptions
        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(anyMap(), any()))
                .willReturn(charge);

        // When
        // Then
        CardPaymentCharge cardPaymentCharge = underTest.chargeCard(amount, currency, source, description);

        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> requestOptionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);

        then(stripeApi).should().create(mapArgumentCaptor.capture(), requestOptionsArgumentCaptor.capture());

        Map<String, Object> mapArgumentCaptorValue = mapArgumentCaptor.getValue();
        RequestOptions requestOptionsArgumentCaptorValue = requestOptionsArgumentCaptor.getValue();

        assertThat(mapArgumentCaptorValue.keySet()).hasSize(4);
        assertThat(mapArgumentCaptorValue.get("amount")).isEqualTo(amount);
        assertThat(mapArgumentCaptorValue.get("currency")).isEqualTo(currency);
        assertThat(mapArgumentCaptorValue.get("source")).isEqualTo(source);
        assertThat(mapArgumentCaptorValue.get("description")).isEqualTo(description);

        assertThat(requestOptionsArgumentCaptorValue).isNotNull();

        assertThat(cardPaymentCharge.isCardDebited()).isTrue();

    }


    @Test
    void itShouldNotChargeWhenApiThrowsException() throws StripeException {
        // Given
        String cardSource = "0x0x0x";
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.USD;
        String description = "Zakat";

        // Throw exception when stripe api is called
        StripeException stripeException = mock(StripeException.class);
        doThrow(stripeException).when(stripeApi).create(anyMap(), any());

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(amount, currency, cardSource, description))
                .isInstanceOf(IllegalStateException.class)
                .hasRootCause(stripeException)
                .hasMessageContaining("Cannot make stripe charge");
    }
}