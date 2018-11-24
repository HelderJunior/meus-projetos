package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.UsuarioPerfil;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.service.PerfilService;
import br.com.sebrae.sgm.service.RMColaboradorService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioPerfilService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ConfigurarPerfilSgmBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ConfigurarPerfilSgmBean.class);

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private PerfilService perfilService;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private RMColaboradorService colaboradorService;

	@Inject
	private UsuarioPerfilService usuarioPerfilService;

	@Inject
	private AppBean appBean;

	@NotNull(message = "Informe o Tipo do Perfil do Avaliador")
	private Perfil tipoPerfilAvaliador;

	private List<Perfil> tiposPerfilAvaliador;

	private String nomeBusca;
	private List<Unidade> unidadesDisponiveis;
	@NotEmpty(message = "Selecione no minimo uma Unidade")
	private List<Unidade> unidadesVinculadas;
	private List<Unidade> unidadesSelecionadasVincular;
	private Boolean editandoUnidades;
	private Unidade unidadeSelecionada;

	private List<UsuarioPerfil> usuariosChefe;
	private UsuarioPerfil usuarioChefe;
	private List<UsuarioPerfil> usuariosChefeEditando;
	private List<Usuario> usuarios;
	
	private boolean editando = false;
	

	@NotNull(message = "Informe o Colaborador")
	private Usuario usuario;

	private UF uf;

	public List<Perfil> getTiposPerfilAvaliador() {
		if (tiposPerfilAvaliador == null || tiposPerfilAvaliador.isEmpty()) {
			tiposPerfilAvaliador = perfilService.bindByChaves(Arrays.asList(new String[] { Perfil.GERENTE,
					Perfil.GERENTE_ADJUNTO, Perfil.DIRETOR, Perfil.ASSESSOR, Perfil.CHEFE_GABINETE }));
		}
		return tiposPerfilAvaliador;
	}

	public void alterouUf() {
		this.unidadesVinculadas = new ArrayList<Unidade>();
		this.usuario = null;
	}

	public Perfil getTipoPerfilAvaliador() {
		return tipoPerfilAvaliador;
	}

	public void setTipoPerfilAvaliador(Perfil tipoPerfilAvaliador) {
		this.tipoPerfilAvaliador = tipoPerfilAvaliador;
	}

	public List<Unidade> getUnidadesDisponiveis() {
		this.unidadesDisponiveis = unidadeService.findByUf(this.uf);
		return unidadesDisponiveis;
	}

	public void setUnidadesDisponiveis(List<Unidade> unidadesDisponiveis) {
		this.unidadesDisponiveis = unidadesDisponiveis;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Unidade> getUnidadesVinculadas() {
		return unidadesVinculadas;
	}

	public void setUnidadesVinculadas(List<Unidade> unidadesVinculadas) {
		this.unidadesVinculadas = unidadesVinculadas;
	}

	public String getNomeBusca() {
		return nomeBusca;
	}

	public void setNomeBusca(String nomeBusca) {
		this.nomeBusca = nomeBusca;
	}

	public List<Unidade> getUnidadesSelecionadasVincular() {
		return unidadesSelecionadasVincular;
	}

	public void setUnidadesSelecionadasVincular(List<Unidade> unidadesSelecionadasVincular) {
		this.unidadesSelecionadasVincular = unidadesSelecionadasVincular;
	}

	public Boolean getEditandoUnidades() {
		return editandoUnidades;
	}

	public void setEditandoUnidades(Boolean editandoUnidades) {
		this.editandoUnidades = editandoUnidades;
	}

	public List<UsuarioPerfil> getUsuariosChefe() {
		if (usuariosChefe == null || usuariosChefe.isEmpty()) {
			usuariosChefe = usuarioPerfilService.findUnidadesChefe();
		}
		return usuariosChefe;
	}

	public void setUsuariosChefe(List<UsuarioPerfil> usuariosChefe) {
		this.usuariosChefe = usuariosChefe;
	}

	public UsuarioPerfil getUsuarioChefe() {
		return usuarioChefe;
	}

	public void setUsuarioChefe(UsuarioPerfil usuarioChefe) {
		this.usuarioChefe = usuarioChefe;
	}

	public Unidade getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	public List<UsuarioPerfil> getUsuariosChefeEditando() {
		return usuariosChefeEditando;
	}

	public void setUsuariosChefeEditando(List<UsuarioPerfil> usuariosChefeEditando) {
		this.usuariosChefeEditando = usuariosChefeEditando;
	}

	public List<Usuario> getUsuarios() {
		try {
			if (getUnidadesVinculadas() != null && !getUnidadesVinculadas().isEmpty()) {
				List<Unidade> unidadesMesmaDiretoria = unidadeService
						.getUnidadesMesmaDiretoria(this.unidadesVinculadas);
				usuarios = usuarioService.findByUnidades(unidadesMesmaDiretoria);
			} else {
				usuarios = new ArrayList<Usuario>();
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_configurar_perfil", "Erro ao obter colaboradores");
			log.error(e.getMessage(), e);
		}
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	/*
	 * Unidades
	 */

	public List<Unidade> procuraSeleciana() {
		try {
			nomeBusca = unidadeSelecionada.getDescricao();
			unidadesDisponiveis.clear();
			unidadesDisponiveis = unidadeService.findByDescricao1(nomeBusca);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return unidadesDisponiveis;
	}

	public void prepararModalVincularUnidades() {
		try {
			unidadesDisponiveis = unidadeService.findAll();
			unidadesSelecionadasVincular = new ArrayList<Unidade>();
			this.nomeBusca = "";
			editandoUnidades = false;
			if (getUnidadesVinculadas() != null) {
				unidadesDisponiveis.removeAll(getUnidadesVinculadas());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_configurar_perfil",
					"Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void buscarUnidadesVincular() {
		try {
			this.unidadesDisponiveis = unidadeService.findByDescricao(this.nomeBusca);
			if (getUnidadesVinculadas() != null) {
				unidadesDisponiveis.removeAll(getUnidadesVinculadas());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("formUnidadesVincular", "Erro ao buscar unidades, tente novamente mais tarde.");
		}
	}

	public void vincularUnidades() {
		try {
			if (editandoUnidades) {
				this.setUnidadesVinculadas(unidadesSelecionadasVincular);
			} else {
				if (this.getUnidadesVinculadas() == null) {
					this.unidadesVinculadas = new ArrayList<Unidade>();
				}
				List<Unidade> unidadesVinculadosTemp = this.unidadesVinculadas;
				unidadesVinculadosTemp.addAll(this.unidadesSelecionadasVincular);

				Collections.sort(unidadesVinculadosTemp, new Comparator<Unidade>() {
					@Override
					public int compare(Unidade o1, Unidade o2) {
						return o1.getDescricao().compareTo(o2.getDescricao());
					}
				});
			}
			// this.usuario = null;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_configurar_perfil", e.getMessage());
		}
	}

	public void desvincularUnidade() {
		try {
			this.unidadesVinculadas.remove(this.unidadeSelecionada);
			this.usuario = null;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_configurar_perfil",
					"Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararModalVincularUnidadesEditar() {
		try {
			unidadesDisponiveis = unidadeService.findAll();
			unidadesSelecionadasVincular = getUnidadesVinculadas();
			this.nomeBusca = "";
			editandoUnidades = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_configurar_perfil",
					"Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void inserirParametro() {
		try {
			for (Unidade un : this.unidadesVinculadas) {
				List<UsuarioPerfil> vinculosExistentes = usuarioPerfilService.findByTipoPerfilAndColaboradorUnidade(
						this.tipoPerfilAvaliador.getChave(), usuario, un);
				if (vinculosExistentes == null || vinculosExistentes.isEmpty()) {
					UsuarioPerfil uc = new UsuarioPerfil();
					uc.setPerfil(this.tipoPerfilAvaliador);
					uc.setUnidade(un);
					uc.setUf(this.uf);
					uc.setUsuario(usuario);
					usuarioPerfilService.insert(uc);
				}
			}
			reset();
			FacesUtil.addInfoMessage("form_configurar_perfil", "Perfil adicionado com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_configurar_perfil",
					"Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}
	
	public void excluirPerfilSelecionadas() {
		try {
			List<UsuarioPerfil> usuariosExcluir = Lists.newArrayList(Collections2.filter(this.usuariosChefe, new Predicate<UsuarioPerfil>() {
				@Override
				public boolean apply(UsuarioPerfil up) {
					return up.getSelecionado();
				}
			}));
			for (UsuarioPerfil up : usuariosExcluir) {
					Usuario user = usuarioService.loadWithPerfis(up.getUsuario().getId());
					user.getPerfis().remove(up);
					usuarioService.save(user);
					usuarioPerfilService.delete(up);
			} 
			if (usuariosExcluir != null && !usuariosExcluir.isEmpty()) {
				reset();
				FacesUtil.addInfoMessage("form_configurar_perfil", "Exclus\u00E3o feita com sucesso.");
				usuariosChefe = usuarioPerfilService.findUnidadesChefe();
			} else {
				FacesUtil.addErrorMessage("form_configurar_perfil", "E necessario selecionar ao menos um registro para exclus\u00E3o.");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluir() {
		try {
			Usuario user = usuarioService.loadWithPerfis(this.usuarioChefe.getUsuario().getId());
			user.getPerfis().remove(this.usuarioChefe);
			usuarioService.save(user);
			try {
				usuarioPerfilService.delete(this.usuarioChefe);
			} catch (Exception e) {
				// do nothing
			}
			reset();
			FacesUtil.addInfoMessage("form_configurar_perfil", "Exclus\u00E3o feita com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_configurar_perfil", "Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterarParametro() {
		try {
			for (UsuarioPerfil up : usuariosChefeEditando) {
				Usuario user = this.usuarioChefe.getUsuario();
				user.getPerfis().remove(up);
				usuarioService.save(user);
			}

			for (Unidade un : this.unidadesVinculadas) {
				List<UsuarioPerfil> vinculosExistentes = usuarioPerfilService.findByTipoPerfilAndColaboradorUnidade(
						this.tipoPerfilAvaliador.getChave(), usuario, un);
				if (vinculosExistentes == null || vinculosExistentes.isEmpty()) {
					UsuarioPerfil uc = new UsuarioPerfil();
					uc.setPerfil(this.tipoPerfilAvaliador);
					uc.setUnidade(un);
					uc.setUsuario(usuario);
					usuarioPerfilService.insert(uc);
				}
			}
			reset();
			FacesUtil.addInfoMessage("form_configurar_perfil", "Altera\u00E7\u00E3o feita com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_configurar_perfil",
					"Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void editar(UsuarioPerfil un) {
		try {
			this.usuarioChefe = un;
			this.tipoPerfilAvaliador = this.usuarioChefe.getPerfil();
			this.usuario = this.usuarioChefe.getUsuario();
			this.uf = this.usuarioChefe.getUf();
			this.unidadesVinculadas = unidadeService.findByTipoPerfilColaborador(this.tipoPerfilAvaliador.getChave(),
					this.usuarioChefe.getUsuario());
			this.usuariosChefeEditando = usuarioPerfilService.findByTipoPerfilAndColaborador(
					this.tipoPerfilAvaliador.getChave(), this.usuarioChefe.getUsuario());
			this.editando = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_configurar_perfil",
					"Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void reset() {
		this.tipoPerfilAvaliador = null;
		this.unidadesVinculadas = null;
		this.usuario = null;
		this.editando = false;
		this.usuariosChefe = null;
		this.uf = null;
	}
	
	public void selecionarTodos() {
		for (UsuarioPerfil up : usuariosChefe) {
			up.setSelecionado(true);
		}
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public List<UF> getUfs() {
		return Arrays.asList(UF.values());
	}

}
