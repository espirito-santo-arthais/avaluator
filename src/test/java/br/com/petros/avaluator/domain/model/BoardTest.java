package br.com.petros.avaluator.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BoardTest {

	/* === helpers === */

	private static Board board(String r1, String r2, String r3) {
		// r* aceita: X, O, E (ou .) — sempre 3 chars
		CellMark[][] cells = new CellMark[3][3];
		String[] rows = { r1, r2, r3 };
		for (int i = 0; i < 3; i++) {
			String row = rows[i].replaceAll("\\s+", "");
			if (row.length() != 3)
				throw new IllegalArgumentException("Linha deve ter 3 caracteres");
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

	/* === count(CellMark) === */

	@Test
	void count_deve_contar_X_O_e_EMPTY_corretamente() {
		// X=2, O=3, EMPTY=4
		Board board = board("XOE", "EXE", "OOE");

		assertEquals(2, board.count(CellMark.X));
		assertEquals(3, board.count(CellMark.O));
		assertEquals(4, board.count(CellMark.EMPTY));
	}

	@Test
	void count_em_tabuleiro_vazio_deve_retornar_9_zeros_para_os_outros() {
		Board board = board("EEE", "EEE", "EEE");

		assertEquals(0, board.count(CellMark.X));
		assertEquals(0, board.count(CellMark.O));
		assertEquals(9, board.count(CellMark.EMPTY));
	}

	/* === get(i,j) válidos === */

	@Test
	void get_deve_retornar_o_valor_correto_para_indices_validos() {
		Board board = board("XOE", "EXE", "OOE");
		// linha 1
		assertEquals(CellMark.X, board.get(0, 0));
		assertEquals(CellMark.O, board.get(0, 1));
		assertEquals(CellMark.EMPTY, board.get(0, 2));
		// linha 2
		assertEquals(CellMark.EMPTY, board.get(1, 0));
		assertEquals(CellMark.X, board.get(1, 1));
		assertEquals(CellMark.EMPTY, board.get(1, 2));
		// linha 3
		assertEquals(CellMark.O, board.get(2, 0));
		assertEquals(CellMark.O, board.get(2, 1));
		assertEquals(CellMark.EMPTY, board.get(2, 2));
	}

	/* === get(i,j) inválidos (limites) === */

	@Test
	void get_deve_lancar_IndexOutOfBounds_para_indices_invalidos() {
		Board board = board("EEE", "EEE", "EEE");

		assertThrows(IndexOutOfBoundsException.class, () -> board.get(-1, 0));
		assertThrows(IndexOutOfBoundsException.class, () -> board.get(3, 0));
		assertThrows(IndexOutOfBoundsException.class, () -> board.get(0, -1));
		assertThrows(IndexOutOfBoundsException.class, () -> board.get(0, 3));
	}
}
