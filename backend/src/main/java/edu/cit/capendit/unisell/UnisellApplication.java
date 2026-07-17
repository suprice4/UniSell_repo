package edu.cit.capendit.unisell;

import edu.cit.capendit.unisell.auth.config.AdminProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AdminProperties.class)
public class UnisellApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnisellApplication.class, args);
	}

}