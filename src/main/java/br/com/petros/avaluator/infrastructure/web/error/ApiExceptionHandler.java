package br.com.petros.avaluator.infrastructure.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.petros.avaluator.domain.exception.InvalidBoardException;
import br.com.petros.avaluator.generated.model.Error;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(InvalidBoardException.class)
	public ResponseEntity<Error> handleInvalidBoard(InvalidBoardException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new Error().message(ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<br.com.petros.avaluator.generated.model.Error> handleUnexpected(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new br.com.petros.avaluator.generated.model.Error().message("internal_error"));
	}

	@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentNotValidException.class })
	public ResponseEntity<Error> handleJsonIssues(Exception ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new Error().message("requisição inválida: " + ex.getMessage()));
	}
}
