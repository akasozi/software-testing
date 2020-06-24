package com.demo.testing.customer;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends CrudRepository<Customer, UUID> {

    public Optional<Customer> findByPhoneNumber(String phoneNumber);
}
