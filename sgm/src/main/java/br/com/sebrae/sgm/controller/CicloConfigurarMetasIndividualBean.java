package br.com.sebrae.sgm.controller;

import java.math.BigDecimal;
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
import br.com.sebrae.sgm.model.PorcentagemRemuneracaoVariavel;
import br.com.sebrae.sgm.model.PorcentagemRemuneracaoVariavelPK;
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
public class CicloConfigurarMetasIndividualBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloConfigurarMetasIndividualBean.class);

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

	private ConfiguracaoMetas configuracaoMetaVisualizacaoRv;

	@Override
	public void init() {
		super.init();
		if (this.cicloConfiguracao != null) {
			this.cicloConfiguracao = this.cicloConfiguracaoService.load(cicloConfiguracao.getId());
		}
	}

	public void inserirParametro() {
		try {

			if (configuracaoMeta.getQtdMinimaMetasIndividuaisPactuadasCiclo() > configuracaoMeta
					.getQtdMaximaMetasIndividuaisPactuadasCiclo()) {
				FacesUtil
						.addErrorMessage("form-individual:qtdMinimasMetas",
								"A quantidade m\u00EDnima de metas n\u00E3o pode ser maior que a quantidade m\u00E1xima de metas.");
				FacesUtil
						.addErrorMessage("form-individual:qtdMaximaMetas",
								"A quantidade m\u00E1xima de metas n\u00E3o pode ser menor que a quantidade m\u00EDnima de metas.");
				return;
			}

			if (this.configuracaoMeta.getUnidades() == null || this.configuracaoMeta.getUnidades().isEmpty()) {
				FacesUtil.addErrorMessage("form-individual", "Informe no m\u00EDnimo uma Unidade.");
				return;
			}

			if (this.configuracaoMeta.getEspacosOcupacionais() == null
					|| this.configuracaoMeta.getEspacosOcupacionais().isEmpty()) {
				FacesUtil.addErrorMessage("form-individual", "Informe no m\u00EDnimo um Espa\u00E7o Ocupacional");
				return;
			}

			if (this.configuracaoMeta.getImpactaRemuneracaoVariavel()) {
				if (this.configuracaoMeta.getPorcentagensRemuneracaoVariavel() == null
						|| this.configuracaoMeta.getPorcentagensRemuneracaoVariavel().isEmpty()) {
					FacesUtil.addErrorMessage("form-individual", "Gere a tabela antes de inserir o par\u00E2metro");
					return;
				}

				if (this.configuracaoMeta.getPorcentagensRemuneracaoVariavel().size() != getQtdCombinacoes()) {
					FacesUtil
							.addErrorMessage("form-individual",
									"Tabela gerada incoerente com a quantidade m\u00EDnima e m\u00E1xima de metas, gere novamente a tabela.");
					return;
				}

				boolean informou = false;
				List<PorcentagemRemuneracaoVariavel> porcentagens = this.configuracaoMeta
						.getPorcentagensRemuneracaoVariavel();
				for (PorcentagemRemuneracaoVariavel pct : porcentagens) {
					if (pct.getPercentualRemuneracaoVariavel() != null
							&& pct.getPercentualRemuneracaoVariavel().compareTo(BigDecimal.ZERO) > 0) {
						informou = true;
					}
				}

				if (!informou) {
					FacesUtil.addErrorMessage("form-individual", "Informe no m\u00EDnimo o valor de uma porcentagem.");
					return;
				}
			}

			if (this.cicloConfiguracao.getConfiguracoesMetas() == null) {
				this.cicloConfiguracao.setConfiguracoesMetas(new ArrayList<ConfiguracaoMetas>());
			}

			this.configuracaoMeta.setTipoMeta(TipoMeta.I);
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
		
		if ((cicloConfiguracao.getNaoParametrizarMetasDesempenhoEquipe() == true && !this.cicloConfiguracao.getConfiguracoesMetasEquipe().isEmpty()) 
				|| (cicloConfiguracao.getNaoParametrizarMetasDesempenhoIndividual() == true && !this.cicloConfiguracao.getConfiguracoesMetasIndividual().isEmpty())
				|| (!this.cicloConfiguracao.getConfiguracoesMetasEquipe().isEmpty() && !this.cicloConfiguracao.getConfiguracoesMetasIndividual().isEmpty())
				|| (cicloConfiguracao.getNaoParametrizarMetasDesempenhoIndividual() == true && !this.cicloConfiguracao.getConfiguracoesMetasEquipe().isEmpty())
				|| (cicloConfiguracao.getNaoParametrizarMetasDesempenhoEquipe() == true && !this.cicloConfiguracao.getConfiguracoesMetasIndividual().isEmpty())) {
			
			this.cicloConfiguracao.setStatusConfiguracaoMetas(StatusConfiguracao.C);

		} else {
			this.cicloConfiguracao.setStatusConfiguracaoMetas(StatusConfiguracao.A);
		}

		try {
			cicloConfiguracaoService.save(cicloConfiguracao);
			cicloService.save(cicloConfiguracao.getCiclo());
		} catch (Exception e) {
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

			if (configuracaoMeta.getQtdMinimaMetasIndividuaisPactuadasCiclo() > configuracaoMeta
					.getQtdMaximaMetasIndividuaisPactuadasCiclo()) {
				FacesUtil
						.addErrorMessage("form-individual:qtdMinimasMetas",
								"A quantidade m\u00EDnima de metas n\u00E3o pode ser maior que a quantidade m\u00E1xima de metas.");
				FacesUtil
						.addErrorMessage("form-individual:qtdMaximaMetas",
								"A quantidade m\u00E1xima de metas n\u00E3o pode ser menor que a quantidade m\u00EDnima de metas.");
				return;
			}

			if (this.configuracaoMeta.getUnidades() == null || this.configuracaoMeta.getUnidades().isEmpty()) {
				FacesUtil.addErrorMessage("Informe no m\u00EDnimo uma Unidade.");
				FacesUtil.addErrorMessage("form-individual:tblUnidadesSelecionadas",
						"Informe no m\u00EDnimo uma Unidade.");
				return;
			}

			if (this.configuracaoMeta.getEspacosOcupacionais() == null
					|| this.configuracaoMeta.getEspacosOcupacionais().isEmpty()) {
				FacesUtil.addErrorMessage("Informe no m\u00EDnimo um Espa\u00E7o Ocupacional");
				FacesUtil.addErrorMessage("form-individual:tblEspacosSelecionados",
						"Informe no m\u00EDnimo um Espa\u00E7o Ocupacional");
				return;
			}

			if (this.configuracaoMeta.getImpactaRemuneracaoVariavel()) {
				if (this.configuracaoMeta.getPorcentagensRemuneracaoVariavel() == null
						|| this.configuracaoMeta.getPorcentagensRemuneracaoVariavel().isEmpty()) {
					FacesUtil.addErrorMessage("Gere a tabela antes de inserir o par\u00E2metro");
					FacesUtil.addErrorMessage("form-individual:tblPorcentagens",
							"Gere a tabela antes de inserir o par\u00E2metro");
					return;
				}

				if (this.configuracaoMeta.getPorcentagensRemuneracaoVariavel().size() != getQtdCombinacoes()) {
					FacesUtil
							.addErrorMessage("Tabela gerada incoerente com a quantidade m\u00EDnima e m\u00E1xima de metas, gere novamente a tabela.");
					FacesUtil
							.addErrorMessage("form-individual:tblPorcentagens",
									"Tabela gerada incoerente com a quantidade m\u00EDnima e m\u00E1xima de metas, gere novamente a tabela.");
					return;
				}
			}
			verificaStatus();
			this.cicloConfiguracaoService.save(cicloConfiguracao);
			this.configuracaoMeta = new ConfiguracaoMetas();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao inserir par\u00E2metro.");
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

	public void visualizarRv(ConfiguracaoMetas cfg) {
		try {
			this.configuracaoMetaVisualizacaoRv = cfg;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void gerarTabela() {
		try {
			Integer minMetas = this.configuracaoMeta.getQtdMinimaMetasIndividuaisPactuadasCiclo();
			Integer maxMetas = this.configuracaoMeta.getQtdMaximaMetasIndividuaisPactuadasCiclo();

			if (maxMetas.compareTo(minMetas) < 0) {
				FacesUtil.addErrorMessage("form-individual:tblPorcentagens",
						"O n\u00FAmero m\u00E1ximo de metas deve ser maior ou igual o m\u00EDnimo de metas");
				return;
			}

			/*if (maxMetas.compareTo(50) > 0 || minMetas.compareTo(50) > 0) {
				FacesUtil
						.addErrorMessage(
								"form-individual:tblPorcentagens",
								"Quantidade de combina\u00E7\u00F5es muito grande, reduza o n\u00FAmero de metas, esta a\u00E7\u00E3o pode ocasionar o travamento do sistema.");
				return;
			}*/

			if (this.configuracaoMeta.getPorcentagensRemuneracaoVariavel() == null) {
				this.configuracaoMeta
						.setPorcentagensRemuneracaoVariavel(new ArrayList<PorcentagemRemuneracaoVariavel>());
			} else {
				this.configuracaoMeta.getPorcentagensRemuneracaoVariavel().clear();
			}

			for (int i = minMetas; i <= maxMetas; i++) {
				for (int x = 0; x <= i; x++) {
					PorcentagemRemuneracaoVariavel prv = new PorcentagemRemuneracaoVariavel(
							new PorcentagemRemuneracaoVariavelPK(i, x, this.configuracaoMeta.getId()));
					prv.setConfiguracaoMetas(this.configuracaoMeta);
					this.configuracaoMeta.getPorcentagensRemuneracaoVariavel().add(prv);
				}
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public int getQtdCombinacoes() {
		int retorno = 0;
		int minMetas = this.configuracaoMeta.getQtdMinimaMetasIndividuaisPactuadasCiclo();
		int maxMetas = this.configuracaoMeta.getQtdMaximaMetasIndividuaisPactuadasCiclo();

		for (int i = minMetas; i <= maxMetas; i++) {
			for (int x = 0; x <= i; x++) {
				retorno++;
			}
		}

		return retorno;
	}

	/*
	 * Unidades
	 */
	public void prepararModalVincularUnidades() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());

			/*
			 * if (this.cicloConfiguracao.getConfiguracoesMetas() != null &&
			 * !this.cicloConfiguracao.getConfiguracoesMetas().isEmpty()) { List<ConfiguracaoMetas> configuracaoMetas =
			 * this.cicloConfiguracao.getConfiguracoesMetas(); List<Unidade> undiadesRemover = new ArrayList<Unidade>();
			 * for (ConfiguracaoMetas confMeta : configuracaoMetas) { if (confMeta.getTipoMeta() == TipoMeta.I) if
			 * (!confMeta.equals(this.configuracaoMeta)) { undiadesRemover.addAll(confMeta.getUnidades()); } }
			 * unidadesDisponiveis.removeAll(undiadesRemover); }
			 */

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
			if (this.cicloConfiguracao.getConfiguracoesMetas() != null
					&& !this.cicloConfiguracao.getConfiguracoesMetas().isEmpty()) {
				List<ConfiguracaoMetas> configuracaoMetas = this.cicloConfiguracao.getConfiguracoesMetas();
				List<Unidade> undiadesRemover = new ArrayList<Unidade>();
				for (ConfiguracaoMetas confMeta : configuracaoMetas) {
					if (confMeta.getTipoMeta() == TipoMeta.I)
						if (!confMeta.equals(this.configuracaoMeta)) {
							undiadesRemover.addAll(confMeta.getUnidades());
						}
				}
				unidadesDisponiveis.removeAll(undiadesRemover);
			}
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
			this.getConfiguracaoMeta().setEspacosOcupacionais(new ArrayList<EspacoOcupacional>());
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void desvincularUnidade() {
		try {
			this.getConfiguracaoMeta().getUnidades().remove(this.unidadeSelecionada);
			this.getConfiguracaoMeta().setEspacosOcupacionais(new ArrayList<EspacoOcupacional>());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararModalVincularUnidadesEditar() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());

			// if (this.cicloConfiguracao.getConfiguracoesMetas() != null
			// && !this.cicloConfiguracao.getConfiguracoesMetas().isEmpty()) {
			// List<ConfiguracaoMetas> configuracaoMetas = this.cicloConfiguracao.getConfiguracoesMetas();
			// List<Unidade> undiadesRemover = new ArrayList<Unidade>();
			// for (ConfiguracaoMetas confMeta : configuracaoMetas) {
			// if (confMeta.getTipoMeta() == TipoMeta.I)
			// if (!confMeta.equals(this.configuracaoMeta)) {
			// undiadesRemover.addAll(confMeta.getUnidades());
			// }
			// }
			// unidadesDisponiveis.removeAll(undiadesRemover);
			// }

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
					if (confMeta.getTipoMeta() == TipoMeta.I)
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

			if (this.cicloConfiguracao.getConfiguracoesMetas() != null
					&& !this.cicloConfiguracao.getConfiguracoesMetas().isEmpty()) {
				List<ConfiguracaoMetas> configuracaoMetas = this.cicloConfiguracao.getConfiguracoesMetas();
				List<EspacoOcupacional> espacosRemover = new ArrayList<EspacoOcupacional>();
				for (ConfiguracaoMetas confMeta : configuracaoMetas) {
					if (confMeta.getTipoMeta() == TipoMeta.I)
						if (!confMeta.equals(this.configuracaoMeta)) {
							espacosRemover.addAll(confMeta.getEspacosOcupacionais());
						}
				}
				espacosDisponiveis.removeAll(espacosRemover);
			}

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

			if (this.cicloConfiguracao.getConfiguracoesMetas() != null
					&& !this.cicloConfiguracao.getConfiguracoesMetas().isEmpty()) {
				List<ConfiguracaoMetas> configuracaoMetas = this.cicloConfiguracao.getConfiguracoesMetas();
				List<EspacoOcupacional> espacosRemover = new ArrayList<EspacoOcupacional>();
				for (ConfiguracaoMetas confMeta : configuracaoMetas) {
					if (confMeta.getTipoMeta() == TipoMeta.I)
						if (!confMeta.equals(this.configuracaoMeta)) {
							espacosRemover.addAll(confMeta.getEspacosOcupacionais());
						}
				}
				espacosDisponiveis.removeAll(espacosRemover);
			}

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

	public ConfiguracaoMetas getConfiguracaoMetaVisualizacaoRv() {
		return configuracaoMetaVisualizacaoRv;
	}

	public void setConfiguracaoMetaVisualizacaoRv(ConfiguracaoMetas configuracaoMetaVisualizacaoRv) {
		this.configuracaoMetaVisualizacaoRv = configuracaoMetaVisualizacaoRv;
	}

}
