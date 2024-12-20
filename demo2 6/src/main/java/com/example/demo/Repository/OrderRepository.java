package com.example.demo.Repository;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Order;
import com.example.demo.Model.Cupcake;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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

    //Optional<Order> findByCustomerAndProduct(Customer customer, Cupcake cupcake);
    @Query("SELECT o FROM Order o WHERE o.customer = :customer AND o.cupcake = :cupcake")
    Optional<Order> findByCustomerAndCupcake(@Param("customer") Customer customer, @Param("cupcake") Cupcake cupcake);

}

