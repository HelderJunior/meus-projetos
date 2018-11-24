package br.com.sebrae.sgm.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class LogoutHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		String paramSessaoExpirada = request.getParameter("expirado");

		if (!StringUtils.isBlank(paramSessaoExpirada)) {
			redirectStrategy.sendRedirect(request, response, "/auth/login.xhtml?expirado=true");
		} else {
			redirectStrategy.sendRedirect(request, response, "/auth/login.xhtml");
		}
	}

}
