package br.com.petros.avaluator.infrastructure.web.error;

import br.com.petros.avaluator.domain.exception.InvalidBoardException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ApiExceptionHandlerTest {

	private MockMvc mockMvc;

	@RestController
	static class BoomController {
		@GetMapping("/boom-invalid")
		public void boomInvalid() {
			throw new InvalidBoardException("O tabuleiro não pode ser nulo");
		}

		@GetMapping("/boom-generic")
		public void boomGeneric() {
			throw new RuntimeException("kaboom");
		}
	}

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(new BoomController())
				.setControllerAdvice(new ApiExceptionHandler())
				.build();
	}

	@Test
	void deve_retornar_422_com_mensagem_quando_InvalidBoardException() throws Exception {
		mockMvc.perform(get("/boom-invalid").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity()) // 422
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("O tabuleiro não pode ser nulo"));
	}

	@Test
	void deve_retornar_500_com_payload_padronizado_quando_excecao_generica() throws Exception {
		mockMvc.perform(get("/boom-generic").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("internal_error"));
	}
}
