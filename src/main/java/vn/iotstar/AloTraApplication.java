package vn.iotstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "vn.iotstar")
@ComponentScan(basePackages = "vn.iotstar")
public class AloTraApplication {

	public static void main(String[] args) {
		SpringApplication.run(AloTraApplication.class, args);
	}

}