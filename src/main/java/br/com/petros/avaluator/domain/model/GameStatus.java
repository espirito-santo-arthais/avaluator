package br.com.petros.avaluator.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameStatus {
	X_WINS("X_WINS", "Vitória do jogador X"),
	O_WINS("O_WINS", "Vitória do jogador O"),
	DRAW("DRAW", "Empate (velha)"),
	ONGOING("ONGOING", "Jogo em andamento"),
	INVALID("INVALID", "Estado inválido do tabuleiro");

	private final String nome; // código usado na API
	private final String descricao; // descrição em português
}
