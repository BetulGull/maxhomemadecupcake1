package com.example.demo.Repository;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Order;
import com.example.demo.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    Long countById(Long id);

    Order getById(int id);
    List<Order> findByCustomerUsername(String username);
    List<Order> findAll();
    List<Order> findByCustomer(Customer customer);

    Optional<Order> findByCustomerAndProduct(Customer customer, Product product);
}

