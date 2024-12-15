package com.example.demo.Repository;

import com.example.demo.Model.Cart;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByCustomer(Customer customer);
    Cart findByCustomerAndItemsProduct(Customer customer, Product product);


}

