package br.com.petros.avaluator.domain.model;

import java.util.Objects;

import lombok.ToString;

@ToString
public final class Board {
	
	public static final String BOARD_DIMENSION_ERROR_MESSAGE = "Tabuleiro deve ser 3x3";

	private final CellMark[][] cells; // 3x3

	public Board(CellMark[][] cells) {
		if (cells == null || cells.length != 3) {
			throw new IllegalArgumentException(BOARD_DIMENSION_ERROR_MESSAGE);
		}
		this.cells = new CellMark[3][3];
		for (int i = 0; i < 3; i++) {
			if (cells[i] == null || cells[i].length != 3) {
				throw new IllegalArgumentException(BOARD_DIMENSION_ERROR_MESSAGE);
			}
			for (int j = 0; j < 3; j++) {
				this.cells[i][j] = Objects.requireNonNullElse(cells[i][j], CellMark.EMPTY);
			}
		}
	}

	public CellMark get(int i, int j) {
		return cells[i][j];
	}

	public int count(CellMark m) {
		int c = 0;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (cells[i][j] == m)
					c++;
		return c;
	}
}
