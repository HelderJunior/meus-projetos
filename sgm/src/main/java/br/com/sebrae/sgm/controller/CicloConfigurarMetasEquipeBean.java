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

import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfiguracaoMetas;
import br.com.sebrae.sgm.model.ExcecaoResponsabilidades;
import br.com.sebrae.sgm.model.PorcentagemRemuneracaoVariavel;
import br.com.sebrae.sgm.model.PorcentagemRemuneracaoVariavelPK;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class CicloConfigurarMetasEquipeBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloConfigurarMetasEquipeBean.class);

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private AppBean appBean;

	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;

	private ConfiguracaoMetas configuracaoMeta = new ConfiguracaoMetas();
	private Boolean visualizando = Boolean.FALSE;

	private ConfiguracaoMetas configuracaoMetaVisualizacaoRv;

	/** Unidades **/
	private String nomeBusca;

	private boolean editandoUnidades = false;

	private List<Unidade> unidadesDisponiveis;
	private List<Unidade> unidadesSelecionadasVincular;
	private Unidade unidadeSelecionada;

	@Override
	public void init() {
		super.init();
		if (this.cicloConfiguracao != null) {
			this.cicloConfiguracao = this.cicloConfiguracaoService.load(cicloConfiguracao.getId());
		}
	}

	public boolean colaboradorPodeInserirMetaEquipeDesenv() {
		Ciclo cicloAtual = appBean.getCicloSelecionado();

		boolean retorno = false;
		if (cicloAtual.getConfiguracoes() != null) {
			List<CicloConfiguracao> lista = cicloAtual.getConfiguracoes();
			Usuario usuarioAutenticado = appBean.getUsuarioAutenticado();
			for (CicloConfiguracao cicloConfiguracao : lista) {
				if (cicloConfiguracao.getTipoConfiguracao() == TipoConfiguracaoCiclo.DESENV) {
					for (ConfiguracaoMetas configuracaoMetas : cicloConfiguracao.getConfiguracoesMetas()) {
						if (configuracaoMetas.getTipoMeta() == TipoMeta.E) {
							List<Unidade> listaUnidades = configuracaoMetas.getUnidades();
							for (Unidade unidade : listaUnidades) {
								List<Usuario> listaUsuario = unidade.getUsuarios();
								for (Usuario usuario : listaUsuario) {
									if (usuario.equals(usuarioAutenticado)) {
										retorno = true;
									}

								}

							}
						}
					}
				}

			}
		}
		return retorno;
	}

	public boolean colaboradorInserirMetaEquipeDesemp() {
		Ciclo cicloAtual = appBean.getCicloSelecionado();
		boolean retorno = false;

		if (cicloAtual.getConfiguracoes() != null) {
			List<CicloConfiguracao> lista = cicloAtual.getConfiguracoes();
			Usuario usuarioAutenticado = appBean.getUsuarioAutenticado();
			for (CicloConfiguracao cicloConfiguracao : lista) {
				if (cicloConfiguracao.getTipoConfiguracao() == TipoConfiguracaoCiclo.DESEMP) {
					for (ExcecaoResponsabilidades excecoes : cicloConfiguracao.getExcecoesResponsabilidades()) {
						if (excecoes.getUsuarioResponsavel().equals(usuarioAutenticado) && excecoes.getUsuarioResponsavel().getUnidadeAtual().equals(usuarioAutenticado.getUnidadeAtual())) {
							retorno = true;
						}
					}

				}
			}
		}
		return retorno;
	}

	public boolean podeInserirMetaEquipeDesemp() {
		Ciclo cicloAtual = appBean.getCicloSelecionado();

		boolean retorno = false;

		if (cicloAtual.getConfiguracoes() != null) {
			List<CicloConfiguracao> lista = cicloAtual.getConfiguracoes();
			Usuario usuarioAutenticado = appBean.getUsuarioAutenticado();
			for (CicloConfiguracao cicloConfiguracao : lista) {
				if (cicloConfiguracao.getTipoConfiguracao() == TipoConfiguracaoCiclo.DESEMP) {
					for (ConfiguracaoMetas configuracaoMetas : cicloConfiguracao.getConfiguracoesMetas()) {
						if (configuracaoMetas.getTipoMeta() == TipoMeta.E) {
							List<Unidade> listaUnidades = configuracaoMetas.getUnidades();
							for (Unidade unidade : listaUnidades) {
								List<Usuario> listaUsuario = unidade.getUsuarios();
								for (Usuario usuario : listaUsuario) {
									if (usuario.equals(usuarioAutenticado)) {
										retorno = true;
									}

								}

							}
						}
					}
				}

			}
		}
		return retorno;
	}

	public void inserirParametro() {
		try {
			if (configuracaoMeta.getQtdMinimaMetasIndividuaisPactuadasCiclo() > configuracaoMeta.getQtdMaximaMetasIndividuaisPactuadasCiclo()) {
				FacesUtil.addErrorMessage("form-equipes:qtdMinimasMetas",
						"A quantidade m\u00EDnima de metas n\u00E3o pode ser maior que a quantidade m\u00E1xima de metas.");
				FacesUtil.addErrorMessage("form-equipes:qtdMaximaMetas",
						"A quantidade m\u00E1xima de metas n\u00E3o pode ser menor que a quantidade m\u00EDnima de metas.");
				return;
			}
			if (this.configuracaoMeta.getUnidades() == null || this.configuracaoMeta.getUnidades().isEmpty()) {
				FacesUtil.addErrorMessage("form-equipes", "Informe no m\u00EDnimo uma Unidade.");
				return;
			}

			if (this.configuracaoMeta.getImpactaRemuneracaoVariavel()) {
				if (this.configuracaoMeta.getPorcentagensRemuneracaoVariavel() == null || this.configuracaoMeta.getPorcentagensRemuneracaoVariavel().isEmpty()) {
					FacesUtil.addErrorMessage("form-equipes", "Gere a tabela antes de inserir o par\u00E2metro");
					return;
				}
				if (this.configuracaoMeta.getPorcentagensRemuneracaoVariavel().size() != getQtdCombinacoes()) {
					FacesUtil.addErrorMessage("form-equipes",
							"Tabela gerada incoerente com a quantidade m\u00EDnima e m\u00E1xima de metas, gere novamente a tabela.");
					return;
				}

				boolean informou = false;
				List<PorcentagemRemuneracaoVariavel> porcentagens = this.configuracaoMeta.getPorcentagensRemuneracaoVariavel();
				for (PorcentagemRemuneracaoVariavel pct : porcentagens) {
					if (pct.getPercentualRemuneracaoVariavel() != null && pct.getPercentualRemuneracaoVariavel().compareTo(BigDecimal.ZERO) > 0) {
						informou = true;
					}
				}

				if (!informou) {
					FacesUtil.addErrorMessage("form-equipes", "Informe no m\u00EDnimo o valor de uma porcentagem.");
					return;
				}
			}

			verificaStatus();
			if (this.cicloConfiguracao.getConfiguracoesMetas() == null) {
				this.cicloConfiguracao.setConfiguracoesMetas(new ArrayList<ConfiguracaoMetas>());
			}

			this.configuracaoMeta.setTipoMeta(TipoMeta.E);
			this.cicloConfiguracao.getConfiguracoesMetas().add(this.configuracaoMeta);
			this.configuracaoMeta.setCicloConfiguracao(this.cicloConfiguracao);
			this.cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.configuracaoMeta = new ConfiguracaoMetas();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void verificaStatus() {

		if ((cicloConfiguracao.getNaoParametrizarMetasDesempenhoEquipe() == true && !this.cicloConfiguracao.getConfiguracoesMetasEquipe().isEmpty())
				|| (cicloConfiguracao.getNaoParametrizarMetasDesempenhoIndividual() == true && !this.cicloConfiguracao.getConfiguracoesMetasIndividual()
						.isEmpty())
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
			if (configuracaoMeta.getQtdMinimaMetasIndividuaisPactuadasCiclo() > configuracaoMeta.getQtdMaximaMetasIndividuaisPactuadasCiclo()) {
				FacesUtil.addErrorMessage("form-equipes:qtdMinimasMetas",
						"A quantidade m\u00EDnima de metas n\u00E3o pode ser maior que a quantidade m\u00E1xima de metas.");
				FacesUtil.addErrorMessage("form-equipes:qtdMaximaMetas",
						"A quantidade m\u00E1xima de metas n\u00E3o pode ser menor que a quantidade m\u00EDnima de metas.");
				return;
			}

			if (this.configuracaoMeta.getUnidades() == null || this.configuracaoMeta.getUnidades().isEmpty()) {
				FacesUtil.addErrorMessage("form-equipes", "Informe no m\u00EDnimo uma Unidade.");
				return;
			}

			if (this.configuracaoMeta.getImpactaRemuneracaoVariavel()) {
				if (this.configuracaoMeta.getPorcentagensRemuneracaoVariavel() == null || this.configuracaoMeta.getPorcentagensRemuneracaoVariavel().isEmpty()) {
					FacesUtil.addErrorMessage("form-equipes", "Gere a tabela antes de inserir o par\u00E2metro");
					return;
				}
				if (this.configuracaoMeta.getPorcentagensRemuneracaoVariavel().size() != getQtdCombinacoes()) {
					FacesUtil.addErrorMessage("form-equipes",
							"Tabela gerada incoerente com a quantidade m\u00EDnima e m\u00E1xima de metas, gere novamente a tabela.");
					return;
				}
			}
			verificaStatus();
			this.cicloConfiguracaoService.save(cicloConfiguracao);
			this.configuracaoMeta = new ConfiguracaoMetas();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
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
				FacesUtil.addErrorMessage("form-equipes:tblPorcentagens", "O n\u00FAmero m\u00E1ximo de metas deve ser maior ou igual o m\u00EDnimo de metas");
				return;
			}

			/*
			 * if (maxMetas.compareTo(50) > 0 || minMetas.compareTo(50) > 0) {
			 * FacesUtil .addErrorMessage( "form-equipes:tblPorcentagens",
			 * "Quantidade de combina\u00E7\u00F5es muito grande, reduza o n\u00FAmero de metas, esta a\u00E7\u00E3o pode ocasionar o travamento do sistema."
			 * ); return; }
			 */

			if (this.configuracaoMeta.getPorcentagensRemuneracaoVariavel() == null) {
				this.configuracaoMeta.setPorcentagensRemuneracaoVariavel(new ArrayList<PorcentagemRemuneracaoVariavel>());
			} else {
				this.configuracaoMeta.getPorcentagensRemuneracaoVariavel().clear();
			}

			for (int i = minMetas; i <= maxMetas; i++) {
				for (int x = 0; x <= i; x++) {
					PorcentagemRemuneracaoVariavel prv = new PorcentagemRemuneracaoVariavel(new PorcentagemRemuneracaoVariavelPK(i, x,
							this.configuracaoMeta.getId()));
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

			if (this.cicloConfiguracao.getConfiguracoesMetas() != null && !this.cicloConfiguracao.getConfiguracoesMetas().isEmpty()) {
				List<ConfiguracaoMetas> configuracaoMetas = this.cicloConfiguracao.getConfiguracoesMetas();
				List<Unidade> undiadesRemover = new ArrayList<Unidade>();
				for (ConfiguracaoMetas confMeta : configuracaoMetas) {
					if (confMeta.getTipoMeta() == TipoMeta.E)
						if (!confMeta.equals(this.configuracaoMeta)) {
							undiadesRemover.addAll(confMeta.getUnidades());
						}
				}
				unidadesDisponiveis.removeAll(undiadesRemover);
			}

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

			if (this.cicloConfiguracao.getConfiguracoesMetas() != null && !this.cicloConfiguracao.getConfiguracoesMetas().isEmpty()) {
				List<ConfiguracaoMetas> configuracaoMetas = this.cicloConfiguracao.getConfiguracoesMetas();
				List<Unidade> undiadesRemover = new ArrayList<Unidade>();
				for (ConfiguracaoMetas confMeta : configuracaoMetas) {
					if (confMeta.getTipoMeta() == TipoMeta.E)
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
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void desvincularUnidade() {
		try {
			this.getConfiguracaoMeta().getUnidades().remove(this.unidadeSelecionada);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararModalVincularUnidadesEditar() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());

			if (this.cicloConfiguracao.getConfiguracoesMetas() != null && !this.cicloConfiguracao.getConfiguracoesMetas().isEmpty()) {
				List<ConfiguracaoMetas> configuracaoMetas = this.cicloConfiguracao.getConfiguracoesMetas();
				List<Unidade> undiadesRemover = new ArrayList<Unidade>();
				for (ConfiguracaoMetas confMeta : configuracaoMetas) {
					if (confMeta.getTipoMeta() == TipoMeta.E)
						if (!confMeta.equals(this.configuracaoMeta)) {
							undiadesRemover.addAll(confMeta.getUnidades());
						}
				}
				unidadesDisponiveis.removeAll(undiadesRemover);
			}

			unidadesSelecionadasVincular = getConfiguracaoMeta().getUnidades();
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

	public ConfiguracaoMetas getConfiguracaoMetaVisualizacaoRv() {
		return configuracaoMetaVisualizacaoRv;
	}

	public void setConfiguracaoMetaVisualizacaoRv(ConfiguracaoMetas configuracaoMetaVisualizacaoRv) {
		this.configuracaoMetaVisualizacaoRv = configuracaoMetaVisualizacaoRv;
	}

}
