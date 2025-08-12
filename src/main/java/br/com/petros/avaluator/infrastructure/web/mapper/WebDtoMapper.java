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

import org.springframework.stereotype.Component;

@Component
public class WebDtoMapper {

  /** Converte o DTO da API (BoardDto) para o domínio (Board). */
  public Board toDomainBoard(EvaluateRequest body) {
    BoardDto b = body.getBoard();
    if (b == null) {
		throw new InvalidBoardException("O tabuleiro não pode ser nulo");
    }
    if (b.getR1() == null) {
		throw new InvalidBoardException("A linha 1 do tabuleiro não pode ser nulo");
    }
    if (b.getR2() == null) {
		throw new InvalidBoardException("A linha 2 do tabuleiro não pode ser nulo");
    }
    if (b.getR3() == null) {
		throw new InvalidBoardException("A linha 3 do tabuleiro não pode ser nulo");
    }
    
    var cells = new CellMark[3][3];

    cells[0][0] = map(b.getR1().getC1());
    cells[0][1] = map(b.getR1().getC2());
    cells[0][2] = map(b.getR1().getC3());

    cells[1][0] = map(b.getR2().getC1());
    cells[1][1] = map(b.getR2().getC2());
    cells[1][2] = map(b.getR2().getC3());

    cells[2][0] = map(b.getR3().getC1());
    cells[2][1] = map(b.getR3().getC2());
    cells[2][2] = map(b.getR3().getC3());

    return new Board(cells);
  }

  /** Converte o resultado do domínio para o DTO da API. */
  public EvaluateResponse toDto(GameResult result) {
    EvaluateResponse dto = new EvaluateResponse();
    dto.setStatus(map(result.status()));
    // winner é Optional<CellMark>: se vazio, retornamos EMPTY (contrato exige 'winner')
    dto.setWinner(result.winner().map(this::map).orElse(Mark.EMPTY));
    return dto;
  }

  /* ===== helpers de mapeamento ===== */

  private CellMark map(Mark m) {
    // por segurança, trate null como EMPTY (deve ser raro por causa do contrato)
    return m == null ? CellMark.EMPTY : CellMark.valueOf(m.name());
  }

  private Mark map(CellMark m) {
    return m == null ? Mark.EMPTY : Mark.valueOf(m.name());
  }

  private br.com.petros.avaluator.generated.model.GameStatus map(GameStatus s) {
    return br.com.petros.avaluator.generated.model.GameStatus.valueOf(s.name());
  }
}
