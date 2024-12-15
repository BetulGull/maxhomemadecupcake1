package com.example.demo.Services;

import com.example.demo.Model.*;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public CartService(CartRepository cartRepository, OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.customerRepository=customerRepository;
    }

    // Method to get cart items by customer
    public List<Cart> getCartItemsByCustomer(Customer customer) {
        return cartRepository.findByCustomer(customer);
    }
    public List<CartItem> getCartItems() {
        // Get the currently authenticated principal
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if the principal is an instance of your Customer class
        if (principal instanceof Customer) {
            Customer loggedInCustomer = (Customer) principal;

            // Get the customer's cart items
            List<Cart> carts = getCartItemsByCustomer(loggedInCustomer);

            // Return all CartItems by flattening the list of carts
            return carts.stream()
                    .flatMap(cart -> cart.getItems().stream())
                    .toList();
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            // Handle case where the principal is a Spring Security User
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();

            // You may need to find the customer by username or other fields in your application
            Customer loggedInCustomer = customerRepository.findByUsername(username);

            // Get the customer's cart items
            List<Cart> carts = getCartItemsByCustomer(loggedInCustomer);

            // Return all CartItems by flattening the list of carts
            return carts.stream()
                    .flatMap(cart -> cart.getItems().stream())
                    .toList();
        } else {
            throw new RuntimeException("Authenticated user is not of type Customer.");
        }
    }
    public Order checkout(Cart cart) {
        // Create a new Order from the Cart
        Order order = new Order();
        order.setCustomer(cart.getCustomer());
        order.setTotalAmount(cart.getTotalAmount());
        order.setDate(new Date());
        order.setDeliveryStatus("Pending");

        // Convert each CartItem into an OrderItem
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());

            // Add to the Order's orderItems
            order.addOrderItem(orderItem);
        }

        // Save the order to the database
        orderRepository.save(order);

        // Clear the cart after checkout
        cart.getItems().clear();
        cart.setTotalAmount(0);  // Reset totalAmount
        cartRepository.save(cart); // Save updated cart

        return order;  // Return the created order
    }



    // Method to handle checkout
    public void checkout(Customer customer) {
        List<Cart> cartItems = getCartItemsByCustomer(customer);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("No items in cart to checkout.");
        }

        // Loop through each Cart (this is the cart containing CartItems)
        for (Cart cart : cartItems) {
            // Loop through CartItems (the actual products in the cart)
            for (CartItem cartItem : cart.getItems()) {  // Accessing CartItems from Cart
                Order order = new Order();
                order.setCustomer(customer);
                order.setProduct(cartItem.getProduct());  // Access Product via CartItem
                //order.setQuantity(cartItem.getQuantity());  // Access Quantity via CartItem
                order.setDate(new Date());

                orderRepository.save(order);  // Save the order

                // Optionally, remove the cart item after order creation
                cartRepository.delete(cart);
            }
        }
    }
}
