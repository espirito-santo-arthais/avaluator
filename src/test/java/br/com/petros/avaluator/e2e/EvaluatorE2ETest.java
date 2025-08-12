package br.com.petros.avaluator.e2e;

import br.com.petros.avaluator.generated.model.EvaluateResponse;
import br.com.petros.avaluator.generated.model.Error;
import br.com.petros.avaluator.generated.model.GameStatus;
import br.com.petros.avaluator.generated.model.Mark;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EvaluatorE2ETest {

	@Autowired
	private TestRestTemplate rest;

	@Test
	void deve_retornar_X_WINS_quando_linha1() {
		String body = """
				{
				  "board":{
				    "r1":{"c1":"X","c2":"X","c3":"X"},
				    "r2":{"c1":"O","c2":"EMPTY","c3":"EMPTY"},
				    "r3":{"c1":"EMPTY","c2":"O","c3":"EMPTY"}
				  }
				}
				""";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

		ResponseEntity<EvaluateResponse> resp = rest.postForEntity(
				"/api/evaluator/evaluate",
				new HttpEntity<>(body, headers),
				EvaluateResponse.class);

		assertEquals(HttpStatus.OK, resp.getStatusCode());
		EvaluateResponse dto = resp.getBody();
		assertNotNull(dto);
		assertEquals(GameStatus.X_WINS, dto.getStatus());
		assertEquals(Mark.X, dto.getWinner());
	}

	@Test
	void deve_retornar_422_quando_board_nulo() {
		String body = """
				{ "board": null }
				""";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

		ResponseEntity<Error> response = rest.postForEntity(
				"/api/evaluator/evaluate",
				new HttpEntity<>(body, headers),
				Error.class);

		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("O tabuleiro não pode ser nulo", response.getBody().getMessage());
	}
	
	// Aqui podemos automatizar todas as situações. Não fiz para não demorar mais
	// para entregar.
}
