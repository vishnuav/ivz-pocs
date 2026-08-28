package com.ivz.p2iws;

import com.ivz.p2iws.auth.IvzAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.ivz"})
@EnableConfigurationProperties(IvzAuthProperties.class)
public class IvzP2iwsServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(IvzP2iwsServerApplication.class, args);
  }
}