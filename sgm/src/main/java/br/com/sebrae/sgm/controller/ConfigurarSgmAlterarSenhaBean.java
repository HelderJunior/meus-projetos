package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.TipoUsuario;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.service.PerfilService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;
import br.com.sebrae.sgm.utils.SenhaUtils;

@ConversationScoped
@Named
public class ConfigurarSgmAlterarSenhaBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ConfigurarSgmAlterarSenhaBean.class);

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private PerfilService perfilService;

	private String cpf;

	private List<Perfil> perfis;
	private Perfil perfil;

	private UF uf;
	private Unidade unidade;
	private List<Unidade> unidades;

	private Usuario usuarioPesquisa;

	private Usuario usuario;
	private List<Usuario> usuarios;

	@Email(message = "Email inv\u00E1lido")
	private String novoEmail;

	@Email(message = "Email inv\u00E1lido")
	private String novoEmailConfirmacao;

	private String novaSenha;
	private String novaSenhaConfirmacao;
	private boolean desbilitarUnidades = true;
	private boolean desabilitarNomeUsuario = true;

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public List<Perfil> getPerfis() {
		if (perfis == null || perfis.isEmpty()) {
			perfis = perfilService.bindByChaves(Arrays.asList(Perfil.PERFIS_CHEFIA_COLABORADOR));
		}
		return perfis;
	}

	public void setPerfis(List<Perfil> perfis) {
		this.perfis = perfis;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public List<Unidade> getUnidades() {
		if (unidades == null || unidades.isEmpty()) {
			unidades = unidadeService.findAll();
		}
		return unidades;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Usuario> getUsuarios() {
		if (usuarios == null || usuarios.isEmpty()) {
			this.usuarios = usuarioService.findByParameters(this.cpf, this.uf, this.perfil, this.unidade,
					this.usuarioPesquisa, TipoUsuario.I);
		}
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public String getNovoEmail() {
		return novoEmail;
	}

	public void setNovoEmail(String novoEmail) {
		this.novoEmail = novoEmail;
	}

	public String getNovoEmailConfirmacao() {
		return novoEmailConfirmacao;
	}

	public void setNovoEmailConfirmacao(String novoEmailConfirmacao) {
		this.novoEmailConfirmacao = novoEmailConfirmacao;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getNovaSenhaConfirmacao() {
		return novaSenhaConfirmacao;
	}

	public void setNovaSenhaConfirmacao(String novaSenhaConfirmacao) {
		this.novaSenhaConfirmacao = novaSenhaConfirmacao;
	}

	public Usuario getUsuarioPesquisa() {
		return usuarioPesquisa;
	}

	public void setUsuarioPesquisa(Usuario usuarioPesquisa) {
		this.usuarioPesquisa = usuarioPesquisa;
	}

	public boolean isDesbilitarUnidades() {
		return desbilitarUnidades;
	}

	public boolean isDesabilitarNomeUsuario() {
		return desabilitarNomeUsuario;
	}

	public void setDesabilitarNomeUsuario(boolean desabilitarNomeUsuario) {
		this.desabilitarNomeUsuario = desabilitarNomeUsuario;
	}

	public void setDesbilitarUnidades(boolean desbilitarUnidades) {
		this.desbilitarUnidades = desbilitarUnidades;
	}

	public List<Usuario> completeUsuariosInterno(String nome) {
		if (unidade != null) {
			List<Usuario> usuarioInterno = usuarioService.findAllUsuarioPorUnidade(nome, unidade);
			return usuarioInterno;
		} else {
			return new ArrayList<Usuario>();
		}
	}


	public void prepararEdicaoUsuario() {
		this.novaSenha = null;
		this.novaSenhaConfirmacao = null;
		this.novoEmail = null;
		this.novoEmailConfirmacao = null;
	}

	public void buscarUsuarios() {
		try {
			this.usuarios = usuarioService.findByParameters(this.cpf, this.uf, this.perfil, this.unidade,
					this.usuarioPesquisa, TipoUsuario.I);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_alterar_senha","Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void salvar() {
		try {
			if (!StringUtils.isBlank(this.novoEmail)) {
				if (!this.novoEmail.equals(this.novoEmailConfirmacao)) {
					FacesUtil.addErrorMessage("formEditarPerfil:confirmarEmail",
							"Confirma\u00E7\u00E3o de email inv\u00E1lido.");
					return;
				} else {
					this.usuario.setEmail(this.novoEmail);
				}
			}

			if (!StringUtils.isBlank(this.novaSenha)) {
				if (!SenhaUtils.validate(this.novaSenha)) {
					FacesUtil.addErrorMessage("formEditarPerfil:senha", SenhaUtils.regra);
					return;
				}
				this.usuario.setSenha(SenhaUtils.encrypt(this.novaSenha));
			}
			this.usuarioService.save(this.usuario);
			FacesUtil.addInfoMessage("form_alterar_senha","Informa\u00E7\u00F5es salvas com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_alterar_senha","Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}

	}


	public List<Unidade> completeUnidade(String query) {
		if(this.uf == null) {
			FacesUtil.addErrorMessage("Informe a UF para listar as Unidades");
			return Collections.EMPTY_LIST;
		}

        List<Unidade> retorno = unidadeService.findByDescricaoOrCodigoAndUF(query, this.uf);
        return retorno;
    }

	public void alterouUF() {
		this.unidade = null;
		if (this.uf != null) {
			desbilitarUnidades = false;
		} else {
			desbilitarUnidades = true;
		}
	}

	public void alterouUnidade() {
		if (this.unidade != null) {
			desabilitarNomeUsuario = false;
		} else {
			desabilitarNomeUsuario = true;
		}
	}

}
