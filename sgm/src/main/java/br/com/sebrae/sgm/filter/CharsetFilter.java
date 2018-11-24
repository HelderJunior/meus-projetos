package br.com.sebrae.sgm.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Forcar UTF8 em tudo.
 * 
 * @author <a href="david.reis@infosolo.com.br">david.reis@infosolo.com.br</a>
 * @since 18/02/2014
 * 
 */
@WebFilter(urlPatterns = "/*")
public class CharsetFilter implements Filter {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
				request.setCharacterEncoding("UTF-8");
				response.setContentType("text/html; charset=UTF-8");
			}
		} catch (Exception e) {
			log.error("Erro no filtro de charset", e);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}