package com.jordanrobin.financial_erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FinancialErpApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialErpApplication.class, args);
	}

}
