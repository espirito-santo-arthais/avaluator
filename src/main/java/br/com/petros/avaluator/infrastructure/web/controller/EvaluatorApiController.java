package br.com.petros.avaluator.infrastructure.web.controller;

import br.com.petros.avaluator.application.EvaluateBoardUseCase;
import br.com.petros.avaluator.domain.model.Board;
import br.com.petros.avaluator.domain.model.CellMark;
import br.com.petros.avaluator.domain.model.GameResult;
import br.com.petros.avaluator.domain.model.GameStatus;
import br.com.petros.avaluator.generated.api.ApiApi;
import br.com.petros.avaluator.generated.model.EvaluateRequest;
import br.com.petros.avaluator.generated.model.EvaluateResponse;
import br.com.petros.avaluator.infrastructure.web.mapper.WebDtoMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EvaluatorApiController implements ApiApi {

	private static final Logger log = LoggerFactory.getLogger(EvaluatorApiController.class);

	private final EvaluateBoardUseCase useCase;
	private final WebDtoMapper mapper;

	public EvaluatorApiController(EvaluateBoardUseCase useCase, WebDtoMapper mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public ResponseEntity<EvaluateResponse> evaluateBoard(EvaluateRequest body) {
		long t0 = System.nanoTime();
		String rid = MDC.get("reqId"); // opcional, caso populemos via Filter
		boolean ok = false;
		GameStatus status = null;
		CellMark winner = null;

		try {
			Board board = mapper.toDomainBoard(body);
			if (log.isDebugEnabled()) {
				log.debug("rid={} board={}", rid, board); // opcional
			}

			GameResult result = useCase.execute(board);
			status = result.status();
			winner = result.winner().orElse(CellMark.EMPTY);
			ok = true;

			return ResponseEntity.ok(mapper.toDto(result));
		} finally {
			long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
			if (ok) {
				log.info("rid={}, fim do evaluateBoard, status={}, winner={}, elapsedMs={}",
						rid, status, winner, elapsedMs);
			} else {
				log.info("rid={}, fim do evaluateBoard com ERRO, elapsedMs={}", rid, elapsedMs);
			}
		}
	}
}
