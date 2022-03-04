package com.example.familybenefitstown;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FamilyBenefitsTownApplication {

  public static void main(String[] args) {
    PropertyConfigurator.configure("classpath:log4j.properties");
    SpringApplication.run(FamilyBenefitsTownApplication.class, args);
  }

}
