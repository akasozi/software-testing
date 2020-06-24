package com.demo.testing.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {

        Customer customer = request.getCustomer();
        Optional<Customer> optionalCustomer =
                customerRepository.findByPhoneNumber(customer.getPhoneNumber());

        if (optionalCustomer.isPresent()) {
            Customer tempCustomer = optionalCustomer.get();
            if (tempCustomer.getName().equals(customer.getName())) {
                return;
            }
            throw new IllegalStateException(String.format("Phone Number [%s] already taken", customer.getPhoneNumber()));
        }

        if (request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
        }

        customerRepository.save(request.getCustomer());
    }
}
