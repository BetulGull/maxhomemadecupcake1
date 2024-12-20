package com.example.demo.Model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Order1")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;  // Add a field for quantity
    private double totalAmount;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderItem> orderItems = new HashSet<>();

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
    }


    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String deliveryStatus;
    private String city;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Cupcake cupcake;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter for quantity
    public int getQuantity() {
        return quantity;
    }

    // Setter for totalAmount
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    // Getter for totalAmount
    public double getTotalAmount() {
        return totalAmount;
    }




}
