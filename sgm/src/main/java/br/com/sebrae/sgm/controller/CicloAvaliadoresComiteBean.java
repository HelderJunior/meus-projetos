package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.AvaliadorCiclo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoAcaoAvaliador;
import br.com.sebrae.sgm.service.AvaliadorCicloService;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.PerfilService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;

@SessionScoped
@Named
public class CicloAvaliadoresComiteBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloAvaliadoresComiteBean.class);

	@Inject
	private PerfilService perfilService;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private AvaliadorCicloService avaliadorService;

	@Inject
	private AppBean appBean;
	
	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;

	private AvaliadorCiclo avaliador;

	private List<Usuario> usuariosAvaliadores;
	private List<Perfil> perfisAvaliadores;
	private Perfil perfilAvaliador;
	private Usuario usuarioAvaliador;
	private Boolean fasePactuacao = Boolean.FALSE;
	private Boolean faseRepactuacao = Boolean.FALSE;
	private Boolean faseAjustes = Boolean.FALSE;
	private Boolean faseValidacao = Boolean.FALSE;
	private Boolean faseTodas = Boolean.FALSE;
	private TipoAcaoAvaliador tipoAcao;

	private List<Unidade> unidadesDisponiveis;
	private List<Unidade> unidadesSelecionadasVincular;
	private List<Unidade> unidadesVinculadas;
	private Unidade unidadeSelecionada;

	private String nomeBusca;
	private boolean editandoUnidades = false;;

	private Boolean visualizando = Boolean.FALSE;
	private Boolean editando = Boolean.FALSE;

	private List<AvaliadorCiclo> avaliadores;
	private List<AvaliadorCiclo> avaliadoresEditando;

	/*
	 * Unidades
	 */
	public void prepararModalVincularUnidades() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());
			unidadesSelecionadasVincular = new ArrayList<Unidade>();
			this.nomeBusca = "";
			editandoUnidades = false;

			if (getUnidadesVinculadas() != null) {
				unidadesDisponiveis.removeAll(getUnidadesVinculadas());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
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
			log.error(e.getMessage(), e);
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
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void desvincularUnidade() {
		try {
			this.unidadesVinculadas.remove(this.unidadeSelecionada);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao desvincular Unidade, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararModalVincularUnidadesEditar() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());
			unidadesSelecionadasVincular = getUnidadesVinculadas();
			this.nomeBusca = "";
			editandoUnidades = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao editar Unidade, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void inserirParametro() {
		try {
			if (this.unidadesVinculadas == null || this.unidadesVinculadas.isEmpty()) {
				FacesUtil.addErrorMessage("form-usuarios-externos:tblUnidadesSelecionadas",
						"Informe no minimo uma Unidade.");
			}
			for (Unidade un : this.unidadesVinculadas) {
				List<AvaliadorCiclo> avaliadoresVinculados = avaliadorService.findByParameters(this.cicloConfiguracao,
						this.perfilAvaliador, un, this.usuarioAvaliador);
				if (avaliadoresVinculados == null || avaliadoresVinculados.isEmpty()) {
					AvaliadorCiclo av = new AvaliadorCiclo();
					av.setCicloConfiguracao(cicloConfiguracao);
					av.setPerfil(this.perfilAvaliador);
					av.setUnidade(un);
					av.setUsuario(this.usuarioAvaliador);
					av.setTipoAcao(this.tipoAcao);
					av.setFaseAjustes(this.faseAjustes);
					av.setFasePactuacao(this.fasePactuacao);
					av.setFaseRepactuacao(this.faseRepactuacao);
					av.setFaseValidacao(this.faseValidacao);
					this.avaliadorService.insert(av);
					this.cicloConfiguracao.setStatusConfiguracaoAvaliadores(StatusConfiguracao.C);
				}
			}
			
			try {
				cicloConfiguracaoService.save(cicloConfiguracao);
				cicloService.save(cicloConfiguracao.getCiclo());
			} catch (Exception e) {
				// do nothing
			}
			
			reset();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluir() {
		try {
			this.cicloConfiguracao = this.avaliador.getCicloConfiguracao();
			this.cicloConfiguracao.getAvaliadores().remove(this.avaliador);
			cicloConfiguracaoService.save(this.cicloConfiguracao);
			reset();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterarParametro() {
		try {
			for (AvaliadorCiclo av : avaliadoresEditando) {
				avaliadorService.delete(av);
			}

			for (Unidade un : this.unidadesVinculadas) {
				AvaliadorCiclo av = new AvaliadorCiclo();
				av.setCicloConfiguracao(cicloConfiguracao);
				av.setPerfil(this.perfilAvaliador);
				av.setUnidade(un);
				av.setUsuario(this.usuarioAvaliador);
				av.setTipoAcao(this.tipoAcao);
				av.setFaseAjustes(this.faseAjustes);
				av.setFasePactuacao(this.fasePactuacao);
				av.setFaseRepactuacao(this.faseRepactuacao);
				av.setFaseValidacao(this.faseValidacao);
				this.avaliadorService.insert(av);
			}
			
			try {
				cicloConfiguracaoService.save(cicloConfiguracao);
				cicloService.save(cicloConfiguracao.getCiclo());
			} catch (Exception e) {
				// do nothing
			}

			reset();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void editar(AvaliadorCiclo un) {
		try {
			this.avaliador = un;
			this.perfilAvaliador = this.avaliador.getPerfil();
			this.usuarioAvaliador = this.avaliador.getUsuario();

			this.unidadesVinculadas = avaliadorService.findUnidadesRelacionadas(this.avaliador.getCicloConfiguracao(),
					this.perfilAvaliador, this.usuarioAvaliador);

			this.avaliadoresEditando = avaliadorService.findByParameters(this.cicloConfiguracao, this.perfilAvaliador,
					this.usuarioAvaliador);

			this.tipoAcao = this.avaliador.getTipoAcao();
			this.faseAjustes = this.avaliador.getFaseAjustes();
			this.fasePactuacao = this.avaliador.getFasePactuacao();
			this.faseRepactuacao = this.avaliador.getFaseRepactuacao();
			this.faseValidacao = this.avaliador.getFaseValidacao();

			this.editando = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void reset() {
		this.perfilAvaliador = null;
		this.unidadesVinculadas = null;
		this.usuarioAvaliador = null;
		this.editando = false;
		this.avaliadores = null;
		this.tipoAcao = null;
		this.faseAjustes = Boolean.FALSE;
		this.fasePactuacao = Boolean.FALSE;
		this.faseRepactuacao = Boolean.FALSE;
		this.faseValidacao = Boolean.FALSE;
		this.faseTodas = Boolean.FALSE;
	}

	public List<Usuario> getUsuariosAvaliadores() {
		if (this.perfilAvaliador != null) {
			this.usuariosAvaliadores = usuarioService.findExternosByPerfilUF(this.perfilAvaliador, this.getCicloConfiguracao().getCiclo().getUf());
		}
		return usuariosAvaliadores;
	}

	public void setUsuariosAvaliadores(List<Usuario> usuariosAvaliadores) {
		this.usuariosAvaliadores = usuariosAvaliadores;
	}

	public List<Perfil> getPerfisAvaliadores() {
		if (perfisAvaliadores == null || perfisAvaliadores.isEmpty()) {
			perfisAvaliadores = perfilService.bindByChaves(Arrays.asList(Perfil.PERFIS_USUARIO_EXTERNO));
		}
		return perfisAvaliadores;
	}

	public void setPerfisAvaliadores(List<Perfil> perfisAvaliadores) {
		this.perfisAvaliadores = perfisAvaliadores;
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

	public Perfil getPerfilAvaliador() {
		return perfilAvaliador;
	}

	public void setPerfilAvaliador(Perfil perfilAvaliador) {
		this.perfilAvaliador = perfilAvaliador;
	}

	public List<Unidade> getUnidadesDisponiveis() {
		return unidadesDisponiveis;
	}

	public void setUnidadesDisponiveis(List<Unidade> unidadesDisponiveis) {
		this.unidadesDisponiveis = unidadesDisponiveis;
	}

	public List<Unidade> getUnidadesSelecionadasVincular() {
		return unidadesSelecionadasVincular;
	}

	public void setUnidadesSelecionadasVincular(List<Unidade> unidadesSelecionadasVincular) {
		this.unidadesSelecionadasVincular = unidadesSelecionadasVincular;
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

	public boolean isEditandoUnidades() {
		return editandoUnidades;
	}

	public void setEditandoUnidades(boolean editandoUnidades) {
		this.editandoUnidades = editandoUnidades;
	}

	public Unidade getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	public List<AvaliadorCiclo> getAvaliadores() {
		if (avaliadores == null || avaliadores.isEmpty()) {
			this.avaliadores = avaliadorService.findAvaliadoresExternosByCiclo(this.cicloConfiguracao);
		}
		return avaliadores;
	}

	public void setAvaliadores(List<AvaliadorCiclo> avaliadores) {
		this.avaliadores = avaliadores;
	}

	public Boolean getEditando() {
		return editando;
	}

	public void setEditando(Boolean editando) {
		this.editando = editando;
	}

	public AvaliadorCiclo getAvaliador() {
		return avaliador;
	}

	public void setAvaliador(AvaliadorCiclo avaliador) {
		this.avaliador = avaliador;
	}

	public Usuario getUsuarioAvaliador() {
		return usuarioAvaliador;
	}

	public void setUsuarioAvaliador(Usuario usuarioAvaliador) {
		this.usuarioAvaliador = usuarioAvaliador;
	}

	public List<AvaliadorCiclo> getAvaliadoresEditando() {
		return avaliadoresEditando;
	}

	public void setAvaliadoresEditando(List<AvaliadorCiclo> avaliadoresEditando) {
		this.avaliadoresEditando = avaliadoresEditando;
	}

	public Boolean getFasePactuacao() {
		return fasePactuacao;
	}

	public void setFasePactuacao(Boolean fasePactuacao) {
		this.fasePactuacao = fasePactuacao;
	}

	public Boolean getFaseRepactuacao() {
		return faseRepactuacao;
	}

	public void setFaseRepactuacao(Boolean faseRepactuacao) {
		this.faseRepactuacao = faseRepactuacao;
	}

	public Boolean getFaseAjustes() {
		return faseAjustes;
	}

	public void setFaseAjustes(Boolean faseAjustes) {
		this.faseAjustes = faseAjustes;
	}

	public Boolean getFaseValidacao() {
		return faseValidacao;
	}

	public void setFaseValidacao(Boolean faseValidacao) {
		this.faseValidacao = faseValidacao;
	}

	public Boolean getFaseTodas() {
		if (isTodasFasesSelecionadas()) {
			faseTodas = true;
		} else {
			faseTodas = false;
		}
		return faseTodas;
	}

	public void setFaseTodas(Boolean faseTodas) {
		this.faseTodas = faseTodas;
	}

	public TipoAcaoAvaliador getTipoAcao() {
		return tipoAcao;
	}

	public void setTipoAcao(TipoAcaoAvaliador tipoAcao) {
		this.tipoAcao = tipoAcao;
	}

	public List<TipoAcaoAvaliador> getTiposAcao() {
		return Arrays.asList(TipoAcaoAvaliador.values());
	}

	public void alterouTodasFases() {
		if (faseTodas) {
			fasePactuacao = Boolean.TRUE;
			faseRepactuacao = Boolean.TRUE;
			faseAjustes = Boolean.TRUE;
			faseValidacao = Boolean.TRUE;
		} else {
			fasePactuacao = Boolean.FALSE;
			faseRepactuacao = Boolean.FALSE;
			faseAjustes = Boolean.FALSE;
			faseValidacao = Boolean.FALSE;
		}
	}

	private boolean isTodasFasesSelecionadas() {
		if (fasePactuacao && faseRepactuacao && faseAjustes && faseValidacao) {
			return true;
		} else {
			return false;
		}
	}

}
