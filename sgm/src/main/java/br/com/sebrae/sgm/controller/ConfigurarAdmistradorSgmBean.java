package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.UsuarioPerfil;
import br.com.sebrae.sgm.model.enums.TipoMetaAcessoAdministrador;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.service.PerfilService;
import br.com.sebrae.sgm.service.UsuarioPerfilService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ConfigurarAdmistradorSgmBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ConfigurarAdmistradorSgmBean.class);

	@NotNull(message = "Informe o tipo do Administrador")
	private Perfil tipoAdministrador;

	@NotNull(message = "Informe a UF")
	private UF uf;

	private List<Usuario> usuariosUf;

	@NotNull(message = "Informe o Colaborador")
	private Usuario usuario;

	@NotNull(message = "Informe o Tipo da Meta")
	private TipoMetaAcessoAdministrador tipoMeta;

	private UsuarioPerfil usuarioEdicao;

	private List<UsuarioPerfil> usuariosAdministradores = new ArrayList<UsuarioPerfil>();

	private boolean editandoAdministrador = false;

	private List<Perfil> tiposAdministrador;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private PerfilService perfilService;

	@Inject
	private UsuarioPerfilService usuarioPerfilService;

	public List<Perfil> getTiposAdministrador() {
		if (tiposAdministrador == null || tiposAdministrador.isEmpty()) {
			tiposAdministrador = perfilService.bindByChaves(Arrays.asList(new String[] { Perfil.ADM_MASTER,
					Perfil.ADM_UF_NACIONAL }));
		}
		return tiposAdministrador;
	}

	public List<UF> getUfs() {
		return Arrays.asList(UF.values());
	}

	public Perfil getTipoAdministrador() {
		return tipoAdministrador;
	}

	public void setTipoAdministrador(Perfil tipoAdministrador) {
		this.tipoAdministrador = tipoAdministrador;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public List<Usuario> getUsuariosUf() {
		try {
			if (uf != null) {
				usuariosUf = usuarioService.findByUF(uf);
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_administrador","Erro ao obter colaboradores");
			log.error(e.getMessage(), e);
		}
		return usuariosUf;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void inserirAdministrador() {
		List<UsuarioPerfil> config = new ArrayList<UsuarioPerfil>();
		try {
			if (usuario.getPerfis() != null) {
				Iterator<UsuarioPerfil> it = usuario.getPerfis().iterator();
				while (it.hasNext()) {
					UsuarioPerfil up = it.next();
					if ((up.getPerfil().getChave().equals(Perfil.ADM_MASTER) || up.getPerfil().getChave()
							.equals(Perfil.ADM_UF_NACIONAL))
							&& (up.getTipoMetaAcessoAdministrador() != null || up.getUf() != null)) {
						config.add(up);
					}
				}
			} else {
				usuario.setPerfis(new ArrayList<UsuarioPerfil>());
			}

			if(config.isEmpty() || config.size() == 0){
				UsuarioPerfil perfilAdmUsuario = new UsuarioPerfil();
				perfilAdmUsuario.setUsuario(usuario);
				perfilAdmUsuario.setPerfil(perfilService.load(tipoAdministrador.getChave()));
				perfilAdmUsuario.setTipoMetaAcessoAdministrador(this.tipoMeta);
				perfilAdmUsuario.setUf(this.uf);
				perfilAdmUsuario.setUnidade(usuario.getUnidade());
				usuario.getPerfis().add(perfilAdmUsuario);
				usuarioService.save(usuario);
				reset();
				FacesUtil.addInfoMessage("form_administrador","Altera\u00E7\u00E3o do administrador realizada sucesso.");
			}else{
				FacesUtil.addErrorMessage("form_administrador","O usu\u00E1rio j\u00E1 possui um perfil cadastrado.");
			}
			
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_administrador","Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void salvarAdministrador() {
		try {

			List<UsuarioPerfil> perfisExistentes = usuarioPerfilService.findByTipoPerfilUsuarioTipoAcessoUf(
					this.tipoAdministrador, this.usuario, this.tipoMeta, this.uf);
			
			perfisExistentes.addAll(usuarioPerfilService.findByTipoPerfilUsuarioTipoAcessoUf(this.tipoAdministrador,
					this.usuario, TipoMetaAcessoAdministrador.A, this.uf));

			if (perfisExistentes != null && !perfisExistentes.isEmpty() && perfisExistentes.size() > 1) {
				FacesUtil.addErrorMessage("form_administrador","Configura\u00E7\u00E3o j\u00E1 existente.");
				return;
			}

			usuarioEdicao.setPerfil(this.tipoAdministrador);
			usuarioEdicao.setTipoMetaAcessoAdministrador(this.tipoMeta);
			usuarioEdicao.setUf(this.uf);
			usuarioEdicao.setUsuario(this.usuario);
			this.usuarioPerfilService.salvar(this.usuarioEdicao);
			reset();
			FacesUtil.addInfoMessage("form_administrador","Altera\u00E7\u00E3o do administrador realizada sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_administrador","Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<UsuarioPerfil> getUsuariosAdministradores() {
		if (usuariosAdministradores == null || usuariosAdministradores.isEmpty()) {
			usuariosAdministradores = usuarioPerfilService.findAdministradores();
		}
		return usuariosAdministradores;
	}

	public void setUsuariosAdministradores(List<UsuarioPerfil> usuariosAdministradores) {
		this.usuariosAdministradores = usuariosAdministradores;
	}

	public void editar(UsuarioPerfil u) {
		try {
			this.tipoAdministrador = u.getPerfil();
			this.uf = u.getUf();
			this.usuariosUf = new ArrayList<Usuario>();
			this.usuariosUf.add(u.getUsuario());
			this.usuario = u.getUsuario();
			this.editandoAdministrador = true;
			this.tipoMeta = u.getTipoMetaAcessoAdministrador();
			this.usuarioEdicao = u;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_administrador","Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public boolean isEditandoAdministrador() {
		return editandoAdministrador;
	}

	public void setEditandoAdministrador(boolean editandoAdministrador) {
		this.editandoAdministrador = editandoAdministrador;
	}

	public void reset() {
		this.tipoAdministrador = null;
		this.uf = null;
		this.usuario = null;
		this.usuarioEdicao = null;
		this.usuariosUf = new ArrayList<Usuario>();
		this.editandoAdministrador = false;
		this.usuariosAdministradores = null;
		this.tipoMeta = null;
	}

	public void excluir() {
		try {
			Usuario u = this.usuarioEdicao.getUsuario();
			u.getPerfis().remove(this.usuarioEdicao);
			this.usuarioService.save(u);
			try {
				this.usuarioPerfilService.delete(this.usuarioEdicao);
			} catch (Exception e) {
				// do nothing
			}
			reset();
			FacesUtil.addInfoMessage("form_administrador","Exclus\u00E3o realizada com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_administrador","Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public UsuarioPerfil getUsuarioEdicao() {
		return usuarioEdicao;
	}

	public void setUsuarioEdicao(UsuarioPerfil usuarioEdicao) {
		this.usuarioEdicao = usuarioEdicao;
	}

	public TipoMetaAcessoAdministrador getTipoMeta() {
		return tipoMeta;
	}

	public void setTipoMeta(TipoMetaAcessoAdministrador tipoMeta) {
		this.tipoMeta = tipoMeta;
	}

	public List<TipoMetaAcessoAdministrador> getTiposMeta() {
		return Arrays.asList(TipoMetaAcessoAdministrador.values());
	}

}
