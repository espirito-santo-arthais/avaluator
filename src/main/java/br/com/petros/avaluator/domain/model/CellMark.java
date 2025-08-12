package br.com.petros.avaluator.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CellMark {
	X("X", "Marca do jogador X"),
	O("O", "Marca do jogador O"),
	EMPTY(" ", "Posição vazia no tabuleiro");

	private final String nome; // tradução ou símbolo exibido
	private final String descricao; // descrição da marca
}
