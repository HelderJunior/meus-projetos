package br.com.sebrae.sgm.security;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import br.com.sebrae.sgm.model.PropriedadesUsuarioExterno;
import br.com.sebrae.sgm.model.RMColaborador;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.CodSituacaoColaborador;
import br.com.sebrae.sgm.model.enums.TipoUsuario;
import br.com.sebrae.sgm.service.RMColaboradorService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.DateUtils;
import br.com.sebrae.sgm.utils.PropertiesUtils;
import br.com.sebrae.sgm.utils.SenhaUtils;

public class SgmAuthenticationProvider implements AuthenticationProvider {

	private static Logger log = LoggerFactory.getLogger(SgmAuthenticationProvider.class);

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();

		UsuarioService userService = BeanProvider.getContextualReference(UsuarioService.class);

		RMColaboradorService rmColaboardorService = BeanProvider.getContextualReference(RMColaboradorService.class);

		Usuario user = null;

		RMColaborador rmColaborador = null;

		try {
			user = userService.loadByCpf(username);
		} catch (Exception e) {
			log.error("Erro ao efetuar login", e);
			throw new BadCredentialsException("Erro ao efetuar login: " + e.getMessage());
		}

		try {
			rmColaborador = rmColaboardorService.loadByCpf(username);
		} catch (Exception e) {
			log.error("Erro ao efetuar login", e);
			throw new BadCredentialsException("Erro ao efetuar login: " + e.getMessage());
		}

		if (user == null && rmColaborador == null) {
			throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
					"msg_usuario_nao_encontrado"));
		} else if ((rmColaborador != null && user == null)
				|| (user.getUltimoAcesso() == null && user.getTipo() == TipoUsuario.I)) { // primeiro acesso

			if (rmColaborador.getColaboradorCodSituacao() == CodSituacaoColaborador.D) {
				throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
						"usuario_inativo"));
			}

			if (Arrays.asList(
					new CodSituacaoColaborador[] { CodSituacaoColaborador.F, CodSituacaoColaborador.P,
							CodSituacaoColaborador.E, CodSituacaoColaborador.L, CodSituacaoColaborador.T,
							CodSituacaoColaborador.I, CodSituacaoColaborador.W }).contains(
					rmColaborador.getColaboradorCodSituacao())) {
				throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
						"usuario_afastado"));
			}

			throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
					"msg_primeiro_acesso"));

		} else if (user != null && rmColaborador == null) { // usuario externo
			if (!user.getAtivo()) {
				throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
						"usuario_inativo"));
			}
			if (user.getPropriedadesUsuarioExterno() != null) {

				List<PropriedadesUsuarioExterno> propriedadesExterno = user.getPropriedadesUsuarioExterno();
				boolean possuiPermissaoPeriodo = false;
				for (PropriedadesUsuarioExterno pue : propriedadesExterno) {
					if (DateUtils.isBetween(new Date(), pue.getDtInicio(), pue.getDtFim())) {
						possuiPermissaoPeriodo = true;
					}
				}
				if (!possuiPermissaoPeriodo) {
					throw new BadCredentialsException(
							"Voc\u00EA n\u00E3o possui nenhum acesso habilitado para o per\u00EDodo atual.");
				}
			} else {
				throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
						"msg_usuario_nao_encontrado"));
			}
		} else if (user != null && rmColaborador != null) {// colaborador

			if (rmColaborador.getColaboradorCodSituacao() == CodSituacaoColaborador.D) {
				throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
						"usuario_inativo"));
			}

			if (Arrays.asList(
					new CodSituacaoColaborador[] { CodSituacaoColaborador.F, CodSituacaoColaborador.P,
							CodSituacaoColaborador.E, CodSituacaoColaborador.L, CodSituacaoColaborador.T,
							CodSituacaoColaborador.I, CodSituacaoColaborador.W }).contains(
					rmColaborador.getColaboradorCodSituacao())) {
				throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
						"usuario_afastado"));
			}

			if (!user.getAtivo()) {
				throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
						"usuario_inativo"));
			}
			if (rmColaborador.isAfastadoLicensa()) {
				throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
						"usuario_afastado"));
			}
		}

		try {
			if (!SenhaUtils.encrypt(password).equals(user.getPassword())) {
				throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
						"msg_senha_invalida"));
			}
		} catch (UnsupportedEncodingException e) {
			log.error("Erro ao encriptar senha", e);
			throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
					"msg_senha_invalida"));
		} catch (NoSuchAlgorithmException e) {
			log.error("Erro ao encriptar senha", e);
			throw new BadCredentialsException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
					"msg_senha_invalida"));
		}

		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
		return new UsernamePasswordAuthenticationToken(user, password, authorities);
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

}
