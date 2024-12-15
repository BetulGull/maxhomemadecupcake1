package com.example.demo.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    private List<CartItem> items = new ArrayList<>();  // List to hold CartItems

    private double totalAmount; // Add totalAmount field to track the cart's total value

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        recalculateTotalAmount();  // Recalculate the total amount when items are set
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    // addItem method to add a product to the cart
    public void addItem(Product product, int quantity) {
        // Check if the product already exists in the cart
        for (CartItem item : items) {
            if (item.getProduct().equals(product)) {
                item.setQuantity(item.getQuantity() + quantity);  // Increase quantity if the product is already in the cart
                recalculateTotalAmount();  // Recalculate total when quantity changes
                return;
            }
        }
        // If product is not in the cart, create a new CartItem and add it
        CartItem newItem = new CartItem();
        newItem.setProduct(product);
        newItem.setQuantity(quantity);  // Set the quantity specified by the user
        items.add(newItem);
        recalculateTotalAmount();  // Recalculate total after adding a new item
    }

    // Recalculate totalAmount by iterating through the items
    private void recalculateTotalAmount() {
        totalAmount = 0;
        for (CartItem item : items) {
            totalAmount += item.getProduct().getPrice() * item.getQuantity();
        }
    }
}
