package com.example.demo.Services;

import com.example.demo.Controller.OrderAlreadyExistsException;
import com.example.demo.Model.Cupcake;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Order;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.CupcakeRepository;
import com.example.demo.DTOs.Orderview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository crepo;
    @Autowired
    private CupcakeRepository productRepository;

    @Override
    public List<Order> getAllOrders() {
        Iterable<Order> orderIterable = orderRepository.findAll();
        List<Order> orderList = new ArrayList<>();
        orderIterable.forEach(orderList::add);
        return orderList;
    }
    public List<Order> getOrdersByCustomer(Customer customer) {
        // Assuming you have an orderRepository with a method to find orders by customer
        return orderRepository.findByCustomer(customer);
    }


    @Override
    public boolean orderAlreadyExists(Order order) {
        // Check if an order with the same customer and product already exists
        Optional<Order> existingOrder = orderRepository.findByCustomerAndCupcake(order.getCustomer(), order.getCupcake());
        return existingOrder.isPresent();
    }

    @Override
    public void saveOrder(Order order) throws OrderAlreadyExistsException {
        if (orderAlreadyExists(order)) {
            throw new OrderAlreadyExistsException("An order with the customer and product already exists.");
        }
        orderRepository.save(order);
    }

    @Override
    public Order createOrder(Orderview orderview) throws OrderAlreadyExistsException, CustomerNotFoundException, CupcakeNotFoundException {
        // Check if an order with the same ID already exists
        Long count = orderRepository.countById(orderview.getId());
        if (count != null && count > 0) {
            throw new OrderAlreadyExistsException("An order with ID " + orderview.getId() + " already exists.");
        }

        // Find the customer based on the provided name
        Customer customer = crepo.findByName(orderview.getCustomerName());
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found with name: " + orderview.getCustomerName());
        }

        // Find the product based on the provided name
        Cupcake cupcake = productRepository.findByName(orderview.getProductName());
        if (cupcake == null) {
            throw new CupcakeNotFoundException("Product not found with name: " + orderview.getProductName());
        }

        // Create and save the new order
        Order order = new Order();
        order.setId(orderview.getId());
        order.setCity(orderview.getCity());
        order.setDate(orderview.getDate());
        order.setDeliveryStatus(orderview.getDeliveryStatus());
        order.setCupcake(cupcake);
        order.setCustomer(customer);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        return optionalOrder.orElse(null);
    }

    @Override
    public void updateOrder(Order order) throws OrderNotFoundException, CustomerNotFoundException, CupcakeNotFoundException {
        // Find the order by ID
        Optional<Order> optionalOrder = orderRepository.findById(order.getId());
        if (optionalOrder.isPresent()) {
            Order existingOrder = optionalOrder.get();

            // Update order details
            existingOrder.setDeliveryStatus(order.getDeliveryStatus());
            existingOrder.setCity(order.getCity());
            existingOrder.setDate(order.getDate());

            // Find and update product
            Cupcake cupcake = productRepository.findByName(order.getCupcake().getName());
            if (cupcake == null) {
                throw new CupcakeNotFoundException("Product not found with name: " + order.getCupcake().getName());
            }
            existingOrder.setCupcake(cupcake);

            // Find and update customer
            Customer customer = crepo.findByName(order.getCustomer().getName());
            if (customer == null) {
                throw new CustomerNotFoundException("Customer not found with name: " + order.getCustomer().getName());
            }
            existingOrder.setCustomer(customer);

            // Save the updated order
            orderRepository.save(existingOrder);
        } else {
            throw new OrderNotFoundException("Order not found with id: " + order.getId());
        }
    }

    @Override
    public void deleteOrder(Long id) throws OrderNotFoundException {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
        } else {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
    }

    @Override
    public Order findByCustomerAndProduct(Customer customer, Cupcake cupcake) {
        // Delegate the call to the repository
        return orderRepository.findByCustomerAndCupcake(customer, cupcake).orElse(null);
    }
}
