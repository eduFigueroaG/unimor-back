package mx.edu.utez.unimor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class UnimorApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(UnimorApplication.class, args);
	}

}
