package br.com.petros.avaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EvaluatorApplication {
	
	private static final Logger log = LoggerFactory.getLogger(EvaluatorApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(EvaluatorApplication.class, args);
        log.info("🚀 Avaluator está ativo e pronto para receber requisições.");

        if (log.isDebugEnabled()) {
            log.debug("Matriz: {}", gerarMatrizExemplo());
        }
	}

	private static String gerarMatrizExemplo() {
		// Simulação de operação custosa
		return "[[X, O, X], [O, X, O], [ ,  , X]]";
	}
}
