package br.com.petros.avaluator.application.impl;

import br.com.petros.avaluator.application.EvaluateBoardUseCase;
import br.com.petros.avaluator.domain.model.Board;
import br.com.petros.avaluator.domain.model.GameResult;
import br.com.petros.avaluator.domain.service.BoardEvaluator;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EvaluateBoardUseCaseImpl implements EvaluateBoardUseCase {

  private final BoardEvaluator evaluator;

  @Override
  public GameResult execute(Board board) {
    return evaluator.evaluate(board);
  }
}
