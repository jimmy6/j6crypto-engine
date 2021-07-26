package com.j6crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.j6crypto",
  excludeFilters = @ComponentScan.Filter(SpringBootApplication.class))
public class J6CryptoApiApp {
  public static void main(String[] args) {
    SpringApplication.run(J6CryptoApiApp.class, args);
  }
}
