package com.example.demo;

import com.example.demo.Model.Order;
import com.example.demo.Model.OrderItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderModelTest {

    @Test
    void testAddOrderItem() {
        Order order = new Order();
        OrderItem orderItem = new OrderItem();

        order.addOrderItem(orderItem);

        assertEquals(1, order.getOrderItems().size());
        assertTrue(order.getOrderItems().contains(orderItem));
    }

    @Test
    void testSetQuantityAndTotalAmount() {
        Order order = new Order();
        order.setQuantity(3);
        order.setTotalAmount(500);

        assertEquals(3, order.getQuantity());
        assertEquals(500, order.getTotalAmount(), 0.001);
    }
}
