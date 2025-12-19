package com.tfm.edcuaweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.tfm.edcuaweb")
public class EdcuawebApplication {
    public static void main(String[] args) {
        SpringApplication.run(EdcuawebApplication.class, args);
        System.out.println("✅ Educaweb iniciado correctamente");
    }
}
