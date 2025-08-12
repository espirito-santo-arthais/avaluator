package br.com.petros.avaluator.domain.service;

import br.com.petros.avaluator.domain.exception.InvalidBoardException;
import br.com.petros.avaluator.domain.model.*;

import java.util.Optional;

public class BoardEvaluator {

	public GameResult evaluate(Board board) {
		int x = board.count(CellMark.X), o = board.count(CellMark.O), empty = board.count(CellMark.EMPTY);
		int diff = x - o;
		if (diff < 0 || diff > 1) {
			throw new InvalidBoardException("contagem inválida: X-O deve ser 0 ou 1");
		}

		boolean xWins = wins(board, CellMark.X);
		boolean oWins = wins(board, CellMark.O);

		if (xWins && oWins) {
			throw new InvalidBoardException("X e O não podem vencer ao mesmo tempo");
		}

		if (xWins) {
			if (diff != 1) {
				throw new InvalidBoardException("estado inconsistente para vitória de X");
			}
			return new GameResult(GameStatus.X_WINS, Optional.of(CellMark.X));
		}
		if (oWins) {
			if (diff != 0) {
				throw new InvalidBoardException("estado inconsistente para vitória de O");
			}
			return new GameResult(GameStatus.O_WINS, Optional.of(CellMark.O));
		}

		if (empty == 0) {
			return new GameResult(GameStatus.DRAW, Optional.empty());
		}

		return new GameResult(GameStatus.ONGOING, Optional.empty());
	}

	private boolean wins(Board board, CellMark cellMark) {
		// linhas
		for (int i = 0; i < 3; i++) {
			if (board.get(i, 0) == cellMark && board.get(i, 1) == cellMark && board.get(i, 2) == cellMark) {
				return true;
			}
		}

		// colunas
		for (int j = 0; j < 3; j++) {
			if (board.get(0, j) == cellMark && board.get(1, j) == cellMark && board.get(2, j) == cellMark) {
				return true;
			}
		}

		// diagonais
		if (board.get(0, 0) == cellMark && board.get(1, 1) == cellMark && board.get(2, 2) == cellMark) {
			return true;
		}
		
		return board.get(0, 2) == cellMark && board.get(1, 1) == cellMark && board.get(2, 0) == cellMark;
	}
}
