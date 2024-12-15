package com.example.demo.DTOs;

import com.example.demo.Model.Customer;
import com.example.demo.Model.Product;
import lombok.*;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Orderview {
    private Long id;
    private String city;
    private String deliveryStatus;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private Long productId;
    private Long customerId;
    private String customerName;
    private String productName;


}
