package com.mgmtp.easyquizy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.mgmtp.easyquizy.model")
@SpringBootApplication
public class EasyQuizyBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyQuizyBeApplication.class, args);
    }

}
