package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.service.AvaliadorCicloService;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ManterCicloBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ManterCicloBean.class);

	@Inject
	private CicloService cicloService;

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private AvaliadorCicloService avaliadorService;

	@Inject
	private AppBean appBean;

	private Ciclo ciclo = new Ciclo();

	private Boolean naoParametrizarMetasDesempenhoIndividual = Boolean.FALSE;
	private Boolean naoParametrizarMetasDesempenhoEquipe = Boolean.FALSE;
	
	private Boolean visualizando = Boolean.FALSE;
	private String itemLimpar;

	private TipoConfiguracaoCiclo tipoConfiguracaoCiclo;

	private CicloConfiguracao cicloConfiguracaoDesempenho;

	private CicloConfiguracao cicloConfiguracaoDesenvolvimento;

	private String aba = "desempenho";

	@Override
	public void init() {
		super.init();
		if (ciclo.getConfiguracoes() == null) {
			ciclo.setConfiguracoes(new ArrayList<CicloConfiguracao>());
			CicloConfiguracao ccDesempenho = new CicloConfiguracao();
			ccDesempenho.setTipoConfiguracao(TipoConfiguracaoCiclo.DESEMP);
			
			if (this.naoParametrizarMetasDesempenhoEquipe == true) {
				ccDesempenho.setNaoParametrizarMetasDesempenhoEquipe(new Boolean(true));
			} else {
				ccDesempenho.setNaoParametrizarMetasDesempenhoEquipe(new Boolean(false));
			} 
			
			if (this.naoParametrizarMetasDesempenhoIndividual == true) {
				ccDesempenho.setNaoParametrizarMetasDesempenhoIndividual(new Boolean(true));
			} else {
				ccDesempenho.setNaoParametrizarMetasDesempenhoIndividual(new Boolean(false));
			}
			
			if (this.naoParametrizarMetasDesempenhoEquipe == true && this.naoParametrizarMetasDesempenhoIndividual == true) {
				ccDesempenho.setNaoParametrizarMetasDesempenhoEquipe(new Boolean(true));
				ccDesempenho.setNaoParametrizarMetasDesempenhoIndividual(new Boolean(true));
			} else {
				ccDesempenho.setNaoParametrizarMetasDesempenhoEquipe(new Boolean(false));
				ccDesempenho.setNaoParametrizarMetasDesempenhoIndividual(new Boolean(false));
			}
			
			
			ccDesempenho.setCiclo(ciclo);
			ciclo.getConfiguracoes().add(ccDesempenho);
			cicloConfiguracaoDesempenho = ccDesempenho;

			CicloConfiguracao ccDesenvolvimento = new CicloConfiguracao();
			ccDesenvolvimento.setTipoConfiguracao(TipoConfiguracaoCiclo.DESENV);
			ccDesenvolvimento.setCiclo(ciclo);
			ciclo.getConfiguracoes().add(ccDesenvolvimento);
			cicloConfiguracaoDesenvolvimento = ccDesenvolvimento;
		}

		if (ciclo.getId() != null) {
			ciclo = cicloService.load(ciclo.getId());
		}
	}

	public Ciclo getCiclo() {
		return ciclo;
	}

	public void setCiclo(Ciclo ciclo) {
		if (ciclo != null) {
			ciclo = cicloService.load(ciclo.getId());
		}
		this.ciclo = ciclo;
	}

	public Boolean getVisualizando() {
		return visualizando;
	}

	public void setVisualizando(Boolean visualizando) {
		this.visualizando = visualizando;
	}

	public void limparConfiguracoes() {

		if ("datas".equals(this.itemLimpar) && tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESEMP) {
			if (this.getCicloConfiguracaoDesempenho().getCalendarios() != null) {
				this.getCicloConfiguracaoDesempenho().getCalendarios().clear();
				if (this.getCicloConfiguracaoDesempenho().getConfiguracaoAuditoria() != null) {
					this.getCicloConfiguracaoDesempenho().getConfiguracaoAuditoria().setDtFim(null);
					this.getCicloConfiguracaoDesempenho().getConfiguracaoAuditoria().setDtInicio(null);
				}
			}
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoCalendario(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de calend\u00E1rio reiniciada com sucesso!");
		} else if ("datas".equals(this.itemLimpar) && tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESENV) {
			if (this.getCicloConfiguracaoDesenvolvimento().getCalendarios() != null) {
				this.getCicloConfiguracaoDesenvolvimento().getCalendarios().clear();
			}
			this.getCicloConfiguracaoDesenvolvimento().setStatusConfiguracaoCalendario(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de calend\u00E1rio reiniciada com sucesso!");
		}

		if ("avaliadores".equals(this.itemLimpar) && tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESEMP) {
			avaliadorService.deleteByCicloConfiguracao(this.getCicloConfiguracaoDesempenho());

			if (this.getCicloConfiguracaoDesempenho().getAvaliadores() != null) {
				this.getCicloConfiguracaoDesempenho().getAvaliadores().clear();
			}
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoAvaliadores(StatusConfiguracao.N);
			this.getCicloConfiguracaoDesenvolvimento().setStatusConfiguracaoAvaliadores(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de avaliadores reiniciada com sucesso!");
		} else if ("avaliadores".equals(this.itemLimpar) && tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESENV) {
			avaliadorService.deleteByCicloConfiguracao(this.getCicloConfiguracaoDesenvolvimento());

			if (this.getCicloConfiguracaoDesenvolvimento().getAvaliadores() != null) {
				this.getCicloConfiguracaoDesenvolvimento().getAvaliadores().clear();
			}
			this.getCicloConfiguracaoDesenvolvimento().setStatusConfiguracaoAvaliadores(StatusConfiguracao.N);
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoAvaliadores(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de avaliadores reiniciada com sucesso!");
		}

		if ("metas_individuais_equipe_desenvolvimento".equals(this.itemLimpar)
				&& tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESEMP) {
			if (this.getCicloConfiguracaoDesempenho().getConfiguracoesMetas() != null) {
				this.getCicloConfiguracaoDesempenho().getConfiguracoesMetas().clear();
			}
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoMetas(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de metas individuais reiniciada com sucesso!");
		} else if ("metas_individuais_equipe_desenvolvimento".equals(this.itemLimpar)
				&& tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESENV) {
			if (this.getCicloConfiguracaoDesenvolvimento().getConfiguracoesMetas() != null) {
				this.getCicloConfiguracaoDesenvolvimento().getConfiguracoesMetas().clear();
			}
			this.getCicloConfiguracaoDesenvolvimento().setStatusConfiguracaoMetas(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de metas individuais reiniciada com sucesso!");
		}

		if ("emails".equals(this.itemLimpar) && tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESEMP) {
			if (this.getCicloConfiguracaoDesempenho().getConfiguracoesEmails() != null) {
				this.getCicloConfiguracaoDesempenho().getConfiguracoesEmails().clear();
			}
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoEmail(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de emails reiniciada com sucesso!");
		} else if ("emails".equals(this.itemLimpar) && tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESENV) {
			if (this.getCicloConfiguracaoDesenvolvimento().getConfiguracoesEmails() != null) {
				this.getCicloConfiguracaoDesenvolvimento().getConfiguracoesEmails().clear();
			}
			this.getCicloConfiguracaoDesenvolvimento().setStatusConfiguracaoEmail(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de emails reiniciada com sucesso!");
		}

		if ("exibicao_metas".equals(this.itemLimpar) && tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESEMP) {
			this.getCicloConfiguracaoDesempenho().setVisualizacaoMeta(null);
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoVisualizacaoMetas(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de exibi\u00E7\u00E3o de metas reiniciada com sucesso!");
		} else if ("exibicao_metas".equals(this.itemLimpar) && tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESENV) {
			this.getCicloConfiguracaoDesenvolvimento().setVisualizacaoMeta(null);
			this.getCicloConfiguracaoDesenvolvimento().setStatusConfiguracaoVisualizacaoMetas(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de exibi\u00E7\u00E3o de metas reiniciada com sucesso!");
		}

		if ("excessao_responsabilidades".equals(this.itemLimpar)
				&& tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESEMP) {
			if (this.getCicloConfiguracaoDesempenho().getExcecoesResponsabilidades() != null) {
				this.getCicloConfiguracaoDesempenho().getExcecoesResponsabilidades().clear();
			}
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoExcecaoResponsabilidades(StatusConfiguracao.N);
			FacesUtil
					.addInfoMessage("Configura\u00E7\u00E3o de exce\u00E7\u00E3o de responsabilidades reiniciada com sucesso!");
		} else if ("excessao_responsabilidades".equals(this.itemLimpar)
				&& tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESENV) {
			if (this.getCicloConfiguracaoDesenvolvimento().getExcecoesResponsabilidades() != null) {
				this.getCicloConfiguracaoDesenvolvimento().getExcecoesResponsabilidades().clear();
			}
			this.getCicloConfiguracaoDesenvolvimento().setStatusConfiguracaoExcecaoResponsabilidades(
					StatusConfiguracao.N);
			FacesUtil
					.addInfoMessage("Configura\u00E7\u00E3o de exce\u00E7\u00E3o de responsabilidades reiniciada com sucesso!");
		}

		if ("fluxo_aprovacao_metas_desempenho".equals(this.itemLimpar)
				&& tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESEMP) {
			if (this.getCicloConfiguracaoDesempenho().getFluxosAprovacaoMetas() != null) {
				this.getCicloConfiguracaoDesempenho().getFluxosAprovacaoMetas().clear();
			}
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoFluxoAprovacaoDesempenho(StatusConfiguracao.N);
			FacesUtil
					.addInfoMessage("Configura\u00E7\u00E3o de fluxo de aprova\u00E7\u00E3o de metas reiniciada com sucesso!");
		} else if ("fluxo_aprovacao_metas_desempenho".equals(this.itemLimpar)
				&& tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESENV) {
			if (this.getCicloConfiguracaoDesenvolvimento().getFluxosAprovacaoMetas() != null) {
				this.getCicloConfiguracaoDesenvolvimento().getFluxosAprovacaoMetas().clear();
			}
			this.getCicloConfiguracaoDesenvolvimento().setStatusConfiguracaoFluxoAprovacaoDesempenho(
					StatusConfiguracao.N);
			FacesUtil
					.addInfoMessage("Configura\u00E7\u00E3o de fluxo de aprova\u00E7\u00E3o de metas reiniciada com sucesso!");
		}

		if ("marcos_criticos".equals(this.itemLimpar)) {
			if (this.getCicloConfiguracaoDesenvolvimento().getConfiguracoesMarcoCritico() != null) {
				this.getCicloConfiguracaoDesenvolvimento().getConfiguracoesMarcoCritico().clear();
			}
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoMarcosCriticos(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de marcos cr\u00EDticos reiniciada com sucesso!");
		}

		if ("auditoria".equals(this.itemLimpar)) {
			this.getCicloConfiguracaoDesempenho().setConfiguracaoAuditoria(null);
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoAuditoria(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de auditoria reiniciada com sucesso!");
		}

		if ("avaliacao_comite".equals(this.itemLimpar) && tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESEMP) {
			this.getCicloConfiguracaoDesempenho().setConfiguracaoComite(null);
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoComite(StatusConfiguracao.N);
			FacesUtil
					.addInfoMessage("Configura\u00E7\u00E3o de avalia\u00E7\u00E3o de comit\u00EA reiniciada com sucesso!");
		} else if ("avaliacao_comite".equals(this.itemLimpar) && tipoConfiguracaoCiclo == TipoConfiguracaoCiclo.DESENV) {
			this.getCicloConfiguracaoDesenvolvimento().setConfiguracaoComite(null);
			this.getCicloConfiguracaoDesenvolvimento().setStatusConfiguracaoComite(StatusConfiguracao.N);
			FacesUtil
					.addInfoMessage("Configura\u00E7\u00E3o de avalia\u00E7\u00E3o de comit\u00EA reiniciada com sucesso!");
		}

		if ("forma_aquisicao".equals(this.itemLimpar)) {
			this.getCicloConfiguracaoDesenvolvimento().setConfiguracaoFormaAquisicao(null);
			this.getCicloConfiguracaoDesenvolvimento().setStatusConfiguracaoFormaAquisicao(StatusConfiguracao.N);
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o de forma de aquisi\u00E7\u00E3o reiniciada com sucesso!");
		}

		try {
			this.ciclo.setStatusConfiguracao(StatusConfiguracao.N);
			this.cicloConfiguracaoService.save(getCicloConfiguracaoDesempenho());
			this.cicloConfiguracaoService.save(getCicloConfiguracaoDesenvolvimento());
			this.cicloService.save(ciclo);
		} catch (ValidateException e) {
			FacesUtil.addErrorMessage(e.getMsgErrors());
		}
	}

	public String getItemLimpar() {
		return itemLimpar;
	}

	public void setItemLimpar(String itemLimpar) {
		this.itemLimpar = itemLimpar;
	}

	public String salvar() {
		try {

			List<Ciclo> ciclosAnoAtual = this.cicloService.findByVigenciaUF(this.ciclo.getVigencia(), this.ciclo.getUf());

			if (!ciclosAnoAtual.isEmpty()) {
				FacesUtil.addWarnMessage("form_manter_ciclo:anoCiclo",
						"J\u00E1 existe um ciclo cadastrado no sistema para o Ano e UF informados.");
				return null;
			}
			this.ciclo.verificaStatusConfiguracaoGlobal();
			
			cicloService.save(this.ciclo);
			FacesUtil.addInfoMessage("Ciclo salvo com sucesso.");
			return "listar.xhtml";
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public String salvarEdicoes() {
		try {
			List<Ciclo> ciclosAnoAtual = this.cicloService.findByVigenciaUF(this.ciclo.getVigencia(), this.ciclo.getUf());
			this.ciclo.verificaStatusConfiguracaoGlobal();
			
			cicloService.save(this.ciclo);
			FacesUtil.addInfoMessage("Ciclo salvo com sucesso.");
			return "listar.xhtml";
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public TipoConfiguracaoCiclo getTipoConfiguracaoCiclo() {
		return tipoConfiguracaoCiclo;
	}

	public void setTipoConfiguracaoCiclo(TipoConfiguracaoCiclo tipoConfiguracaoCiclo) {
		this.tipoConfiguracaoCiclo = tipoConfiguracaoCiclo;
	}

	public CicloConfiguracao getCicloConfiguracaoDesempenho() {
		if (this.cicloConfiguracaoDesempenho == null) {
			this.cicloConfiguracaoDesempenho = this.ciclo.getConfiguracaoByTipo(TipoConfiguracaoCiclo.DESEMP);
			if (this.cicloConfiguracaoDesempenho != null) {
				this.cicloConfiguracaoDesempenho = this.cicloConfiguracaoService.load(this.cicloConfiguracaoDesempenho
						.getId());
			}
		}
		return cicloConfiguracaoDesempenho;
	}

	public void setCicloConfiguracaoDesempenho(CicloConfiguracao cicloConfiguracaoDesempenho) {
		this.cicloConfiguracaoDesempenho = cicloConfiguracaoDesempenho;
	}

	public CicloConfiguracao getCicloConfiguracaoDesenvolvimento() {
		if (this.cicloConfiguracaoDesenvolvimento == null) {
			this.cicloConfiguracaoDesenvolvimento = this.ciclo.getConfiguracaoByTipo(TipoConfiguracaoCiclo.DESENV);
			if (this.cicloConfiguracaoDesenvolvimento != null) {
				this.cicloConfiguracaoDesenvolvimento = this.cicloConfiguracaoService
						.load(this.cicloConfiguracaoDesenvolvimento.getId());
			}
		}
		return cicloConfiguracaoDesenvolvimento;
	}

	public void setCicloConfiguracaoDesenvolvimento(CicloConfiguracao cicloConfiguracaoDesenvolvimento) {
		this.cicloConfiguracaoDesenvolvimento = cicloConfiguracaoDesenvolvimento;
	}

	public String getAba() {
		return aba;
	}

	public void setAba(String aba) {
		this.aba = aba;
	}

	public void setarAbaDesempenho() {
		this.aba = "desempenho";
	}

	public void setarAbaDesenvolvimento() {
		this.aba = "desenvolvimento";
	}

	public List<UF> getUfs() {
		return Arrays.asList(UF.values());
	}

	public void verificarCiclo() {
		List<Ciclo> ciclosAnoAtual = this.cicloService.findByVigenciaUF(this.ciclo.getVigencia(), this.ciclo.getUf());

		if (!ciclosAnoAtual.isEmpty()) {
			FacesUtil.addWarnMessage("form_manter_ciclo:anoCiclo",
					"J\u00E1 existe um ciclo cadastrado no sistema para o Ano e UF informados.");
		}
	}

	private void reset() {
		if (ciclo.getId() != null) {
			ciclo = cicloService.load(ciclo.getId());
		}
		setCicloConfiguracaoDesempenho(null);
		setCicloConfiguracaoDesenvolvimento(null);

	}
	
	public void naoParametrizarMetasDesempenhoEquipe() {
		alterouNaoParametrizarMetasDesempenhoEquipe();
	}

	public void naoParametrizarMetasDesempenhoIndividual() {
		alterouNaoParametrizarMetasDesempenhoIndividual();
	}

	public void alterouNaoParametrizarMetasDesempenhoEquipe() {
		if (this.getCicloConfiguracaoDesempenho().getConfiguracoesMetas() != null) {
			this.getCicloConfiguracaoDesempenho().getConfiguracoesMetas().clear();
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoMetas(StatusConfiguracao.N);
		}
		if (naoParametrizarMetasDesempenhoEquipe == true) {
			naoParametrizarMetasDesempenhoEquipe = Boolean.FALSE;
		} else {
			naoParametrizarMetasDesempenhoEquipe = Boolean.TRUE;
		}
		
		if (naoParametrizarMetasDesempenhoEquipe == true && naoParametrizarMetasDesempenhoIndividual == true) {
			cicloConfiguracaoDesempenho.setStatusConfiguracaoMetas(StatusConfiguracao.C);
		}
	}

	public void alterouNaoParametrizarMetasDesempenhoIndividual() {
		if (this.getCicloConfiguracaoDesempenho().getConfiguracoesMetas() != null) {
			this.getCicloConfiguracaoDesempenho().getConfiguracoesMetas().clear();
			this.getCicloConfiguracaoDesempenho().setStatusConfiguracaoMetas(StatusConfiguracao.N);
		}
		if (naoParametrizarMetasDesempenhoIndividual == true) {
			naoParametrizarMetasDesempenhoIndividual = Boolean.FALSE;
		} else {
			naoParametrizarMetasDesempenhoIndividual = Boolean.TRUE;
		}
		
		if (naoParametrizarMetasDesempenhoEquipe == true && naoParametrizarMetasDesempenhoIndividual == true) {
			cicloConfiguracaoDesempenho.setStatusConfiguracaoMetas(StatusConfiguracao.C);
		}
	}

	public Boolean getNaoParametrizarMetasDesempenhoEquipe() {
		return naoParametrizarMetasDesempenhoEquipe;
	}

	public Boolean getNaoParametrizarMetasDesempenhoIndividual() {
		return naoParametrizarMetasDesempenhoIndividual;
	}
	
}
