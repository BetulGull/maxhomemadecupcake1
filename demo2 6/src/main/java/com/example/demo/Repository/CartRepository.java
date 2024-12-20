package com.example.demo.Repository;

import com.example.demo.Model.Cart;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Cupcake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByCustomer(Customer customer);
    @Query("SELECT c FROM Cart c JOIN c.items i WHERE c.customer = :customer AND i.cupcake = :cupcake")
    Cart findByCustomerAndItemsCupcake(@Param("customer") Customer customer, @Param("cupcake") Cupcake cupcake);

}

