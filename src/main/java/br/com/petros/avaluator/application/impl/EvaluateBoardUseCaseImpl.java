package br.com.petros.avaluator.application.impl;

import br.com.petros.avaluator.application.EvaluateBoardUseCase;
import br.com.petros.avaluator.domain.model.Board;
import br.com.petros.avaluator.domain.model.CellMark;
import br.com.petros.avaluator.domain.model.GameResult;
import br.com.petros.avaluator.domain.model.GameStatus;
import br.com.petros.avaluator.domain.service.BoardEvaluator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EvaluateBoardUseCaseImpl implements EvaluateBoardUseCase {

	private static final Logger log = LoggerFactory.getLogger(EvaluateBoardUseCaseImpl.class);

	private final BoardEvaluator evaluator;

	@Override
	public GameResult execute(Board board) {
		long t0 = System.nanoTime();
		String rid = MDC.get("reqId"); // opcional (se houver Filter populando)
		boolean ok = false;
		GameStatus status = null;
		CellMark winner = null;

		try {
			log.debug("rid={}, início do execute, board={}", rid, board);

			GameResult result = evaluator.evaluate(board);
			status = result.status();
			winner = result.winner().orElse(CellMark.EMPTY);
			ok = true;

			return result;
		} finally {
			long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
			if (ok) {
				log.info("rid={}, fim do execute, status={}, winner={} elapsedMs={}",
						rid, status, winner, elapsedMs);
			} else {
				log.info("rid={}, fim do execute com ERRO, elapsedMs={}", rid, elapsedMs);
			}
		}
	}
}
