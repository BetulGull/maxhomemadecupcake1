package com.example.demo.Services;

import com.example.demo.Controller.OrderAlreadyExistsException;
import com.example.demo.DTOs.Orderview;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Order;
import com.example.demo.Model.Cupcake;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    boolean orderAlreadyExists(Order order);
    void saveOrder(Order order) throws OrderAlreadyExistsException;
    Order createOrder(Orderview orderview) throws OrderAlreadyExistsException, CustomerNotFoundException, CupcakeNotFoundException;
    Order getOrderById(Long id);
    public List<Order> getOrdersByCustomer(Customer customer);

    void updateOrder(Order order) throws OrderNotFoundException, CustomerNotFoundException, CupcakeNotFoundException;
    void deleteOrder(Long id) throws OrderNotFoundException;

    // Add the method signature for 'findByCustomerAndProduct'
    Order findByCustomerAndProduct(Customer customer, Cupcake cupcake);
}
