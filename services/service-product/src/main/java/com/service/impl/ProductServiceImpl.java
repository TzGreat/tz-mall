package com.service.impl;

import com.example.Product.been.Product;
import com.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public Product getProductByid(Long productId) {
        Product product=new Product();
        product.setId(productId);
        product.setProductName("苹果"+productId);
        product.setPrice(new BigDecimal("99"));
        product.setNum(100);
        return product;
    }
}
