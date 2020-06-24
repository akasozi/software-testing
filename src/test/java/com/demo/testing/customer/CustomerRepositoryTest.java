package com.demo.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given a customer
        UUID customerId = UUID.randomUUID();
        String phoneNumber = "+254728107303";
        Customer myCustomer = new Customer(customerId, "Stephanie Namusisi", phoneNumber);
        // When
        underTest.save(myCustomer);
        Optional<Customer> optionalCustomer = underTest.findByPhoneNumber(phoneNumber);
        // Then
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToComparingFieldByField(myCustomer);
                });
    }

    @Test
    void itShouldSaveNewCustomer() {
        // ... Given a id, customer
        UUID customerId = UUID.randomUUID();
        Customer myCustomer = new Customer(customerId, "Stephanie Namusisi", "+254728107303");
        // ... When
        underTest.save(myCustomer);
        // ... Then
        Optional<Customer> optionalCustomer = underTest.findById(customerId);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
//                    assertThat(c.getId()).isEqualTo(myCustomer.getId());
//                    assertThat(c.getName()).isEqualTo(myCustomer.getName());
//                    assertThat(c.getPhoneNumber()).isEqualTo(myCustomer.getPhoneNumber());
                    assertThat(c).isEqualToComparingFieldByField(myCustomer);
                });
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer myCustomer = new Customer(customerId, null, "+254728107303");
        // ... When
        // ... then
        assertThatThrownBy(() -> underTest.save(myCustomer))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.name");
    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // ... Given a customerId, customer
        UUID customerId = UUID.randomUUID();
        Customer myCustomer = new Customer(customerId, "Stephanie Namusisi", null);
        // ... When
        // ... then
        assertThatThrownBy(() -> underTest.save(myCustomer))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.phoneNumber");
    }


}