package br.com.petros.avaluator.infrastructure.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CorrelationIdFilterTest {

	private final CorrelationIdFilter filter = new CorrelationIdFilter();

	@AfterEach
	void cleanup() {
		MDC.clear();
	}

	@Test
	void deve_gerar_uuid_quando_header_ausente_e_propagar_no_response_e_no_mdc() throws Exception {
		var request = new MockHttpServletRequest();
		var response = new MockHttpServletResponse();

		final String[] ridDuranteChain = new String[1];
		FilterChain chain = (var r, var s) -> ridDuranteChain[0] = MDC.get(CorrelationIdFilter.MDC_KEY);

		filter.doFilter(request, response, chain);

		var header = response.getHeader(CorrelationIdFilter.HEADER);
		assertAll(
				() -> assertNotNull(header, "Response deve conter X-Request-Id"),
				() -> assertDoesNotThrow(() -> UUID.fromString(header), "Deve ser UUID válido"),
				() -> assertEquals(header, ridDuranteChain[0], "MDC durante o chain deve bater com o header"),
				() -> assertNull(MDC.get(CorrelationIdFilter.MDC_KEY), "MDC deve ser limpo após o filtro"));
	}

	@Test
	void deve_reutilizar_header_quando_uuid_valido() throws Exception {
		var incoming = UUID.randomUUID().toString();
		var request = new MockHttpServletRequest();
		request.addHeader(CorrelationIdFilter.HEADER, incoming);
		var response = new MockHttpServletResponse();

		final String[] ridDuranteChain = new String[1];
		FilterChain chain = (var r, var s) -> ridDuranteChain[0] = MDC.get(CorrelationIdFilter.MDC_KEY);

		filter.doFilter(request, response, chain);

		assertAll(
				() -> assertEquals(incoming, response.getHeader(CorrelationIdFilter.HEADER)),
				() -> assertEquals(incoming, ridDuranteChain[0]),
				() -> assertNull(MDC.get(CorrelationIdFilter.MDC_KEY)));
	}

	@Test
	void deve_gerar_novo_quando_header_invalido() throws Exception {
		var request = new MockHttpServletRequest();
		request.addHeader(CorrelationIdFilter.HEADER, "nao-e-uuid");
		var response = new MockHttpServletResponse();

		FilterChain chain = (ServletRequest r, ServletResponse s) -> { /* no-op */ };
		filter.doFilter(request, response, chain);

		var header = response.getHeader(CorrelationIdFilter.HEADER);
		assertAll(
				() -> assertNotNull(header),
				() -> assertNotEquals("nao-e-uuid", header),
				() -> assertDoesNotThrow(() -> UUID.fromString(header)),
				() -> assertNull(MDC.get(CorrelationIdFilter.MDC_KEY)));
	}
}
