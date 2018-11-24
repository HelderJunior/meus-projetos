package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ExcecaoResponsabilidades;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.UsuarioPerfil;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoAcao;
import br.com.sebrae.sgm.model.enums.TipoExcecao;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.model.enums.TipoUsuario;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioPerfilService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class CicloConfigurarExcecaoMetasMonitoramentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloConfigurarExcecaoMetasMonitoramentoBean.class);

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private UsuarioService usuarioService;
	
	@Inject
	private UsuarioPerfilService usuarioPerfilService;
	
	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;

	private ExcecaoResponsabilidades excecao = new ExcecaoResponsabilidades();

	private List<Unidade> unidades;

	private Boolean visualizando = Boolean.FALSE;

	/** Usuario **/
	private String nomeBusca;
	private boolean editandoUsuario = false;
	private List<Usuario> usuariosDisponiveis;
	private List<Usuario> usuariosSelecionadasVincular;
	private Usuario usuarioSelecionado;
	
	private List<Usuario> usuarioResponsaveis;
	
	private Usuario responsavelAtual;

	public void inserirParametro() {
		try {

			if (this.getExcecao().getUsuarios() == null || this.getExcecao().getUsuarios().isEmpty()
					&& this.getExcecao().getTipoMeta() == TipoMeta.I) {
				FacesUtil.addErrorMessage("form-metas-monitoramento:tblUsuariosSelecionados",
						"Informe no m\u00EDnimo um Usu\u00E1rio.");
				return;
			}

			if (this.cicloConfiguracao.getExcecoesResponsabilidades() == null) {
				this.cicloConfiguracao.setExcecoesResponsabilidades(new ArrayList<ExcecaoResponsabilidades>());
			}

			this.excecao.setTipoExcecao(TipoExcecao.IMM);
			this.cicloConfiguracao.getExcecoesResponsabilidades().add(this.excecao);

			this.excecao.setCicloConfiguracao(this.cicloConfiguracao);
			verificaStatus();
			this.cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.excecao = new ExcecaoResponsabilidades();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void verificaStatus() {
		if (this.cicloConfiguracao.getExcecoesResponsabilidades() != null
				&& !this.cicloConfiguracao.getExcecoesResponsabilidades().isEmpty()) {
			this.cicloConfiguracao.setStatusConfiguracaoExcecaoResponsabilidades(StatusConfiguracao.C);
		} else {
			this.cicloConfiguracao.setStatusConfiguracaoExcecaoResponsabilidades(StatusConfiguracao.A);
		}
		
		try {
			cicloConfiguracaoService.save(cicloConfiguracao);
			cicloService.save(cicloConfiguracao.getCiclo());
		} catch (Exception e) {
			// do nothing
		}
	}

	public void excluir() {
		try {
			this.cicloConfiguracao.getExcecoesResponsabilidades().remove(this.excecao);
			verificaStatus();
			cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.excecao = new ExcecaoResponsabilidades();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterarParametro() {
		try {
			if (this.getExcecao().getUsuarios() == null || this.getExcecao().getUsuarios().isEmpty()
					&& this.getExcecao().getTipoMeta() == TipoMeta.I) {
				FacesUtil.addErrorMessage("form-metas-monitoramento:tblUsuariosSelecionados",
						"Informe no m\u00EDnimo um Usu\u00E1rio.");
				return;
			}

			this.cicloConfiguracaoService.save(cicloConfiguracao);
			this.excecao = new ExcecaoResponsabilidades();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void editar(ExcecaoResponsabilidades cfg) {
		try {
			this.excecao = cfg;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * Usuarios
	 */
	public void prepararModalVincularUsuarios() {
		try {
			if (this.excecao.getUnidade() != null) {
				this.usuariosDisponiveis = usuarioService.findByUFAndTipo(this.cicloConfiguracao.getCiclo().getUf(), TipoUsuario.I);
				usuariosSelecionadasVincular = new ArrayList<Usuario>();
				this.nomeBusca = "";
				editandoUsuario = false;
				if (getExcecao().getUsuarios() != null) {
					usuariosDisponiveis.removeAll(getExcecao().getUsuarios());
				}
			} else {
				FacesUtil
						.addErrorMessage("formUsuarioMonitoramento", "Informe a unidade para busca dos usu\u00E1rios.");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void buscarUsuariosVincular() {
		try {
			this.usuariosDisponiveis = usuarioService.findByNameLikeAndTipoUsuarioAndUF(this.nomeBusca,
					TipoUsuario.I, this.cicloConfiguracao.getCiclo().getUf());

			if (getExcecao().getUsuarios() != null) {
				usuariosDisponiveis.removeAll(getExcecao().getUsuarios());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("formUsuarioMonitoramento",
					"Erro ao buscar Usuarios, tente novamente mais tarde.");
		}
	}

	public void vincularUsuarios() {
		try {
			if (editandoUsuario) {
				this.getExcecao().setUsuarios(this.usuariosSelecionadasVincular);
			} else {
				if (this.getExcecao().getUsuarios() == null) {
					this.getExcecao().setUsuarios(new ArrayList<Usuario>());
				}

				List<Usuario> usuariosVinculadosTemp = this.getExcecao().getUsuarios();
				usuariosVinculadosTemp.addAll(this.usuariosSelecionadasVincular);

				Collections.sort(usuariosVinculadosTemp, new Comparator<Usuario>() {
					@Override
					public int compare(Usuario o1, Usuario o2) {
						return o1.getNome().compareTo(o2.getNome());
					}
				});
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void desvincularUsuario() {
		try {
			this.getExcecao().getUsuarios().remove(this.usuarioSelecionado);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararModalVincularUsuarioEditar() {
		try {
			usuariosDisponiveis = usuarioService.findByUFAndTipo(this.cicloConfiguracao.getCiclo().getUf(), TipoUsuario.I);
			usuariosSelecionadasVincular = getExcecao().getUsuarios();
			this.nomeBusca = "";
			editandoUsuario = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public String getNomeBusca() {
		return nomeBusca;
	}

	public void setNomeBusca(String nomeBusca) {
		this.nomeBusca = nomeBusca;
	}

	public boolean isEditandoUsuario() {
		return editandoUsuario;
	}

	public void setEditandoUsuario(boolean editandoUsuario) {
		this.editandoUsuario = editandoUsuario;
	}

	public List<Usuario> getUsuariosDisponiveis() {
		return usuariosDisponiveis;
	}

	public void setUsuariosDisponiveis(List<Usuario> usuariosDisponiveis) {
		this.usuariosDisponiveis = usuariosDisponiveis;
	}

	public List<Usuario> getUsuariosSelecionadasVincular() {
		return usuariosSelecionadasVincular;
	}

	public void setUsuariosSelecionadasVincular(List<Usuario> usuariosSelecionadasVincular) {
		this.usuariosSelecionadasVincular = usuariosSelecionadasVincular;
	}

	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public Boolean getVisualizando() {
		return visualizando;
	}

	public void setVisualizando(Boolean visualizando) {
		this.visualizando = visualizando;
	}

	public List<TipoMeta> getTiposMeta() {
		return Arrays.asList(new TipoMeta[]{TipoMeta.I, TipoMeta.E});
	}

	public List<TipoAcao> getTiposAcao() {
		return Arrays.asList(new TipoAcao[] { TipoAcao.IMT, TipoAcao.IMM });
	}

	public ExcecaoResponsabilidades getExcecao() {
		return excecao;
	}

	public void setExcecao(ExcecaoResponsabilidades excecao) {
		this.excecao = excecao;
	}

	public List<Unidade> getUnidades() {
		if (unidades == null) {
			unidades = unidadeService.findAll();
		}
		return unidades;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public List<Usuario> getUsuariosResponsaveis() {
		/*if(this.usuarioResponsaveis == null || this.usuarioResponsaveis.isEmpty()){
			this.usuarioResponsaveis = usuarioService.findByUFAndTipo(this.cicloConfiguracao.getCiclo().getUf(), TipoUsuario.I);
		}*/
		if(this.excecao.getUnidade() != null) {
			this.usuarioResponsaveis = usuarioService.findByUnidadeAndTipo(this.excecao.getUnidade(), TipoUsuario.I);
		}
		return this.usuarioResponsaveis;
	}

	public void alterouTipoMeta() {
		this.excecao.setUnidade(null);
		if (this.excecao.getTipoMeta() == TipoMeta.E) {
			this.excecao.setUsuarios(new ArrayList<Usuario>());
		}
	}
	
	public List<Unidade> completeUnidade(String query) {
        List<Unidade> retorno = unidadeService.findByDescricaoOrCodigoAndUF(query, this.cicloConfiguracao.getCiclo().getUf());
        return retorno;
    }
	
	public Usuario getResponsavelAtual() {
		if(this.excecao.getUnidade() != null){
			List<UsuarioPerfil> usuariosPerfil = usuarioPerfilService.findByTipoPerfilAndUnidade(Perfil.GERENTE, this.excecao.getUnidade());
			if(usuariosPerfil != null && !usuariosPerfil.isEmpty()){
				this.responsavelAtual = usuariosPerfil.get(0).getUsuario();
			}
		}
		return responsavelAtual;
	}

	public void setResponsavelAtual(Usuario responsavelAtual) {
		this.responsavelAtual = responsavelAtual;
	}
}
