package br.com.petros.avaluator.domain.model;

import java.util.Optional;

public record GameResult(GameStatus status, Optional<CellMark> winner) {
}
