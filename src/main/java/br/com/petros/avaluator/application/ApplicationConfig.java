package br.com.petros.avaluator.application;

import br.com.petros.avaluator.domain.service.BoardEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApplicationConfig.class);

	@Bean
	public BoardEvaluator boardEvaluator() {
		if (log.isDebugEnabled()) {
			log.debug("Inicializando bean BoardEvaluator");
		}
		return new BoardEvaluator();
	}
}
