package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfiguracaoMetas;
import br.com.sebrae.sgm.model.EspacoOcupacional;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.EspacoOcupacionalService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class CicloConfigurarMetasDesenvolvimentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloConfigurarMetasDesenvolvimentoBean.class);

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private EspacoOcupacionalService espacoOcupacionalService;
	
	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;

	private ConfiguracaoMetas configuracaoMeta = new ConfiguracaoMetas();
	private Boolean visualizando = Boolean.FALSE;

	/** Unidades **/
	private String nomeBusca;
	private boolean editandoUnidades = false;
	private List<Unidade> unidadesDisponiveis;
	private List<Unidade> unidadesSelecionadasVincular;
	private Unidade unidadeSelecionada;

	/** Espaco Ocupacional **/
	private boolean editandoEspaco = false;
	private List<EspacoOcupacional> espacosDisponiveis;
	private List<EspacoOcupacional> espacosSelecionadasVincular;
	private EspacoOcupacional espacoSelecionado;

	@Override
	public void init() {
		super.init();
		if (this.cicloConfiguracao != null) {
			this.cicloConfiguracao = this.cicloConfiguracaoService.load(cicloConfiguracao.getId());
		}
	}

	public void inserirParametro() {
		try {

			if (this.configuracaoMeta.getUnidades() == null || this.configuracaoMeta.getUnidades().isEmpty()) {
				FacesUtil.addErrorMessage("form-individual:tblUnidadesSelecionadas",
						"Informe no m\u00EDnimo uma Unidade.");
				FacesUtil.addErrorMessage("Informe no m\u00EDnimo uma Unidade.");
				return;
			}

			if (this.configuracaoMeta.getEspacosOcupacionais() == null
					|| this.configuracaoMeta.getEspacosOcupacionais().isEmpty()) {
				FacesUtil.addErrorMessage("form-individual:tblEspacosSelecionados",
						"Informe no m\u00EDnimo um Espa\u00E7o Ocupacional");
				FacesUtil.addErrorMessage("Informe no m\u00EDnimo um Espa\u00E7o Ocupacional");
				return;
			}

			if (this.cicloConfiguracao.getConfiguracoesMetas() == null) {
				this.cicloConfiguracao.setConfiguracoesMetas(new ArrayList<ConfiguracaoMetas>());
			}

			this.configuracaoMeta.setTipoMeta(TipoMeta.D);
			this.cicloConfiguracao.getConfiguracoesMetas().add(this.configuracaoMeta);
			this.configuracaoMeta.setCicloConfiguracao(this.cicloConfiguracao);
			verificaStatus();
			this.cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.configuracaoMeta = new ConfiguracaoMetas();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void verificaStatus() {
		if (!this.cicloConfiguracao.getConfiguracoesMetasDesenvolvimento().isEmpty()) {
			this.cicloConfiguracao.setStatusConfiguracaoMetas(StatusConfiguracao.C);
		} else {
			this.cicloConfiguracao.setStatusConfiguracaoMetas(StatusConfiguracao.A);
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
			this.cicloConfiguracao.getConfiguracoesMetas().remove(getConfiguracaoMeta());
			verificaStatus();
			cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.configuracaoMeta = new ConfiguracaoMetas();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterarParametro() {
		try {
			if (this.configuracaoMeta.getUnidades() == null || this.configuracaoMeta.getUnidades().isEmpty()) {
				FacesUtil.addErrorMessage("form-individual", "Informe no m\u00EDnimo uma Unidade.");
				return;
			}

			if (this.configuracaoMeta.getEspacosOcupacionais() == null
					|| this.configuracaoMeta.getEspacosOcupacionais().isEmpty()) {
				FacesUtil.addErrorMessage("form-individual", "Informe no m\u00EDnimo um Espa\u00E7o Ocupacional");
				return;
			}

			this.cicloConfiguracaoService.save(cicloConfiguracao);
			this.configuracaoMeta = new ConfiguracaoMetas();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao inserir par�metro.");
			log.error(e.getMessage(), e);
		}
	}

	public void editar(ConfiguracaoMetas cfg) {
		try {
			this.configuracaoMeta = cfg;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * Unidades
	 */
	public void prepararModalVincularUnidades() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());
			unidadesSelecionadasVincular = new ArrayList<Unidade>();
			this.nomeBusca = "";
			editandoUnidades = false;
			if (getConfiguracaoMeta().getUnidades() != null) {
				unidadesDisponiveis.removeAll(getConfiguracaoMeta().getUnidades());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void buscarUnidadesVincular() {
		try {
			this.unidadesDisponiveis = unidadeService.findByDescricao(this.nomeBusca);
			if (getConfiguracaoMeta().getUnidades() != null) {
				unidadesDisponiveis.removeAll(getConfiguracaoMeta().getUnidades());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("formUnidadesVincular", "Erro ao buscar unidades, tente novamente mais tarde.");
		}
	}

	public void vincularUnidades() {
		try {
			if (editandoUnidades) {
				this.getConfiguracaoMeta().setUnidades(unidadesSelecionadasVincular);
			} else {
				if (this.getConfiguracaoMeta().getUnidades() == null) {
					this.getConfiguracaoMeta().setUnidades(new ArrayList<Unidade>());
				}
				List<Unidade> unidadesVinculadosTemp = this.getConfiguracaoMeta().getUnidades();
				unidadesVinculadosTemp.addAll(this.unidadesSelecionadasVincular);

				Collections.sort(unidadesVinculadosTemp, new Comparator<Unidade>() {
					@Override
					public int compare(Unidade o1, Unidade o2) {
						return o1.getDescricao().compareTo(o2.getDescricao());
					}
				});
			}
			this.configuracaoMeta.setEspacosOcupacionais(new ArrayList<EspacoOcupacional>());
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void desvincularUnidade() {
		try {
			this.getConfiguracaoMeta().getUnidades().remove(this.unidadeSelecionada);
			this.configuracaoMeta.setEspacosOcupacionais(new ArrayList<EspacoOcupacional>());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararModalVincularUnidadesEditar() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());
			unidadesSelecionadasVincular = getConfiguracaoMeta().getUnidades();
			this.nomeBusca = "";
			editandoUnidades = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * Espaco ocupacional
	 */
	public void prepararModalVincularEspacos() {
		try {
			espacosDisponiveis = espacoOcupacionalService.buscarPelaUfCicloSelecionado(this.cicloConfiguracao
					.getCiclo().getUf());

			if (this.cicloConfiguracao.getConfiguracoesMetas() != null
					&& !this.cicloConfiguracao.getConfiguracoesMetas().isEmpty()) {
				List<ConfiguracaoMetas> configuracaoMetas = this.cicloConfiguracao.getConfiguracoesMetas();
				List<EspacoOcupacional> espacosRemover = new ArrayList<EspacoOcupacional>();
				for (ConfiguracaoMetas confMeta : configuracaoMetas) {
					if (!confMeta.equals(this.configuracaoMeta)) {
						List<Unidade> unidadesConfiguracaoAtual = this.configuracaoMeta.getUnidades();
						for (Unidade unidade : unidadesConfiguracaoAtual) {
							if (confMeta.getUnidades().contains(unidade)) {
								espacosRemover.addAll(confMeta.getEspacosOcupacionais());
							}
						}
					}
				}
				espacosDisponiveis.removeAll(espacosRemover);
			}

			espacosSelecionadasVincular = new ArrayList<EspacoOcupacional>();
			this.nomeBusca = "";
			editandoEspaco = false;
			if (getConfiguracaoMeta().getEspacosOcupacionais() != null) {
				espacosDisponiveis.removeAll(getConfiguracaoMeta().getEspacosOcupacionais());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void buscarEspacosVincular() {
		try {
			this.espacosDisponiveis = espacoOcupacionalService.buscarPelaDescricao(this.nomeBusca);
			if (getConfiguracaoMeta().getEspacosOcupacionais() != null) {
				espacosDisponiveis.removeAll(getConfiguracaoMeta().getEspacosOcupacionais());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("formEspacoOcupacional",
					"Erro ao buscar Espa\u00E7o Ocupacional, tente novamente mais tarde.");
		}
	}

	public void vincularEspacos() {
		try {
			if (editandoEspaco) {
				this.getConfiguracaoMeta().setEspacosOcupacionais(this.espacosSelecionadasVincular);
			} else {
				if (this.getConfiguracaoMeta().getEspacosOcupacionais() == null) {
					this.getConfiguracaoMeta().setEspacosOcupacionais(new ArrayList<EspacoOcupacional>());
				}

				List<EspacoOcupacional> espacosVinculadosTemp = this.getConfiguracaoMeta().getEspacosOcupacionais();
				espacosVinculadosTemp.addAll(this.espacosSelecionadasVincular);

				Collections.sort(espacosVinculadosTemp, new Comparator<EspacoOcupacional>() {
					@Override
					public int compare(EspacoOcupacional o1, EspacoOcupacional o2) {
						return o1.getDescricao().compareTo(o2.getDescricao());
					}
				});
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void desvincularEspaco() {
		try {
			this.getConfiguracaoMeta().getEspacosOcupacionais().remove(this.espacoSelecionado);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararModalVincularEspacosEditar() {
		try {
			espacosDisponiveis = espacoOcupacionalService.buscarPelaUfCicloSelecionado(this.cicloConfiguracao
					.getCiclo().getUf());
			espacosSelecionadasVincular = getConfiguracaoMeta().getEspacosOcupacionais();
			this.nomeBusca = "";
			editandoEspaco = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
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

	public ConfiguracaoMetas getConfiguracaoMeta() {
		return configuracaoMeta;
	}

	public void setConfiguracaoMeta(ConfiguracaoMetas configuracaoMeta) {
		this.configuracaoMeta = configuracaoMeta;
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

	public Unidade getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	public boolean isEditandoEspaco() {
		return editandoEspaco;
	}

	public void setEditandoEspaco(boolean editandoEspaco) {
		this.editandoEspaco = editandoEspaco;
	}

	public List<EspacoOcupacional> getEspacosDisponiveis() {
		return espacosDisponiveis;
	}

	public void setEspacosDisponiveis(List<EspacoOcupacional> espacosDisponiveis) {
		this.espacosDisponiveis = espacosDisponiveis;
	}

	public List<EspacoOcupacional> getEspacosSelecionadasVincular() {
		return espacosSelecionadasVincular;
	}

	public void setEspacosSelecionadasVincular(List<EspacoOcupacional> espacosSelecionadasVincular) {
		this.espacosSelecionadasVincular = espacosSelecionadasVincular;
	}

	public EspacoOcupacional getEspacoSelecionado() {
		return espacoSelecionado;
	}

	public void setEspacoSelecionado(EspacoOcupacional espacoSelecionado) {
		this.espacoSelecionado = espacoSelecionado;
	}
}
