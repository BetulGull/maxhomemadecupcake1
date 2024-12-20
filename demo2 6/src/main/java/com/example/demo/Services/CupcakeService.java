package com.example.demo.Services;

import com.example.demo.Model.Cupcake;
import com.example.demo.Repository.CupcakeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CupcakeService {

    @Autowired
    private CupcakeRepository productRepository;

    public List<Cupcake> listAll() {
        return (List<Cupcake>) productRepository.findAll();
    }


    public Cupcake get(Long id) throws CupcakeNotFoundException {
        Optional<Cupcake> result = productRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw new CupcakeNotFoundException("Could not find any product with ID " + id);

    }
    public CupcakeService(CupcakeRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Cupcake findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Cupcake findByName2(String name) {
        return productRepository.findByName(name);
    }
    public String findProductNameById(Long id) throws CupcakeNotFoundException {
        Optional<Cupcake> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Cupcake cupcake = productOptional.get();
            return cupcake.getName();
        } else {
            throw new CupcakeNotFoundException("Could not find any product with ID " + id);
        }
    }

    public void save(Cupcake cupcake) throws CupcakeAlreadyExistsException {
        Long count = productRepository.countById(cupcake.getId());
        if (count != null && count > 0) {
            throw new CupcakeAlreadyExistsException("A product with ID " + cupcake.getId() + " already exists.");
        }
        productRepository.save(cupcake);
    }

    public void updateProduct(Long id, Cupcake cupcake) throws CupcakeNotFoundException {
        Optional<Cupcake> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Cupcake existingCupcake = optionalProduct.get();
            existingCupcake.setName(cupcake.getName());
            existingCupcake.setSupplier(cupcake.getSupplier());
            existingCupcake.setPrice(cupcake.getPrice());
            productRepository.save(existingCupcake);
        } else {
            throw new CupcakeNotFoundException("Could not find any product with ID " + id);
        }
    }

    public void delete(Long id) throws CupcakeNotFoundException {
        Long count = productRepository.countById(id);
        if (count == null || count == 0) {
            throw new CupcakeNotFoundException("Could not find any product with ID " + id);
        }
        productRepository.deleteById(id);
    }
}
