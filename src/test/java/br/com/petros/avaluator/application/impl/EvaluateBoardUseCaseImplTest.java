package br.com.petros.avaluator.application.impl;

import br.com.petros.avaluator.domain.exception.InvalidBoardException;
import br.com.petros.avaluator.domain.model.Board;
import br.com.petros.avaluator.domain.model.CellMark;
import br.com.petros.avaluator.domain.model.GameResult;
import br.com.petros.avaluator.domain.model.GameStatus;
import br.com.petros.avaluator.domain.service.BoardEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluateBoardUseCaseImplTest {

	@Mock
	private BoardEvaluator evaluator;

	@Mock
	private Board board;

	private EvaluateBoardUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		useCase = new EvaluateBoardUseCaseImpl(evaluator);
	}

	@Test
	void deve_chamar_evaluator_uma_vez_e_retornar_o_mesmo_resultado() {
		// arrange
		GameResult esperado = new GameResult(GameStatus.X_WINS, Optional.of(CellMark.X));
		when(evaluator.evaluate(board)).thenReturn(esperado);

		// act
		GameResult obtido = useCase.execute(board);

		// assert
		assertSame(esperado, obtido, "deve retornar exatamente a mesma instância de GameResult");
		verify(evaluator, times(1)).evaluate(board);
		verifyNoMoreInteractions(evaluator);
	}

	@Test
	void deve_propagar_InvalidBoardException_sem_tratar() {
		// arrange
		InvalidBoardException esperado = new InvalidBoardException("contagem inválida: X-O deve ser 0 ou 1");
		when(evaluator.evaluate(board)).thenThrow(esperado);

		// act + assert
		InvalidBoardException lancada = assertThrows(InvalidBoardException.class, () -> useCase.execute(board));
		assertSame(esperado, lancada, "a mesma exceção deve ser propagada (sem wrapping)");

		verify(evaluator, times(1)).evaluate(board);
		verifyNoMoreInteractions(evaluator);
	}
}
