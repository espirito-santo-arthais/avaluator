package br.com.petros.avaluator.application;

import br.com.petros.avaluator.domain.service.BoardEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
	
	@Bean
	public BoardEvaluator boardEvaluator() {
		return new BoardEvaluator(); // domínio sem dependências de Spring
	}
}
