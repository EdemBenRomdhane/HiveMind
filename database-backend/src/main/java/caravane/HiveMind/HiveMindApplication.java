package caravane.HiveMind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.context.annotation.ComponentScan(basePackages = { "caravane.HiveMind", "com.security.backend" })
public class HiveMindApplication {

	public static void main(String[] args) {
		SpringApplication.run(HiveMindApplication.class, args);
	}
}
