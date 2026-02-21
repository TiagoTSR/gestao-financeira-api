package br.com.decodex.gestaofinanceira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import br.com.decodex.gestaofinanceira.config.property.GestaoApiProperty;

@SpringBootApplication
@EnableConfigurationProperties(GestaoApiProperty.class)
public class GestaoFinanceiraApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestaoFinanceiraApiApplication.class, args);
	}

}
