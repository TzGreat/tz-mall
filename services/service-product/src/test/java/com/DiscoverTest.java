package com;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;

@SpringBootTest
public class DiscoverTest {

    @Autowired
    DiscoveryClient discoveryClient;

    @Test
    void discoveryClientTest(){
        for (String service:discoveryClient.getServices()){
            System.out.println(service);
        }
    }
}
