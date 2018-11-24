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
import br.com.sebrae.sgm.model.FluxoAprovacaoMetas;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.enums.FluxoCiclo;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.FluxoAprovacaoMetasService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class CicloFluxoAprovacaoMetasDesenvolvimentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloFluxoAprovacaoMetasDesenvolvimentoBean.class);

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private FluxoAprovacaoMetasService fluxoAprovacaoService;

	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;
	private FluxoAprovacaoMetas fluxoAprovacao = new FluxoAprovacaoMetas();

	private Boolean visualizando = Boolean.FALSE;

	/** Unidades **/
	private String nomeBusca;

	private boolean editandoUnidades = false;
	private List<Unidade> unidadesDisponiveis;
	private List<Unidade> unidadesSelecionadasVincular;
	private Unidade unidadeSelecionada;

	public void inserirParametro() {
		try {
			if (this.fluxoAprovacao.getUnidades() == null || this.fluxoAprovacao.getUnidades().isEmpty()) {
				FacesUtil.addErrorMessage("form-fluxo-metas:tblUnidadesSelecionadas",
						"Informe no m\u00EDnimo uma Unidade.");
				return;
			}

			if (this.cicloConfiguracao.getFluxosAprovacaoMetas() == null) {
				this.cicloConfiguracao.setFluxosAprovacaoMetas(new ArrayList<FluxoAprovacaoMetas>());
			}
			this.cicloConfiguracao.getFluxosAprovacaoMetas().add(this.fluxoAprovacao);
			this.fluxoAprovacao.setCicloConfiguracao(this.cicloConfiguracao);
			verificaStatus();
			this.cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.fluxoAprovacao = new FluxoAprovacaoMetas();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void verificaStatus() {
		if (this.cicloConfiguracao.getFluxosAprovacaoMetas() != null
				&& !this.cicloConfiguracao.getFluxosAprovacaoMetas().isEmpty()) {
			this.cicloConfiguracao.setStatusConfiguracaoFluxoAprovacaoDesempenho(StatusConfiguracao.C);
		} else {
			this.cicloConfiguracao.setStatusConfiguracaoFluxoAprovacaoDesempenho(StatusConfiguracao.A);
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
			this.cicloConfiguracao.getFluxosAprovacaoMetas().remove(this.fluxoAprovacao);
			verificaStatus();
			cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.fluxoAprovacao = new FluxoAprovacaoMetas();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterarParametro() {
		try {
			if (this.fluxoAprovacao.getUnidades() == null || this.fluxoAprovacao.getUnidades().isEmpty()) {
				FacesUtil.addErrorMessage("form-fluxo-metas:tblUnidadesSelecionadas", "Informe no m�nimo uma Unidade.");
				return;
			}
			if (cicloConfiguracao.getFluxosAprovacaoMetas().contains(fluxoAprovacao)) {
				cicloConfiguracao.getFluxosAprovacaoMetas().remove(fluxoAprovacao);
			}

			cicloConfiguracao.getFluxosAprovacaoMetas().add(fluxoAprovacao);

			this.cicloConfiguracaoService.save(cicloConfiguracao);
			this.fluxoAprovacao = new FluxoAprovacaoMetas();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void editar(FluxoAprovacaoMetas fx) {
		try {
			this.fluxoAprovacao = fx;
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
			if (fluxoAprovacao.getUnidades() != null) {
				unidadesDisponiveis.removeAll(fluxoAprovacao.getUnidades());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void vincularUnidades() {
		try {
			if (editandoUnidades) {
				this.fluxoAprovacao.setUnidades(unidadesSelecionadasVincular);
			} else {
				if (this.fluxoAprovacao.getUnidades() == null) {
					this.fluxoAprovacao.setUnidades(new ArrayList<Unidade>());
				}
				List<Unidade> unidadesVinculadosTemp = this.fluxoAprovacao.getUnidades();
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
			this.fluxoAprovacao.getUnidades().remove(this.unidadeSelecionada);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao desvincular Unidade, tente novamente mais tarde.");
		}
	}

	public void prepararModalVincularUnidadesEditar() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());
			unidadesSelecionadasVincular = this.fluxoAprovacaoService.getUnidadesFluxoAprovacao(this
					.getFluxoAprovacao().getId());
			this.nomeBusca = "";
			editandoUnidades = true;
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

	public FluxoAprovacaoMetas getFluxoAprovacao() {
		return fluxoAprovacao;
	}

	public void setFluxoAprovacao(FluxoAprovacaoMetas fluxoAprovacao) {
		this.fluxoAprovacao = fluxoAprovacao;
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

	public List<FluxoCiclo> getFluxosAprovacao() {
		return Arrays.asList(new FluxoCiclo[] { FluxoCiclo.FX1, FluxoCiclo.FX2, FluxoCiclo.FX3, FluxoCiclo.FX4,
				FluxoCiclo.FX5, FluxoCiclo.FX6, FluxoCiclo.FX7, FluxoCiclo.FX8 });
	}

	public List<FluxoCiclo> getFluxosAprovacaoDesenvolvimento() {
		return Arrays.asList(new FluxoCiclo[] { FluxoCiclo.FX9, FluxoCiclo.FX10, FluxoCiclo.FX11 });
	}

}
