package com.example.demo;

import com.example.demo.Model.Customer;
import com.example.demo.Repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@ContextConfiguration(classes = {Demo2Application.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindByName() {
        // Create and save a valid customer with all required fields
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setUsername("johndoe");
        customer.setAddress("123 Street");
        customer.setTelephone("123456789");
        customerRepository.save(customer);

        // Find the customer by name and validate
        Customer foundCustomer = customerRepository.findByName("John Doe");
        assertNotNull(foundCustomer);
        assertEquals("John Doe", foundCustomer.getName());
    }

    @Test
    void testSearchName() {
        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setUsername("jahndoe");
        customer.setAddress("123 Street");
        customer.setTelephone("123456759");
        customerRepository.save(customer);

        List<Customer> customers = customerRepository.searchName("Jane");
        assertFalse(customers.isEmpty());
        assertEquals("Jane Doe", customers.get(0).getName());
    }

    @Test
    void testFindByUsername() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setUsername("johndoe");
        customer.setAddress("123 Street");
        customer.setTelephone("123456789");
        customerRepository.save(customer);

        Customer foundCustomer = customerRepository.findByUsername("johndoe");
        assertNotNull(foundCustomer);
        assertEquals("johndoe", foundCustomer.getUsername());
    }
}

