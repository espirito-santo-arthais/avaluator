package br.com.petros.avaluator.domain.service;

import br.com.petros.avaluator.domain.exception.InvalidBoardException;
import br.com.petros.avaluator.domain.model.Board;
import br.com.petros.avaluator.domain.model.CellMark;
import br.com.petros.avaluator.domain.model.GameResult;
import br.com.petros.avaluator.domain.model.GameStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BoardEvaluatorTest {

	private final BoardEvaluator evaluator = new BoardEvaluator();

	/* === Helpers === */

	private static Board board(String r1, String r2, String r3) {
		// rX: "XXX", "OEO", "EEE" (E = EMPTY)
		CellMark[][] cells = new CellMark[3][3];
		String[] rows = { r1, r2, r3 };
		for (int i = 0; i < 3; i++) {
			String row = rows[i].replaceAll("\\s+", "");
			if (row.length() != 3) {
				throw new IllegalArgumentException("Linha deve ter 3 caracteres: " + row);
			}
			for (int j = 0; j < 3; j++) {
				char c = row.charAt(j);
				cells[i][j] = switch (c) {
				case 'X', 'x' -> CellMark.X;
				case 'O', 'o' -> CellMark.O;
				case 'E', 'e', '.' -> CellMark.EMPTY;
				default -> throw new IllegalArgumentException("Caractere inválido: " + c);
				};
			}
		}
		return new Board(cells);
	}

	/* === Cenários de vitória (parametrizados) === */

	static Stream<Board> xWinBoards() {
		return Stream.of(
				// Linhas
				board("XXX", "OEE", "EOE"),
				board("OEE", "XXX", "EEO"),
				board("OEE", "EEO", "XXX"),
				// Colunas
				board("XOE", "XEO", "XEE"),
				board("OXE", "EXO", "EXE"),
				board("OEX", "EOX", "EEX"),
				// Diagonais
				board("XOE", "EXO", "EEX"), // diagonal principal X
				board("EOX", "OXE", "XEE") // diagonal secundária X
		);
	}

	static Stream<Board> oWinBoards() {
		return Stream.of(
				// Linhas
				board("OOO", "EXX", "EXE"),
				board("XEX", "OOO", "EXE"),
				board("XEE", "XXE", "OOO"),
				// Colunas
				board("OXE", "OEX", "OXE"),
				board("XOE", "EOX", "XOE"),
				board("XEO", "EXO", "XEO"),
				// Diagonais
				board("OXE", "EOX", "XEO"), // diagonal principal O
				board("XEO", "EOX", "OXE") // diagonal secundária O
		);
	}

	@ParameterizedTest(name = "X vence (caso {index})")
	@MethodSource("xWinBoards")
	void deve_retornar_X_WINS_e_winner_X(Board board) {
		GameResult gameResult = evaluator.evaluate(board);
		assertEquals(GameStatus.X_WINS, gameResult.status());
		assertTrue(gameResult.winner().isPresent());
		assertEquals(CellMark.X, gameResult.winner().get());
	}

	@ParameterizedTest(name = "O vence (caso {index})")
	@MethodSource("oWinBoards")
	void deve_retornar_O_WINS_e_winner_O(Board board) {
		GameResult gameResult = evaluator.evaluate(board);
		assertEquals(GameStatus.O_WINS, gameResult.status());
		assertTrue(gameResult.winner().isPresent());
		assertEquals(CellMark.O, gameResult.winner().get());
	}

	/* === Erros de contagem (diff < 0, diff > 1) === */

	@Test
	void deve_lancar_quando_diff_menor_que_zero() {
		// X=1, O=2 -> diff = -1
		Board board = board("OOE", "XEE", "EEE");
		InvalidBoardException ex = assertThrows(InvalidBoardException.class, () -> evaluator.evaluate(board));
		assertEquals("contagem inválida: X-O deve ser 0 ou 1", ex.getMessage());
	}

	@Test
	void deve_lancar_quando_diff_maior_que_um() {
		// X=3, O=1 -> diff = 2
		Board board = board("XXE", "XOE", "EEE");
		InvalidBoardException ex = assertThrows(InvalidBoardException.class, () -> evaluator.evaluate(board));
		assertEquals("contagem inválida: X-O deve ser 0 ou 1", ex.getMessage());
	}

	/* === Dupla vitória (X e O ao mesmo tempo) === */

	@Test
	void deve_lancar_quando_X_e_O_vencem_ao_mesmo_tempo() {
		// X vence na linha 1; O vence na linha 3; diff = 0 (válido para passar da 1ª
		// checagem)
		Board board = board("XXX", "EEE", "OOO");
		InvalidBoardException ex = assertThrows(InvalidBoardException.class, () -> evaluator.evaluate(board));
		assertEquals("X e O não podem vencer ao mesmo tempo", ex.getMessage());
	}

	/* === Paridade errada === */

	@Test
	void deve_lancar_quando_X_vence_mas_diff_nao_e_1() {
		// X vence (linha 1), diff=0 (X=3, O=3) -> paridade inválida
		Board board = board("XXX", "OOE", "OEE");
		InvalidBoardException ex = assertThrows(InvalidBoardException.class, () -> evaluator.evaluate(board));
		assertEquals("estado inconsistente para vitória de X", ex.getMessage());
	}

	@Test
	void deve_lancar_quando_O_vence_mas_diff_nao_e_0() {
		// O vence (linha 3), diff=1 (X=4, O=3) -> paridade inválida
		Board board = board("XXE", "XEX", "OOO");
		InvalidBoardException ex = assertThrows(InvalidBoardException.class, () -> evaluator.evaluate(board));
		assertEquals("estado inconsistente para vitória de O", ex.getMessage());
	}

	/* === DRAW e ONGOING === */

	@Test
	void deve_retornar_DRAW_quando_tabuleiro_cheio_sem_vencedor() {
		Board board = board("XOX", "XOO", "OXX");
		GameResult r = evaluator.evaluate(board);
		assertEquals(GameStatus.DRAW, r.status());
		assertEquals(Optional.empty(), r.winner());
	}

	@Test
	void deve_retornar_ONGOING_quando_existe_EMPTY_sem_vencedor() {
		Board board = board("XOE", "EXE", "EEE");
		GameResult r = evaluator.evaluate(board);
		assertEquals(GameStatus.ONGOING, r.status());
		assertEquals(Optional.empty(), r.winner());
	}
}
