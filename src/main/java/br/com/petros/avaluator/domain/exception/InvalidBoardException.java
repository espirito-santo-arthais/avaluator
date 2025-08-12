package br.com.petros.avaluator.domain.exception;

public class InvalidBoardException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public InvalidBoardException(String message) {
		super(message);
	}
}
