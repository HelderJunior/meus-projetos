package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.PropriedadesUsuarioExterno;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.TipoUsuario;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.service.PerfilService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.CPFUtils;
import br.com.sebrae.sgm.utils.FacesUtil;
import br.com.sebrae.sgm.utils.SenhaUtils;

@ConversationScoped
@Named
public class ConfigurarSgmUsuarioExternoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ConfigurarSgmUsuarioExternoBean.class);

	@Inject
	private UsuarioService usuarioExternoService;

	@Inject
	private PerfilService perfilService;

	private Usuario usuarioExterno = new Usuario();
	private PropriedadesUsuarioExterno propriedadeUsuarioExterno = new PropriedadesUsuarioExterno();
	private int index;

	private List<Usuario> usuariosExterno;
	private List<Perfil> perfis;

	private Set<UF> ufsSelecioandas = new HashSet<UF>();

	private boolean editandoPropriedade = false;

	public Usuario getUsuarioExterno() {
		return usuarioExterno;
	}

	public void setUsuarioExterno(Usuario usuarioExterno) {
		this.usuarioExterno = usuarioExterno;
	}

	public List<Perfil> getPerfis() {
		if (perfis == null || perfis.isEmpty()) {
			perfis = perfilService.bindByChaves(Arrays.asList(Perfil.CONFIGURAR_PERFIS_USUARIO_EXTERNO));
		}
		return perfis;
	}

	public void setPerfis(List<Perfil> perfis) {
		this.perfis = perfis;
	}

	public List<Usuario> getUsuariosExterno() {
		if (usuariosExterno == null || usuariosExterno.isEmpty()) {
			usuariosExterno = usuarioExternoService.findAllExternos();
		}
		return usuariosExterno;
	}

	public void setUsuariosExterno(List<Usuario> usuariosExterno) {
		this.usuariosExterno = usuariosExterno;
	}

	public void excluir() {
		try {
			this.usuarioExternoService.delete(this.usuarioExterno);
			this.usuarioExterno = new Usuario();
			this.usuariosExterno = null;
			FacesUtil.addInfoMessage("form_usuario_externo", "Exclus\u00E3o realizada com sucesso");
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("form_usuario_externo",
							"O usu\u00E1rio n\u00E3o pode ser exclu\u00EDdo, pois est\u00E1 associado a algum registro no sistema.");
			log.error(e.getMessage(), e);
		}
	}

	public void editar(Usuario ue) {
		this.usuarioExterno = ue;
		this.usuarioExterno.setSenhaConfirmacao(null);
		this.usuarioExterno.setSenhaVo(null);
		this.usuarioExterno.setEmailConfirmacao(this.usuarioExterno.getEmail());
	}

	public void salvar() {
		try {
			if (!StringUtils.isEmpty(this.usuarioExterno.getSenhaVo())) {
				this.usuarioExterno.setSenha(SenhaUtils.encrypt(this.usuarioExterno.getSenhaVo()));
			}
			if (!usuarioExterno.getEmail().equals(usuarioExterno.getEmailConfirmacao())) {
				FacesUtil.addErrorMessage("form_usuario_externo:confirmaEmailExterno",
						"Email de confirma\u00E7\u00E3o n\u00E3o confere.");
				return;
			}
			if (usuarioExterno.getPropriedadesUsuarioExterno() == null
					|| usuarioExterno.getPropriedadesUsuarioExterno().isEmpty()) {
				FacesUtil.addErrorMessage("form_usuario_externo:tblPropriedadesUsuarioExterno",
						"Informe no m\u00EDnimo um acesso para o usu\u00E1rio.");
			}
			if (this.usuarioExterno.getId() == null) {
				boolean erro = false;

				if (!CPFUtils.validate(this.usuarioExterno.getCpf())) {
					FacesUtil.addErrorMessage("form_usuario_externo:cpfExterno", "CPF inv\u00E1lido.");
					erro = true;
				}

				Usuario ue = usuarioExternoService.loadByCpf(this.usuarioExterno.getCpf());
				if (ue != null) {
					FacesUtil.addErrorMessage("form_usuario_externo:cpfExterno", "CPF j\u00E1 cadastrado.");
					erro = true;
				}
				ue = usuarioExternoService.loadByEmail(this.usuarioExterno.getEmail());
				if (ue != null) {
					FacesUtil.addErrorMessage("form_usuario_externo:emailExterno", "Email j\u00E1 cadastrado.");
					erro = true;
				}

				if (!StringUtils.isBlank(usuarioExterno.getSenhaVo())) {
					if (!SenhaUtils.validate(usuarioExterno.getSenhaVo())) {
						FacesUtil.addErrorMessage("form_usuario_externo:senhaExterno", SenhaUtils.regra);
						erro = true;
					}

					if (!usuarioExterno.getSenhaVo().equals(usuarioExterno.getSenhaConfirmacao())) {
						FacesUtil.addErrorMessage("form_usuario_externo:senhaConfirmacao",
								"As senhas n\u00E3o conferem.");
						erro = true;
					}
					usuarioExterno.setSenhaVo(null);
				}

				if (erro) {
					return;
				}
			}

			this.usuarioExterno.setTipo(TipoUsuario.E);
			this.usuarioExterno.setAtivo(Boolean.TRUE);

			this.usuarioExternoService.save(this.usuarioExterno);
			this.usuarioExterno = new Usuario();
			this.usuariosExterno = null;
			this.ufsSelecioandas = new HashSet<UF>();
			FacesUtil.addInfoMessage("form_usuario_externo", "Informa\u00E7\u00F5es salvas com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_usuario_externo",
					"Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void validarDatas() {
		if (getPropriedadeUsuarioExterno().getDtInicio() != null && getPropriedadeUsuarioExterno().getDtFim() != null) {
			if (getPropriedadeUsuarioExterno().getDtInicio().compareTo(getPropriedadeUsuarioExterno().getDtFim()) > 0) {
				FacesUtil.addErrorMessage("formPropriedadesUsuario:dataFimExterno",
						"Data Fim deve ser maior ou igual a data de in\u00EDcio.");
				getPropriedadeUsuarioExterno().setDtFim(null);
			}
		}
	}

	public void alterouCpf() {
		try {
			Usuario u = this.usuarioExternoService.loadExternoByCpf(this.usuarioExterno.getCpf());
			if (u != null) {
				editar(u);
			}
		} catch (Exception e) {
			// do nothing
		}
	}

	public void adicionarPropriedades() {
		this.propriedadeUsuarioExterno = new PropriedadesUsuarioExterno();
		this.ufsSelecioandas = new HashSet<UF>();
		this.editandoPropriedade = false;
	}
	
	public void editarPropriedade(PropriedadesUsuarioExterno propriedade) {
		this.propriedadeUsuarioExterno = propriedade;
		this.ufsSelecioandas = new HashSet<UF>(propriedadeUsuarioExterno.getUfs());
		this.editandoPropriedade = true;
	}

	public void excluirPropriedade() {
		this.usuarioExterno.getPropriedadesUsuarioExterno().remove(this.index);
	}

	public void salvarPropriedadeUsuarioExterno() {
		this.propriedadeUsuarioExterno.setUsuario(this.usuarioExterno);
		if (!this.editandoPropriedade) {
			this.propriedadeUsuarioExterno.setUfs(this.ufsSelecioandas);
			if (this.usuarioExterno.getPropriedadesUsuarioExterno() == null) {
				this.usuarioExterno.setPropriedadesUsuarioExterno(new ArrayList<PropriedadesUsuarioExterno>());
			}
			this.usuarioExterno.getPropriedadesUsuarioExterno().add(this.propriedadeUsuarioExterno);
			this.propriedadeUsuarioExterno = new PropriedadesUsuarioExterno();
			this.ufsSelecioandas = new HashSet<UF>();
			this.editandoPropriedade = false;
		} else {
			this.propriedadeUsuarioExterno.setUfs(this.ufsSelecioandas);
			this.propriedadeUsuarioExterno = new PropriedadesUsuarioExterno();
			this.ufsSelecioandas = new HashSet<UF>();
			this.editandoPropriedade = false;
		}
	}

	public Set<UF> getUfsSelecioandas() {
		return ufsSelecioandas;
	}

	public void setUfsSelecioandas(Set<UF> ufsSelecioandas) {
		this.ufsSelecioandas = ufsSelecioandas;
	}

	public PropriedadesUsuarioExterno getPropriedadeUsuarioExterno() {
		return propriedadeUsuarioExterno;
	}

	public void setPropriedadeUsuarioExterno(PropriedadesUsuarioExterno propriedadeUsuarioExterno) {
		this.propriedadeUsuarioExterno = propriedadeUsuarioExterno;
	}

	public boolean isEditandoPropriedade() {
		return editandoPropriedade;
	}

	public void setEditandoPropriedade(boolean editandoPropriedade) {
		this.editandoPropriedade = editandoPropriedade;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
