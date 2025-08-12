package br.com.petros.avaluator.domain.service;

import br.com.petros.avaluator.domain.exception.InvalidBoardException;
import br.com.petros.avaluator.domain.model.*;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class BoardEvaluator {

	private static final Logger log = LoggerFactory.getLogger(BoardEvaluator.class);

	public GameResult evaluate(Board board) {
		String rid = MDC.get("reqId"); // opcional

		int x = board.count(CellMark.X), o = board.count(CellMark.O), empty = board.count(CellMark.EMPTY);
		int diff = x - o;

		if (diff < 0 || diff > 1) {
			log.warn("rid={}, contagem inválida: diff={} (x={}, o={}, empty={})", rid, diff, x, o, empty);
			throw new InvalidBoardException("contagem inválida: X-O deve ser 0 ou 1");
		}

		boolean xWins = wins(board, CellMark.X, rid);
		boolean oWins = wins(board, CellMark.O, rid);

		if (xWins && oWins) {
			log.warn("rid={}, estado inconsistente: X e O vencendo ao mesmo tempo", rid);
			throw new InvalidBoardException("X e O não podem vencer ao mesmo tempo");
		}

		if (xWins) {
			if (diff != 1) {
				log.warn("rid={}, estado inconsistente: X vence mas diff={}", rid, diff);
				throw new InvalidBoardException("estado inconsistente para vitória de X");
			}
			log.info("rid={}, resultado=X_WINS", rid);
			return new GameResult(GameStatus.X_WINS, Optional.of(CellMark.X));
		}

		if (oWins) {
			if (diff != 0) {
				log.warn("rid={} estado inconsistente: O vence mas diff={}", rid, diff);
				throw new InvalidBoardException("estado inconsistente para vitória de O");
			}
			log.info("rid={}, resultado=O_WINS", rid);
			return new GameResult(GameStatus.O_WINS, Optional.of(CellMark.O));
		}

		if (empty == 0) {
			log.info("rid={}, resultado=DRAW", rid);
			return new GameResult(GameStatus.DRAW, Optional.empty());
		}

		if (log.isDebugEnabled()) {
			log.debug("rid={}, resultado=ONGOING (x={}, o={}, vazias={})", rid, x, o, empty);
		}
		return new GameResult(GameStatus.ONGOING, Optional.empty());
	}

	private boolean wins(Board board, CellMark cellMark, String rid) {
		// linhas
		for (int i = 0; i < 3; i++) {
			if (board.get(i, 0) == cellMark && board.get(i, 1) == cellMark && board.get(i, 2) == cellMark) {
				if (log.isDebugEnabled()) {
					log.debug("rid={}, {} vence por linha {}", rid, cellMark, i + 1);
				}
				return true;
			}
		}
		// colunas
		for (int j = 0; j < 3; j++) {
			if (board.get(0, j) == cellMark && board.get(1, j) == cellMark && board.get(2, j) == cellMark) {
				if (log.isDebugEnabled()) {
					log.debug("rid={}, {} vence por coluna {}", rid, cellMark, j + 1);
				}
				return true;
			}
		}
		// diagonais
		if (board.get(0, 0) == cellMark && board.get(1, 1) == cellMark && board.get(2, 2) == cellMark) {
			if (log.isDebugEnabled()) {
				log.debug("rid={}, {} vence pela diagonal principal", rid, cellMark);
			}
			return true;
		}
		if (board.get(0, 2) == cellMark && board.get(1, 1) == cellMark && board.get(2, 0) == cellMark) {
			if (log.isDebugEnabled()) {
				log.debug("rid={}, {} vence pela diagonal secundária", rid, cellMark);
			}
			return true;
		}
		return false;
	}
}
