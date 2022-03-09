package com.example.familybenefitstown;

import com.example.familybenefitstown.resources.R;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FamilyBenefitsTownApplication {

  public static void main(String[] args) {
    PropertyConfigurator.configure(R.LOG_CONFIG_FILE_NAME);
    SpringApplication.run(FamilyBenefitsTownApplication.class, args);
  }

}
