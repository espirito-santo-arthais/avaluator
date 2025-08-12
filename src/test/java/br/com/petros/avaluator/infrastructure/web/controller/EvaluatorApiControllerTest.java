package br.com.petros.avaluator.infrastructure.web.controller;

import br.com.petros.avaluator.application.EvaluateBoardUseCase;
import br.com.petros.avaluator.domain.exception.InvalidBoardException;
import br.com.petros.avaluator.domain.model.Board;
import br.com.petros.avaluator.domain.model.CellMark;
import br.com.petros.avaluator.domain.model.GameResult;
import br.com.petros.avaluator.domain.model.GameStatus;
import br.com.petros.avaluator.infrastructure.web.mapper.WebDtoMapper;
import br.com.petros.avaluator.generated.model.EvaluateResponse;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluatorApiController.class)
class EvaluatorApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EvaluateBoardUseCase useCase;

	@MockBean
	private WebDtoMapper mapper;

	@Test
	void deve_retornar_200_quando_sucesso() throws Exception {
		var result = new GameResult(GameStatus.X_WINS, Optional.of(CellMark.X));
		var response = new EvaluateResponse();
		response.setStatus(br.com.petros.avaluator.generated.model.GameStatus.X_WINS);
		response.setWinner(br.com.petros.avaluator.generated.model.Mark.X);

		Mockito.when(useCase.execute(any())).thenReturn(result);
		Mockito.when(mapper.toDto(result)).thenReturn(response);
		// mapper.toDomainBoard(...) será chamado — podemos simplesmente permitir any() retornar um Board mockado
		Mockito.when(mapper.toDomainBoard(any())).thenReturn(Mockito.mock(Board.class));

		String body = """
				{
				  "board": {
				    "r1": {"c1":"X","c2":"O","c3":"X"},
				    "r2": {"c1":"O","c2":"X","c3":"O"},
				    "r3": {"c1":"EMPTY","c2":"EMPTY","c3":"X"}
				  }
				}
				""";

		mockMvc.perform(post("/api/evaluator/evaluate")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(body))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("X_WINS"))
				.andExpect(jsonPath("$.winner").value("X"));
	}

	@Test
	void deve_retornar_400_quando_payload_invalido() throws Exception {
		Mockito.when(mapper.toDomainBoard(any())).thenThrow(new InvalidBoardException("O tabuleiro não pode ser nulo"));

		String bodyInvalido = """
				{ "board": null }
				""";

		mockMvc.perform(post("/api/evaluator/evaluate")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(bodyInvalido))
				.andExpect(status().isUnprocessableEntity());
	}
}
