package com.example.Order.been;

import com.example.Product.been.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private String name;
    private List<Product> products;
    private long userId;
    private long totalPrice;
    private String address;
}
