package br.com.petros.avaluator.infrastructure.web.filter;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

	public static final String HEADER = "X-Request-Id";
	public static final String MDC_KEY = "reqId";

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain)
			throws ServletException, IOException {

		String header = request.getHeader(HEADER);
		String rid;

		if (header == null || header.isBlank()) {
			rid = UUID.randomUUID().toString();
			if (log.isDebugEnabled()) {
				log.debug("Gerado novo reqId={}", rid);
			}
		} else {
			// valida o header como UUID; se inválido, gera um novo e avisa
			String trimmed = header.trim();
			try {
				UUID.fromString(trimmed);
				rid = trimmed;
				if (log.isDebugEnabled()) {
					log.debug("Usando X-Request-Id recebido reqId={}", rid);
				}
			} catch (IllegalArgumentException e) {
				rid = UUID.randomUUID().toString();
				log.warn("X-Request-Id inválido recebido: \"{}\" — gerando novo reqId={}", header, rid);
			}
		}

		MDC.put(MDC_KEY, rid);
		response.setHeader(HEADER, rid); // propaga para o cliente

		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(MDC_KEY); // sempre limpar
		}
	}
}
