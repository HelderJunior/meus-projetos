package br.com.sebrae.sgm.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.PropriedadesUsuarioExterno;
import br.com.sebrae.sgm.model.RMColaborador;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.CodSituacaoColaborador;
import br.com.sebrae.sgm.model.enums.TipoUsuario;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.service.RMColaboradorService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.ApplicationContextUtils;
import br.com.sebrae.sgm.utils.CPFUtils;
import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.FacesUtil;
import br.com.sebrae.sgm.utils.PropertiesUtils;

@SessionScoped
@Named
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(LoginBean.class);

	private String cpf;

	private String senha;

	private UF uf;

	private String expirado;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private RMColaboradorService rmColaboradorService;

	private RMColaborador colaboradorSelecionado;

	private boolean possuiMaisUmaUF = false;

	private boolean possuiMaisUmaMatricula = false;

	public void inicializar() {
		if (!StringUtils.isBlank(expirado)) {
			FacesUtil.addErrorMessage("Sess\u00E3o expirada!");
		}
	}

	public void login() {
		try {
			if (validarLogin()) {
				Authentication auth = new UsernamePasswordAuthenticationToken(cpf, senha);
				AuthenticationManager authenticationManager = (AuthenticationManager) ApplicationContextUtils.getApplicationContext().getBean("authenticationManager");
				authenticationManager.authenticate(auth);
				SecurityContextHolder.getContext().setAuthentication(auth);

				Usuario u = usuarioService.loadByCpf(cpf);

				AppBean appBean = BeanProvider.getContextualReference(AppBean.class);

				// SE O USUARIO FOR GERENTE, CONFORME REGRA DE NEGOCIO ESPECIFICADO PELA SRTA ThATIANy SEBRAE UTIC EM 13/05/2015, TEMOS QUE SETAR POR DEFAULT
				// O PERFIL NO SPRING SECURITY PARA QUE O SISTEMA SAIBA QUE QUEM ESTA LOGADO DE FATO E UM GERENTE.
				for (Perfil p : u.getPerfisSistema()) {
					if (p.getChave().equals(Perfil.GERENTE.toString())) {
						appBean.setPerfilSelecionado(p);
						break;
					} else {
						appBean.setPerfilSelecionado(u.getPerfisSistema().get(0));
					}
				}

				if (uf == null) {
					List<UF> ufs = getUfsUsuario();
					if (ufs != null && !ufs.isEmpty()) {
						uf = ufs.get(0);
					}
				}

				if (colaboradorSelecionado == null) {
					List<RMColaborador> colaboradores = getColaboradoresUsuario();
					if (colaboradores != null && !colaboradores.isEmpty()) {
						colaboradorSelecionado = colaboradores.get(0);
					}
				}

				appBean.setColaboradorSelecionado(colaboradorSelecionado);
				appBean.setUfSelecionada(uf);
				appBean.setUsuarioAutenticado(u);
				u.setUltimoAcesso(new Date());

				if (u.getPropriedadesUsuarioExterno() == null) {
					RMColaborador colaborador = rmColaboradorService.loadByCpf(cpf);
					if (colaborador != null) {
						usuarioService.createUpdateFromRM(colaborador, null, new Date());
					}
				} else {// se for usuario externo apenas atualiza a data
					usuarioService.save(u);
				}

				FacesUtil.getExternalContext().redirect(FacesUtil.getExternalContext().getRequestContextPath());
			}
		} catch (ValidateException ve) {

			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			if (e instanceof GenericJDBCException) {
				FacesUtil
						.addErrorMessage("O servidor perdeu a conex\u00E3o com o banco de dados, entre em contato com o administrador.");
			} else {
				FacesUtil.addErrorMessage(e.getMessage());//"Erro ao efetuar login, tente novamente mais tarde.");
			}
		}

	}

	private boolean validarLogin() throws ValidateException {
		boolean sucesso = true;
		if (StringUtils.isBlank(this.cpf)) {
			FacesUtil.addErrorMessage("frmLogin:cpf", "Informe o CPF");
			sucesso = false;
		} else {
			if (!CPFUtils.validate(this.cpf)) {
				FacesUtil.addErrorMessage("frmLogin:cpf", "CPF inv\u00E1lido");
				sucesso = false;
			}
		}
		if (StringUtils.isBlank(this.senha)) {
			FacesUtil.addErrorMessage("frmLogin:senha", "Informe a senha");
			sucesso = false;
		}

		if (isPossuiMaisDeUmaUf() && uf == null) {
			FacesUtil.addErrorMessage("frmLogin:uf", "Informe a UF");
			sucesso = false;
		}

		if (isPossuiMaisUmaMatricula() && colaboradorSelecionado == null) {
			FacesUtil.addErrorMessage("frmLogin:matricula", "Informe a Matr\u00EDcula");
			sucesso = false;
		}
		return sucesso;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public void alterouCpf() {
		List<UF> ufs = getUfsUsuario();
		if (ufs != null && ufs.size() > 1) {
			possuiMaisUmaUF = true;
			this.uf = ufs.get(0);
		} else {
			possuiMaisUmaUF = false;
		}

		List<RMColaborador> colaboradores = getColaboradoresUsuario();
		if (colaboradores != null && colaboradores.size() > 1) {
			possuiMaisUmaMatricula = true;
			this.colaboradorSelecionado = colaboradores.get(0);
		} else {
			possuiMaisUmaMatricula = false;
		}
	}

	public boolean isPossuiMaisDeUmaUf() {
		return possuiMaisUmaUF;
	}

	public List<UF> getUfsUsuario() {
		List<UF> retorno = new ArrayList<UF>();
		try {
			Usuario usuario = usuarioService.loadByCpf(this.cpf);
			if (usuario != null) {
				if (usuario.getTipo() == TipoUsuario.I) {
					List<Unidade> unidadesUsuario = usuario.getUnidades();
					if (unidadesUsuario != null && !unidadesUsuario.isEmpty()) {
						for (Unidade un : unidadesUsuario) {
							if (!retorno.contains(un.getUnidadePK().getUf()))
								retorno.add(un.getUnidadePK().getUf());
						}
					}

					// ufs do cara no rm
					List<UF> ufsRM = rmColaboradorService.getUfsColaborador(this.cpf);
					if (ufsRM != null && !ufsRM.isEmpty()) {
						for (UF uf : ufsRM) {
							if (!retorno.contains(uf))
								retorno.add(uf);
						}
					}
				} else if (usuario.getTipo() == TipoUsuario.E) {
					Set<UF> ufsExterno = new HashSet<UF>();
					List<PropriedadesUsuarioExterno> propriedadesUsuarioExternos = usuario
							.getPropriedadesUsuarioExterno();
					for (PropriedadesUsuarioExterno pue : propriedadesUsuarioExternos) {
						ufsExterno.addAll(pue.getUfs());
					}
					retorno = new ArrayList<UF>(ufsExterno);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao obter UF's do usuario");
		}
		return retorno;
	}

	public List<RMColaborador> getColaboradoresUsuario() {
		List<RMColaborador> retorno = new ArrayList<RMColaborador>();
		try {
			retorno = rmColaboradorService.getColaboradoresAtivosCPF(this.cpf);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao obter UF's do usuario");
		}
		return retorno;
	}

	public void primeiroAcesso() {
		try {
			validarPrimeiroAcesso();
			RMColaborador rmColaborador = rmColaboradorService.loadByCpf(cpf);
			rmColaboradorService.reenviarSenha(this.cpf);
			FacesUtil.addInfoMessage("Nova senha enviada para o email: " + rmColaborador.getColaboradorEmail());
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			if (e instanceof GenericJDBCException) {
				FacesUtil
						.addErrorMessage("O servidor perdeu a conex\u00E3o com o banco de dados, entre em contato com o administrador.");
			} else {
				FacesUtil.addErrorMessage("Erro ao efetuar login, tente novamente mais tarde.");
			}
		}
	}

	private void validarPrimeiroAcesso() throws ValidateException {
		List<String> erros = new ArrayList<String>();

		if (StringUtils.isBlank(this.cpf)) {
			erros.add(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty("preencher_cpf_primeiro_acesso"));
		} else {
			if (!CPFUtils.validate(this.cpf)) {
				erros.add("CPF inv\u00E1lido");
			}
			try {
				RMColaborador rmColaborador = rmColaboradorService.loadByCpf(cpf);
				if (rmColaborador == null) {
					erros.add(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
							"msg_usuario_nao_encontrado_cpf"));
				} else {
					if (rmColaborador.getColaboradorCodSituacao() == CodSituacaoColaborador.D) {
						erros.add(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty("usuario_inativo"));
					}
					if (Arrays.asList(
							new CodSituacaoColaborador[] { CodSituacaoColaborador.F, CodSituacaoColaborador.P,
									CodSituacaoColaborador.E, CodSituacaoColaborador.L, CodSituacaoColaborador.T,
									CodSituacaoColaborador.I, CodSituacaoColaborador.W }).contains(
							rmColaborador.getColaboradorCodSituacao())) {
						erros.add(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty("usuario_afastado"));
					}
					Usuario u = usuarioService.loadByCpf(this.cpf);
					if (u != null && u.getUltimoAcesso() != null) {
						erros.add(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
								"msg_nao_e_primeiro_acesso"));
					}
				}
			} catch (Exception e) {
				erros.add(e.getMessage());
			}
		}
		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	public String recuperarSenha() {
		try {
			validarRecuperarSenha();
			Usuario u = usuarioService.loadByCpf(this.cpf);
			usuarioService.reenviarSenha(this.cpf);
			FacesUtil.addInfoMessage("Nova senha enviada para o email: " + u.getEmail());
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (e instanceof GenericJDBCException) {
				FacesUtil
						.addErrorMessage("O servidor perdeu a conex\u00E3o com o banco de dados, entre em contato com o administrador.");
			} else {
				FacesUtil.addErrorMessage("Erro ao efetuar login, tente novamente mais tarde.");
			}
		}
		return "";
	}

	private void validarRecuperarSenha() throws ValidateException {
		List<String> erros = new ArrayList<String>();

		if (StringUtils.isBlank(this.cpf)) {
			erros.add(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty("preencher_cpf_primeiro_acesso"));
		} else {
			if (!CPFUtils.validate(this.cpf)) {
				erros.add("CPF inv\u00E1lido");
			}
			try {
				Usuario u = usuarioService.loadByCpf(this.cpf);
				if (u == null) {
					erros.add(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
							"msg_usuario_nao_encontrado_cpf"));
				}
			} catch (Exception e) {
				erros.add(e.getMessage());
			}
		}
		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	public String getExpirado() {
		return expirado;
	}

	public void setExpirado(String expirado) {
		this.expirado = expirado;
	}

	public RMColaborador getColaboradorSelecionado() {
		return colaboradorSelecionado;
	}

	public void setColaboradorSelecionado(RMColaborador colaboradorSelecionado) {
		this.colaboradorSelecionado = colaboradorSelecionado;
	}

	public boolean isPossuiMaisUmaMatricula() {
		return possuiMaisUmaMatricula;
	}

	public void setPossuiMaisUmaMatricula(boolean possuiMaisUmaMatricula) {
		this.possuiMaisUmaMatricula = possuiMaisUmaMatricula;
	}

}
