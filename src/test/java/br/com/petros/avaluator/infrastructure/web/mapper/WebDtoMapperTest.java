package br.com.petros.avaluator.infrastructure.web.mapper;

import br.com.petros.avaluator.domain.exception.InvalidBoardException;
import br.com.petros.avaluator.domain.model.Board;
import br.com.petros.avaluator.domain.model.CellMark;
import br.com.petros.avaluator.domain.model.GameResult;
import br.com.petros.avaluator.domain.model.GameStatus;
import br.com.petros.avaluator.generated.model.BoardDto;
import br.com.petros.avaluator.generated.model.EvaluateRequest;
import br.com.petros.avaluator.generated.model.EvaluateResponse;
import br.com.petros.avaluator.generated.model.Mark;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebDtoMapperTest {

	private final WebDtoMapper mapper = new WebDtoMapper();

	/*
	 * === toDomainBoard - erros (board/r1/r2/r3 nulos) ===
	 */

	@Test
	void toDomainBoard_deve_lancar_quando_board_nulo() {
		EvaluateRequest request = mock(EvaluateRequest.class);
		when(request.getBoard()).thenReturn(null);

		InvalidBoardException ex = assertThrows(InvalidBoardException.class, () -> mapper.toDomainBoard(request));
		assertEquals("O tabuleiro não pode ser nulo", ex.getMessage());
	}

	@Test
	void toDomainBoard_deve_lancar_quando_r1_nulo() {
		EvaluateRequest request = mock(EvaluateRequest.class);
		BoardDto boardDto = mock(BoardDto.class);
		when(request.getBoard()).thenReturn(boardDto);
		when(boardDto.getR1()).thenReturn(null);

		InvalidBoardException ex = assertThrows(InvalidBoardException.class, () -> mapper.toDomainBoard(request));
		assertEquals("A linha 1 do tabuleiro não pode ser nulo", ex.getMessage());
	}

	@Test
	void toDomainBoard_deve_lancar_quando_r2_nulo() {
		EvaluateRequest request = mock(EvaluateRequest.class);
		BoardDto boardDto = mock(BoardDto.class, RETURNS_DEEP_STUBS);
		when(request.getBoard()).thenReturn(boardDto);

		// NÃO precisa stubbar r1: deep stub já garante não nulo
		when(boardDto.getR2()).thenReturn(null);

		InvalidBoardException ex = assertThrows(InvalidBoardException.class, () -> mapper.toDomainBoard(request));
		assertEquals("A linha 2 do tabuleiro não pode ser nulo", ex.getMessage());
	}

	@Test
	void toDomainBoard_deve_lancar_quando_r3_nulo() {
		EvaluateRequest request = mock(EvaluateRequest.class);
		BoardDto boardDto = mock(BoardDto.class, RETURNS_DEEP_STUBS);
		when(request.getBoard()).thenReturn(boardDto);

		// r1 e r2 ficam não nulos via deep stubs; só forçamos r3 = null
		when(boardDto.getR3()).thenReturn(null);

		InvalidBoardException ex = assertThrows(InvalidBoardException.class, () -> mapper.toDomainBoard(request));
		assertEquals("A linha 3 do tabuleiro não pode ser nulo", ex.getMessage());
	}

	/*
	 * === toDomainBoard - células nulas -> EMPTY ===
	 */

	@Test
	void toDomainBoard_deve_normalizar_celulas_nulas_para_EMPTY() {
		EvaluateRequest request = mock(EvaluateRequest.class);
		BoardDto boardDto = mock(BoardDto.class, RETURNS_DEEP_STUBS);
		when(request.getBoard()).thenReturn(boardDto);

		// r1
		when(boardDto.getR1().getC1()).thenReturn(null); // -> EMPTY
		when(boardDto.getR1().getC2()).thenReturn(Mark.X); // -> X
		when(boardDto.getR1().getC3()).thenReturn(Mark.O); // -> O

		// r2
		when(boardDto.getR2().getC1()).thenReturn(Mark.EMPTY); // -> EMPTY
		when(boardDto.getR2().getC2()).thenReturn(null); // -> EMPTY
		when(boardDto.getR2().getC3()).thenReturn(Mark.X); // -> X

		// r3
		when(boardDto.getR3().getC1()).thenReturn(Mark.O); // -> O
		when(boardDto.getR3().getC2()).thenReturn(Mark.EMPTY); // -> EMPTY
		when(boardDto.getR3().getC3()).thenReturn(null); // -> EMPTY

		Board board = mapper.toDomainBoard(request);

		// r1
		assertEquals(CellMark.EMPTY, board.get(0, 0));
		assertEquals(CellMark.X, board.get(0, 1));
		assertEquals(CellMark.O, board.get(0, 2));
		// r2
		assertEquals(CellMark.EMPTY, board.get(1, 0));
		assertEquals(CellMark.EMPTY, board.get(1, 1));
		assertEquals(CellMark.X, board.get(1, 2));
		// r3
		assertEquals(CellMark.O, board.get(2, 0));
		assertEquals(CellMark.EMPTY, board.get(2, 1));
		assertEquals(CellMark.EMPTY, board.get(2, 2));
	}

	/*
	 * === toDto - mapeamento de status e winner ===
	 */

	@Test
	void toDto_deve_mapear_status_e_winner_presente() {
		GameResult result = new GameResult(GameStatus.X_WINS, Optional.of(CellMark.X));

		EvaluateResponse dto = mapper.toDto(result);

		assertEquals(br.com.petros.avaluator.generated.model.GameStatus.X_WINS, dto.getStatus());
		assertEquals(Mark.X, dto.getWinner());
	}

	@Test
	void toDto_deve_mapear_winner_ausente_para_EMPTY() {
		GameResult result = new GameResult(GameStatus.DRAW, Optional.empty());

		EvaluateResponse dto = mapper.toDto(result);

		assertEquals(br.com.petros.avaluator.generated.model.GameStatus.DRAW, dto.getStatus());
		assertEquals(Mark.EMPTY, dto.getWinner());
	}
}
