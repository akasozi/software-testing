package com.demo.testing.payment;

import com.demo.testing.customer.Customer;
import com.demo.testing.customer.CustomerRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MockMvc mockMvc;
    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        String phoneNumber = "+254728107303";
        Customer customer = new Customer(customerId, "Abel", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        ResultActions customerRegResultAction = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customer-registration")
                .contentType("application/json")
                .content(objectToJson(request)));

        Payment payment =
                new Payment(1L, customerId, new BigDecimal("10.00"), Currency.GBP, "Card1234", "Donation");
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        System.out.println(customerRegResultAction);
        // When
        ResultActions paymentResultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payments/charge-card/{customerId}", customerId)
                .contentType("application/json")
                .content(objectToJson(paymentRequest)));
        // Then
        customerRegResultAction.andExpect(status().isOk());
        paymentResultActions.andExpect(status().isOk());

        assertThat(paymentRepository.findById(1L))
                .isPresent()
                .hasValueSatisfying(p -> {
                      assertThat(p).isEqualToComparingFieldByField(payment);
                });
    }
    private String objectToJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
           fail("Failed to convert object to JSON");
           return null;
        }
    }
}
