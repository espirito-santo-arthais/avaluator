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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class WebDtoMapper {

	private static final Logger log = LoggerFactory.getLogger(WebDtoMapper.class);

	/** Converte o DTO da API (BoardDto) para o domínio (Board). */
	public Board toDomainBoard(EvaluateRequest body) {
		final String rid = MDC.get("reqId"); // opcional, se você populou via Filter
		BoardDto boardDto = body.getBoard();

		if (boardDto == null) {
			log.warn("rid={}, payload inválido: boardDto nulo", rid);
			throw new InvalidBoardException("O tabuleiro não pode ser nulo");
		}
		if (boardDto.getR1() == null) {
			log.warn("rid={}, payload inválido: r1 nulo", rid);
			throw new InvalidBoardException("A linha 1 do tabuleiro não pode ser nulo");
		}
		if (boardDto.getR2() == null) {
			log.warn("rid={}, payload inválido: r2 nulo", rid);
			throw new InvalidBoardException("A linha 2 do tabuleiro não pode ser nulo");
		}
		if (boardDto.getR3() == null) {
			log.warn("rid={}, payload inválido: r3 nulo", rid);
			throw new InvalidBoardException("A linha 3 do tabuleiro não pode ser nulo");
		}

		var cells = new CellMark[3][3];

		cells[0][0] = map(boardDto.getR1().getC1(), "r1.c1");
		cells[0][1] = map(boardDto.getR1().getC2(), "r1.c2");
		cells[0][2] = map(boardDto.getR1().getC3(), "r1.c3");

		cells[1][0] = map(boardDto.getR2().getC1(), "r2.c1");
		cells[1][1] = map(boardDto.getR2().getC2(), "r2.c2");
		cells[1][2] = map(boardDto.getR2().getC3(), "r2.c3");

		cells[2][0] = map(boardDto.getR3().getC1(), "r3.c1");
		cells[2][1] = map(boardDto.getR3().getC2(), "r3.c2");
		cells[2][2] = map(boardDto.getR3().getC3(), "r3.c3");

		return new Board(cells);
	}

	/** Converte o resultado do domínio para o DTO da API. */
	public EvaluateResponse toDto(GameResult result) {
		EvaluateResponse dto = new EvaluateResponse();
		dto.setStatus(map(result.status()));
		// winner é Optional/Option: se vazio, retornamos EMPTY (contrato exige
		// 'winner')
		dto.setWinner(result.winner().map(this::map).orElse(Mark.EMPTY));
		return dto;
	}

	/* ===== helpers de mapeamento ===== */

	// Loga em DEBUG quando a célula vem nula (normalização para EMPTY).
	private CellMark map(Mark mark, String cellName) {
		if (mark == null) {
			String rid = MDC.get("reqId");
			if (log.isDebugEnabled()) {
				log.debug("rid={}, marca nula em {} -> EMPTY", rid, cellName);
			}
			return CellMark.EMPTY;
		}
		return CellMark.valueOf(mark.name());
	}

	private Mark map(CellMark cellMark) {
		return cellMark == null ? Mark.EMPTY : Mark.valueOf(cellMark.name());
	}

	private br.com.petros.avaluator.generated.model.GameStatus map(GameStatus gameStatus) {
		return br.com.petros.avaluator.generated.model.GameStatus.valueOf(gameStatus.name());
	}
}
