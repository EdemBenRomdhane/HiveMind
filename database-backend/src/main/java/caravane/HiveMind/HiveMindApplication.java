package caravane.HiveMind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.context.annotation.ComponentScan(basePackages = { "caravane.HiveMind", "com.security.backend" })
@org.springframework.data.cassandra.repository.config.EnableCassandraRepositories(basePackages = "com.security.backend.repository")
public class HiveMindApplication {

	public static void main(String[] args) {
		SpringApplication.run(HiveMindApplication.class, args);
	}
}
