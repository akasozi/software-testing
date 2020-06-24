package com.demo.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldRegisterNewCustomer() {
        // ... Given id, customer
        UUID id = UUID.randomUUID();
        String phoneNumber = "+254728107303";
        Customer customer = new Customer(id, "Abel", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        // No customer with phone number passed
        given(customerRepository.findByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());
        // When
        underTest.registerNewCustomer(request);

        // ... then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToComparingFieldByField(customer);

    }

    @Test
    void itShouldRegisterNewCustomerWhenIdIsNull() {
        // ... Given id, customer
        UUID id = UUID.randomUUID();
        String phoneNumber = "+254728107303";
        Customer customer = new Customer(null, "Abel", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        // No customer with phone number passed
        given(customerRepository.findByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());
        // When
        underTest.registerNewCustomer(request);

        // ... then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToIgnoringGivenFields(customer, "id");

    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerAlreadyExists() {
        // Given
        // ... Given id, customer
        UUID id = UUID.randomUUID();
        String phoneNumber = "+254728107303";
        Customer customer = new Customer(id, "Abel", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... Customer exists
        given(customerRepository.findByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer));

        // When
        underTest.registerNewCustomer(request);

        // Then
        then(customerRepository).should(never()).save(any());
    }

    @Test
    void itShouldThrowExceptionWhenPhoneNumberTaken() {
        // ... Given id, customer
        UUID id = UUID.randomUUID();
        String phoneNumber = "+254728107303";
        Customer customer = new Customer(id, "Abel", phoneNumber);
        Customer customerTwo = new Customer(UUID.randomUUID(), "Franco", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... PhoneNumber exists under a different custome
        given(customerRepository.findByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customerTwo));
        // When
        // Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Phone Number [%s] already taken", phoneNumber));

       then(customerRepository).should(never()).save(any());
    }
}