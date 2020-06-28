package com.demo.testing.payment;

import com.demo.testing.customer.Customer;
import com.demo.testing.customer.CustomerRepository;
import com.demo.testing.sms.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    @Captor
    private ArgumentCaptor<Payment> paymentArgumentCaptor;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;
    @Mock
    private SmsService smsService;

    private PaymentService underTest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger, smsService);
    }


    @Test
    void itShouldNotChargeCardAndThrowWhenCustomerDoesNotExist() {
        // Given
        UUID customerId = UUID.randomUUID();
        String phoneNumber = "+254728107303";
        // ... given customer does not exists
        given(customerRepository.findById(customerId))
                               .willReturn(Optional.empty());

        Long paymentId = 1L;
        Payment payment =
                new Payment(paymentId, UUID.randomUUID(), new BigDecimal("10.00"), Currency.GBP, "Card1234", "Donation");
        PaymentRequest request = new PaymentRequest(payment);
        // When
        // Then
        assertThatThrownBy(() -> underTest.chargCard(customerId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Customer " + customerId + " was not found");

        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
        then(smsService).shouldHaveNoInteractions();

    }

    @Test
    void itShouldChargeCardAndSendSMS() {
        // Given a customer
        UUID customerId = UUID.randomUUID();
        String phoneNumber = "+254728107303";
        Customer myCustomer = new Customer(customerId, "Stephanie Namusisi", phoneNumber);
        Long paymentId = 1L;
        Payment payment =
                new Payment(paymentId, UUID.randomUUID(), new BigDecimal("10.00"), Currency.GBP, "Card1234", "Donation");
         PaymentRequest request = new PaymentRequest(payment);
         // ... Given a customer exists
         given(customerRepository.findById(customerId))
                                  .willReturn(Optional.of(myCustomer));
         // ... Given card is charged successfully
         given(cardPaymentCharger.chargeCard(request.getPayment().getAmount(),
                    request.getPayment().getCurrency(),
                    request.getPayment().getSource(),
                    request.getPayment().getDescription()))
                 .willReturn(new CardPaymentCharge(true));
        // When
        underTest.chargCard(customerId, request);
        // Then
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());
        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();

        assertThat(paymentArgumentCaptorValue)
                .isEqualToComparingFieldByField(request.getPayment());

        assertThat(paymentArgumentCaptorValue.getCustomerId())
                .isEqualTo(customerId);

        ArgumentCaptor<String> destinationMSISDNArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> sourceMSISDNArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageArgumentCaptor = ArgumentCaptor.forClass(String.class);

        then(smsService).should().send(destinationMSISDNArgumentCaptor.capture(),
                                         messageArgumentCaptor.capture());

        String destinationMSISDNArgumentCaptorValue = destinationMSISDNArgumentCaptor.getValue();
        String sourceMSISDNArgumentCaptorValue = sourceMSISDNArgumentCaptor.getValue();
        String messageArgumentCaptorValue = messageArgumentCaptor.getValue();

        assertThat(destinationMSISDNArgumentCaptorValue).isEqualTo(phoneNumber);
        assertThat(sourceMSISDNArgumentCaptorValue).isNotNull();
        assertThat(messageArgumentCaptorValue).isNotNull();

    }

    @Test
    void itShouldThrowWhenCardIsNotCharged() {
        // Given
        UUID customerId = UUID.randomUUID();
        String phoneNumber = "+254728107303";
        Customer myCustomer = new Customer(customerId, "Stephanie Namusisi", phoneNumber);
        Long paymentId = 1L;
        Payment payment =
                new Payment(paymentId, UUID.randomUUID(), new BigDecimal("10.00"), Currency.GBP, "Card1234", "Donation");
        PaymentRequest request = new PaymentRequest(payment);
        // ... Given a customer exists
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(myCustomer));
        // ... Given card is charged successfully
        given(cardPaymentCharger.chargeCard(request.getPayment().getAmount(),
                request.getPayment().getCurrency(),
                request.getPayment().getSource(),
                request.getPayment().getDescription()))
                .willReturn(new CardPaymentCharge(false));

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargCard(customerId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Card '" + request.getPayment().getSource() + "' was not charged");
        // Then
        then(paymentRepository).should(never()).save(any(Payment.class));
        then(smsService).shouldHaveNoInteractions();

    }

    @Test
    void itShouldThrowAndNotChargeCardWhenCurrencyNotSupported() {
        // Given
        // Given
        UUID customerId = UUID.randomUUID();
        String phoneNumber = "+254728107303";
        Customer myCustomer = new Customer(customerId, "Stephanie Namusisi", phoneNumber);
        Long paymentId = 1L;
        Payment payment =
                new Payment(paymentId, UUID.randomUUID(), new BigDecimal("10.00"), Currency.EUR, "Card1234", "Donation");
        PaymentRequest request = new PaymentRequest(payment);
        // ... Given a customer exists
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(myCustomer));

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargCard(customerId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Currency " + payment.getCurrency() + " is not supported");

        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).should(never()).save(any(Payment.class));
        then(smsService).shouldHaveNoInteractions();
    }
}