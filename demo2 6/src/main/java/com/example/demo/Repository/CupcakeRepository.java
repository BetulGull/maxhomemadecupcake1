package com.example.demo.Repository;

import com.example.demo.Model.Cupcake;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CupcakeRepository extends CrudRepository<Cupcake, Long> {
    Long countById(Long id);
    Cupcake findByName(String name);


}
