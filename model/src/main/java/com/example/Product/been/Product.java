package com.example.Product.been;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;
    public BigDecimal price;
    private String productName;
    private int num;
}
