package com.example.demo.Services;

import com.example.demo.Model.Customer;
import com.example.demo.Repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository crepo;

    public List<Customer> listAll() {
        return (List<Customer>) crepo.findAll();
    }

    public Customer findByName1(String name) {
        return crepo.findByName(name);
    }

    public void deleteCustomer(Long id) throws Exception {
        if (crepo.existsById(id)) {
            crepo.deleteById(id);
        } else {
            throw new Exception("Customer not found");
        }
    }

    public void save(Customer customer) throws CustomerAlreadyExistsException {
        Long count = crepo.countById(customer.getId());
        if (count != null && count > 0) {
            throw new CustomerAlreadyExistsException("A customer with ID " + customer.getId() + " already exists.");
        }
        crepo.save(customer);
    }

    public Customer get(Long id) throws CustomerNotFoundException {
        Optional<Customer> result = crepo.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw new CustomerNotFoundException("Could not find any customer with ID " + id);
    }

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.crepo = customerRepository;
    }

    public Customer findById(Long id) {
        return crepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    public String findCustomerNameById(Long id) throws CustomerNotFoundException {
        Optional<Customer> customerOptional = crepo.findById(id);
        if (customerOptional.isPresent()) {
            return customerOptional.get().getName();
        } else {
            return "Customer Not Found";
        }
    }

    public void updateCustomer(Long id, Customer customer) throws CustomerNotFoundException {
        Optional<Customer> optionalCustomer = crepo.findById(id);
        if (optionalCustomer.isPresent()) {
            Customer existingCustomer = optionalCustomer.get();
            existingCustomer.setName(customer.getName());
            existingCustomer.setAddress(customer.getAddress());
            existingCustomer.setTelephone(customer.getTelephone());
            crepo.save(existingCustomer);
        } else {
            throw new CustomerNotFoundException("Could not find any customer with ID " + id);
        }
    }
}