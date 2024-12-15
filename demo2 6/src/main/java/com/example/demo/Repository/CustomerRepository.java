package com.example.demo.Repository;

import com.example.demo.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Long countById(Long id);
    Customer findByName(String name);
    Customer getById(int id);



    @Query("SELECT c FROM Customer c WHERE c.name LIKE %:query%")
    List<Customer> searchName(@Param("query") String query);
    @Query("SELECT c FROM Customer c WHERE c.username = :username")
    Customer findByUsername(@Param("username") String username);

}
