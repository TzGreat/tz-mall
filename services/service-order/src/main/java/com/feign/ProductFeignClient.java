package com.feign;

import com.example.Product.been.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-product")
public interface ProductFeignClient {

    @GetMapping("/product/{Id}")
     Product getProductById(@PathVariable(value = "Id") Long Id);
}
