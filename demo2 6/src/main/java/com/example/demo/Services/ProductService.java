package com.example.demo.Services;

import com.example.demo.Model.Product;
import com.example.demo.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll();
    }

    public Product get(Long id) throws ProductNotFoundException {
        Optional<Product> result = productRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw new ProductNotFoundException("Could not find any product with ID " + id);

    }
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product findByName2(String name) {
        return productRepository.findByName(name);
    }
    public String findProductNameById(Long id) throws ProductNotFoundException {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            return product.getName();
        } else {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
    }

    public void save(Product product) throws ProductAlreadyExistsException{
        Long count = productRepository.countById(product.getId());
        if (count != null && count > 0) {
            throw new ProductAlreadyExistsException("A product with ID " + product.getId() + " already exists.");
        }
        productRepository.save(product);
    }

    public void updateProduct(Long id, Product product) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();
            existingProduct.setName(product.getName());
            existingProduct.setSupplier(product.getSupplier());
            existingProduct.setPrice(product.getPrice());
            productRepository.save(existingProduct);
        } else {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
    }

    public void delete(Long id) throws ProductNotFoundException {
        Long count = productRepository.countById(id);
        if (count == null || count == 0) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
        productRepository.deleteById(id);
    }
}
