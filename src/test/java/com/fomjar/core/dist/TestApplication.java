package com.fomjar.core.dist;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.fomjar.core"})
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class);
    }

}
