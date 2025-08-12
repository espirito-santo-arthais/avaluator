package br.com.petros.avaluator.infrastructure.web.controller;

import br.com.petros.avaluator.application.EvaluateBoardUseCase;
import br.com.petros.avaluator.domain.model.Board;
import br.com.petros.avaluator.domain.model.GameResult;
import br.com.petros.avaluator.generated.api.ApiApi;
import br.com.petros.avaluator.generated.model.EvaluateRequest;
import br.com.petros.avaluator.generated.model.EvaluateResponse;
import br.com.petros.avaluator.infrastructure.web.mapper.WebDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EvaluatorApiController implements ApiApi {

  private final EvaluateBoardUseCase useCase;
  private final WebDtoMapper mapper;

  public EvaluatorApiController(EvaluateBoardUseCase useCase, WebDtoMapper mapper) {
    this.useCase = useCase;
    this.mapper = mapper;
  }

  @Override
  public ResponseEntity<EvaluateResponse> evaluateBoard(EvaluateRequest body) {
    Board board = mapper.toDomainBoard(body);
    GameResult result = useCase.execute(board);
    return ResponseEntity.ok(mapper.toDto(result));
  }
}
