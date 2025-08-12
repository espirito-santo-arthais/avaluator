package br.com.petros.avaluator.application;

import br.com.petros.avaluator.domain.model.Board;
import br.com.petros.avaluator.domain.model.GameResult;

public interface EvaluateBoardUseCase {
	
	GameResult execute(Board board);
}
