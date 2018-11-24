package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoExcecao;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoMeta;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

@Entity
@Table(name = "CICLO_CONFIGURACAO")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "CicloConfiguracao.findAll", query = "SELECT c FROM CicloConfiguracao c order by c.ciclo.vigencia asc"),
		@NamedQuery(name = "CicloConfiguracao.findAllAndamento", query = "SELECT c FROM CicloConfiguracao c where c.ciclo.status <> 'F' order by c.ciclo.vigencia asc") })
public class CicloConfiguracao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "STATUS_CONFIGURACAO")
	@Enumerated(EnumType.STRING)
	private StatusConfiguracao statusConfiguracao = StatusConfiguracao.A;

	@Column(name = "TIPO_CONFIGURACAO")
	@Enumerated(EnumType.STRING)
	private TipoConfiguracaoCiclo tipoConfiguracao;

	@Column(name = "AVALIA_META_INDIVIDUAL_DIRETOR")
	private Boolean avaliarMetaIndividualDiretor = Boolean.FALSE;

	@Column(name = "IMPEDIR_REPACTUACAO_APOS_PREVISAO_CONCLUSAO_META")
	private Boolean impedirRepactuacaoAposPrevisaoConclusaoMeta = Boolean.FALSE;

	@Column(name = "NAO_PARAMETRIZAR_METAS_DESEMPENHO_INDIVIDUAL")
	private Boolean naoParametrizarMetasDesempenhoIndividual = Boolean.FALSE;

	@Column(name = "NAO_PARAMETRIZAR_METAS_DESEMPENHO_EQUIPE")
	private Boolean naoParametrizarMetasDesempenhoEquipe = Boolean.FALSE;

	@Column(name = "NAO_PARAMETRIZAR_METAS_DESENVOLVIMENTO")
	private Boolean naoParametrizarMetasDesenvolvimento = Boolean.FALSE;

	@Column(name = "CAMPO_VINCULO_PROJETO_OBRIGATORIO")
	private Boolean campoVinculoProjetoObrigatorio = Boolean.FALSE;

	@Column(name = "PERMITIR_INSERCAO_ANEXO_APOS_PRAZO_CONCLUSAO_META")
	private Boolean permitirInsercaoAnexoAposPrazoConcluzaoMeta = Boolean.FALSE;

	@Column(name = "ENVIAR_EMAIL_COM_PENDENCIA")
	private Boolean enviarEmailComPendencia = Boolean.FALSE;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_CONFIGURACAO_CALENDARIO")
	private StatusConfiguracao statusConfiguracaoCalendario = StatusConfiguracao.N;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_CONFIGURACAO_AVALIADORES")
	private StatusConfiguracao statusConfiguracaoAvaliadores = StatusConfiguracao.N;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_CONFIGURACAO_METAS")
	private StatusConfiguracao statusConfiguracaoMetas = StatusConfiguracao.N;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_CONFIGURACAO_EMAIL")
	private StatusConfiguracao statusConfiguracaoEmail = StatusConfiguracao.N;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_CONFIGURACAO_VISUALIZACAO_METAS")
	private StatusConfiguracao statusConfiguracaoVisualizacaoMetas = StatusConfiguracao.N;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_CONFIGURACAO_EXCECAO_RESPONSABILIDADES")
	private StatusConfiguracao statusConfiguracaoExcecaoResponsabilidades = StatusConfiguracao.N;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_CONFIGURACAO_FLUXO_APROVACAO_DESEMPENHO")
	private StatusConfiguracao statusConfiguracaoFluxoAprovacaoDesempenho = StatusConfiguracao.N;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_CONFIGURACAO_MARCOS_CRITICOS")
	private StatusConfiguracao statusConfiguracaoMarcosCriticos = StatusConfiguracao.N;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_CONFIGURACAO_AUDITORIA")
	private StatusConfiguracao statusConfiguracaoAuditoria = StatusConfiguracao.N;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_CONFIGURACAO_COMITE")
	private StatusConfiguracao statusConfiguracaoComite = StatusConfiguracao.N;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_CONFIGURACAO_FORMA_AQUISICAO")
	private StatusConfiguracao statusConfiguracaoFormaAquisicao = StatusConfiguracao.N;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao", orphanRemoval = true)
	private List<Calendario> calendarios;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao")
	private List<AvaliadorCiclo> avaliadores;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao")
	private List<ConfiguracaoMetas> configuracoesMetas;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao")
	private List<ConfiguracaoEmail> configuracoesEmails;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao")
	private VisualizacaoMeta visualizacaoMeta;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao")
	private List<ConfiguracaoMarcoCritico> configuracoesMarcoCritico;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao")
	private ConfiguracaoComite configuracaoComite;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao")
	private ConfiguracaoAuditoria configuracaoAuditoria;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao")
	private ConfigurarFormaAquisicao configuracaoFormaAquisicao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao")
	private List<ExcecaoResponsabilidades> excecoesResponsabilidades;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao")
	private List<FluxoAprovacaoMetas> fluxosAprovacaoMetas;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao")
	private List<FaseCicloConfiguracao> fasesCicloConfiguracao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cicloConfiguracao")
	private List<Exportacao> exportacoes;

	@OneToMany(mappedBy = "cicloConfiguracao")
	private List<Meta> metas;

	@JoinColumn(name = "ID_CICLO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private Ciclo ciclo;

	public CicloConfiguracao() {
	}

	public CicloConfiguracao(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ConfiguracaoAuditoria getConfiguracaoAuditoria() {
		return configuracaoAuditoria;
	}

	public void setConfiguracaoAuditoria(ConfiguracaoAuditoria configuracaoAuditoria) {
		this.configuracaoAuditoria = configuracaoAuditoria;
	}

	public List<ConfiguracaoMarcoCritico> getConfiguracoesMarcoCritico() {
		return configuracoesMarcoCritico;
	}

	public void setConfiguracoesMarcoCritico(List<ConfiguracaoMarcoCritico> configuracoesMarcoCritico) {
		this.configuracoesMarcoCritico = configuracoesMarcoCritico;
	}

	public Boolean getAvaliarMetaIndividualDiretor() {
		return avaliarMetaIndividualDiretor;
	}

	public void setAvaliarMetaIndividualDiretor(Boolean avaliarMetaIndividualDiretor) {
		this.avaliarMetaIndividualDiretor = avaliarMetaIndividualDiretor;
	}

	public Boolean getImpedirRepactuacaoAposPrevisaoConclusaoMeta() {
		return impedirRepactuacaoAposPrevisaoConclusaoMeta;
	}

	public void setImpedirRepactuacaoAposPrevisaoConclusaoMeta(Boolean impedirRepactuacaoAposPrevisaoConclusaoMeta) {
		this.impedirRepactuacaoAposPrevisaoConclusaoMeta = impedirRepactuacaoAposPrevisaoConclusaoMeta;
	}

	public Boolean getNaoParametrizarMetasDesempenhoIndividual() {
		return naoParametrizarMetasDesempenhoIndividual;
	}

	public void setNaoParametrizarMetasDesempenhoIndividual(Boolean naoParametrizarMetasDesempenhoIndividual) {
		this.naoParametrizarMetasDesempenhoIndividual = naoParametrizarMetasDesempenhoIndividual;
	}

	public Boolean getNaoParametrizarMetasDesempenhoEquipe() {
		return naoParametrizarMetasDesempenhoEquipe;
	}

	public List<FluxoAprovacaoMetas> getFluxosAprovacaoMetas() {
		return fluxosAprovacaoMetas;
	}

	public List<Exportacao> getExportacoes() {
		return exportacoes;
	}

	public void setExportacoes(List<Exportacao> exportacoes) {
		this.exportacoes = exportacoes;
	}

	public FluxoAprovacaoMetas getFluxoByUnidade(Unidade unidade) {
		if (this.fluxosAprovacaoMetas != null && !this.fluxosAprovacaoMetas.isEmpty()) {
			for (FluxoAprovacaoMetas fam : this.fluxosAprovacaoMetas) {
				if (fam.getUnidades().contains(unidade)) {
					return fam;
				}
			}
		}
		return null;
	}

	public void setFluxosAprovacaoMetas(List<FluxoAprovacaoMetas> fluxosAprovacaoMetas) {
		this.fluxosAprovacaoMetas = fluxosAprovacaoMetas;
	}

	public void setNaoParametrizarMetasDesempenhoEquipe(Boolean naoParametrizarMetasDesempenhoEquipe) {
		this.naoParametrizarMetasDesempenhoEquipe = naoParametrizarMetasDesempenhoEquipe;
	}

	public Boolean getCampoVinculoProjetoObrigatorio() {
		return campoVinculoProjetoObrigatorio;
	}

	public void setCampoVinculoProjetoObrigatorio(Boolean campoVinculoProjetoObrigatorio) {
		this.campoVinculoProjetoObrigatorio = campoVinculoProjetoObrigatorio;
	}

	public Boolean getPermitirInsercaoAnexoAposPrazoConcluzaoMeta() {
		return permitirInsercaoAnexoAposPrazoConcluzaoMeta;
	}

	public void setPermitirInsercaoAnexoAposPrazoConcluzaoMeta(Boolean permitirInsercaoAnexoAposPrazoConcluzaoMeta) {
		this.permitirInsercaoAnexoAposPrazoConcluzaoMeta = permitirInsercaoAnexoAposPrazoConcluzaoMeta;
	}
	
	public Boolean getEnviarEmailComPendencia() {
		return enviarEmailComPendencia;
	}

	public void setEnviarEmailComPendencia(Boolean enviarEmailComPendencia) {
		this.enviarEmailComPendencia = enviarEmailComPendencia;
	}

	public List<Calendario> getCalendarios() {
		return calendarios;
	}

	public void setCalendarios(List<Calendario> calendarios) {
		this.calendarios = calendarios;
	}

	public List<ConfiguracaoMetas> getConfiguracoesMetas() {
		return configuracoesMetas;
	}

	public VisualizacaoMeta getVisualizacaoMeta() {
		return visualizacaoMeta;
	}

	public void setVisualizacaoMeta(VisualizacaoMeta visualizacaoMeta) {
		this.visualizacaoMeta = visualizacaoMeta;
	}

	public List<ExcecaoResponsabilidades> getExcecoesResponsabilidades() {
		return excecoesResponsabilidades;
	}

	public ConfiguracaoComite getConfiguracaoComite() {
		return configuracaoComite;
	}

	public void setConfiguracaoComite(ConfiguracaoComite configuracaoComite) {
		this.configuracaoComite = configuracaoComite;
	}

	public List<Meta> getMetas() {
		return metas;
	}

	public void setMetas(List<Meta> metas) {
		this.metas = metas;
	}

	public List<ExcecaoResponsabilidades> getExcecoesResponsabilidadesMetasMonitoramento() {
		List<ExcecaoResponsabilidades> retorno = new ArrayList<ExcecaoResponsabilidades>();
		if (excecoesResponsabilidades != null && !excecoesResponsabilidades.isEmpty()) {
			retorno = Lists.newArrayList(Collections2.filter(excecoesResponsabilidades,
					new Predicate<ExcecaoResponsabilidades>() {
						@Override
						public boolean apply(ExcecaoResponsabilidades obs) {
							return obs.getTipoExcecao() == TipoExcecao.IMM;
						}
					}));
		}
		return retorno;
	}

	public List<ExcecaoResponsabilidades> getExcecoesResponsabilidadesValidarResultados() {
		List<ExcecaoResponsabilidades> retorno = new ArrayList<ExcecaoResponsabilidades>();
		if (excecoesResponsabilidades != null && !excecoesResponsabilidades.isEmpty()) {
			retorno = Lists.newArrayList(Collections2.filter(excecoesResponsabilidades,
					new Predicate<ExcecaoResponsabilidades>() {
						@Override
						public boolean apply(ExcecaoResponsabilidades obs) {
							return obs.getTipoExcecao() == TipoExcecao.VMR;
						}
					}));
		}
		return retorno;
	}

	public void setExcecoesResponsabilidades(List<ExcecaoResponsabilidades> excecoesResponsabilidades) {
		this.excecoesResponsabilidades = excecoesResponsabilidades;
	}

	public List<ConfiguracaoMetas> getConfiguracoesMetasEquipe() {
		List<ConfiguracaoMetas> retorno = new ArrayList<ConfiguracaoMetas>();
		if (configuracoesMetas != null && !configuracoesMetas.isEmpty()) {
			retorno = Lists.newArrayList(Collections2.filter(configuracoesMetas, new Predicate<ConfiguracaoMetas>() {
				@Override
				public boolean apply(ConfiguracaoMetas obs) {
					return obs.getTipoMeta() == TipoMeta.E;
				}
			}));
		}
		return retorno;
	}

	public List<ConfiguracaoMetas> getConfiguracoesMetasIndividual() {
		List<ConfiguracaoMetas> retorno = new ArrayList<ConfiguracaoMetas>();
		if (configuracoesMetas != null && !configuracoesMetas.isEmpty()) {
			retorno = Lists.newArrayList(Collections2.filter(configuracoesMetas, new Predicate<ConfiguracaoMetas>() {
				@Override
				public boolean apply(ConfiguracaoMetas obs) {
					return obs.getTipoMeta() == TipoMeta.I;
				}
			}));
		}
		return retorno;
	}

	public List<ConfiguracaoMetas> getConfiguracoesMetasDesenvolvimento() {
		List<ConfiguracaoMetas> retorno = new ArrayList<ConfiguracaoMetas>();
		if (configuracoesMetas != null && !configuracoesMetas.isEmpty()) {
			retorno = Lists.newArrayList(Collections2.filter(configuracoesMetas, new Predicate<ConfiguracaoMetas>() {
				@Override
				public boolean apply(ConfiguracaoMetas obs) {
					return obs.getTipoMeta() == TipoMeta.D;
				}
			}));
		}
		return retorno;
	}

	public List<ConfiguracaoMetas> getConfiguracoesMetasDesenvolvimento(final Unidade un) {
		List<ConfiguracaoMetas> retorno = new ArrayList<ConfiguracaoMetas>();
		if (configuracoesMetas != null && !configuracoesMetas.isEmpty()) {
			retorno = Lists.newArrayList(Collections2.filter(configuracoesMetas, new Predicate<ConfiguracaoMetas>() {
				@Override
				public boolean apply(ConfiguracaoMetas obs) {
					return obs.getTipoMeta() == TipoMeta.D && obs.getUnidades().contains(un);
				}
			}));
		}
		return retorno;
	}

	public void setConfiguracoesMetas(List<ConfiguracaoMetas> configuracoesMetas) {
		this.configuracoesMetas = configuracoesMetas;
	}

	public Calendario getCalendarioByTipo(TipoGrupo tipo) {
		Calendario retorno = null;
		if (this.calendarios != null && !this.calendarios.isEmpty()) {
			for (Calendario cal : this.calendarios) {
				if (cal.getTipo() == tipo) {
					retorno = cal;
				}
			}
		}
		return retorno;
	}

	public Calendario getCalendarioGerente() {
		Calendario retorno = null;
		if (this.calendarios != null && !this.calendarios.isEmpty()) {
			for (Calendario cal : this.calendarios) {
				if (cal.getTipo() == TipoGrupo.G) {
					retorno = cal;
				}
			}
		}
		return retorno;
	}

	public Calendario getCalendarioColaborador() {
		Calendario retorno = null;
		if (this.calendarios != null && !this.calendarios.isEmpty()) {
			for (Calendario cal : this.calendarios) {
				if (cal.getTipo() == TipoGrupo.C) {
					retorno = cal;
				}
			}
		}
		return retorno;
	}

	public Calendario getCalendarioEquipe() {
		Calendario retorno = null;
		if (this.calendarios != null && !this.calendarios.isEmpty()) {
			for (Calendario cal : this.calendarios) {
				if (cal.getTipo() == TipoGrupo.E) {
					retorno = cal;
				}
			}
		}
		return retorno;
	}

	public FaseCicloConfiguracao getFaseCicloGerente() {
		FaseCicloConfiguracao retorno = null;
		if (this.fasesCicloConfiguracao != null && !this.fasesCicloConfiguracao.isEmpty()) {
			for (FaseCicloConfiguracao fc : this.fasesCicloConfiguracao) {
				if (fc.getTipo() == TipoGrupo.G) {
					retorno = fc;
				}
			}
		}
		return retorno;
	}

	public FaseCicloConfiguracao getFaseCicloColaborador() {
		FaseCicloConfiguracao retorno = null;
		if (this.fasesCicloConfiguracao != null && !this.fasesCicloConfiguracao.isEmpty()) {
			for (FaseCicloConfiguracao fc : this.fasesCicloConfiguracao) {
				if (fc.getTipo() == TipoGrupo.C) {
					retorno = fc;
				}
			}
		}
		return retorno;
	}

	public FaseCicloConfiguracao getFaseCicloByTipo(TipoGrupo tc) {
		FaseCicloConfiguracao retorno = null;
		if (this.fasesCicloConfiguracao != null && !this.fasesCicloConfiguracao.isEmpty()) {
			for (FaseCicloConfiguracao fc : this.fasesCicloConfiguracao) {
				if (fc.getTipo() == tc) {
					retorno = fc;
				}
			}
		}
		return retorno;
	}

	public FaseCicloConfiguracao getFaseCicloEquipe() {
		FaseCicloConfiguracao retorno = null;
		if (this.fasesCicloConfiguracao != null && !this.fasesCicloConfiguracao.isEmpty()) {
			for (FaseCicloConfiguracao fc : this.fasesCicloConfiguracao) {
				if (fc.getTipo() == TipoGrupo.E) {
					retorno = fc;
				}
			}
		}
		return retorno;
	}

	public StatusConfiguracao getStatusConfiguracao() {
		return statusConfiguracao;
	}

	public void setStatusConfiguracao(StatusConfiguracao statusConfiguracao) {
		this.statusConfiguracao = statusConfiguracao;
	}

	public StatusCiclo getStatusCiclo() {
		return StatusCiclo.N;
	}

	public List<FaseCicloConfiguracao> getFasesCicloConfiguracao() {
		return fasesCicloConfiguracao;
	}

	public void setFasesCicloConfiguracao(List<FaseCicloConfiguracao> fasesCicloConfiguracao) {
		this.fasesCicloConfiguracao = fasesCicloConfiguracao;
	}

	public void adicionarFaseCicloConfiguracao(FaseCicloConfiguracao faseCicloConfiguracao) {
		if (this.fasesCicloConfiguracao == null) {
			this.fasesCicloConfiguracao = new ArrayList<FaseCicloConfiguracao>();
		}
		this.fasesCicloConfiguracao.add(faseCicloConfiguracao);
	}

	public StatusConfiguracao getStatusConfiguracaoCalendario() {
		return statusConfiguracaoCalendario;
	}

	public void setStatusConfiguracaoCalendario(StatusConfiguracao statusConfiguracaoCalendario) {
		this.statusConfiguracaoCalendario = statusConfiguracaoCalendario;
	}

	public StatusConfiguracao getStatusConfiguracaoAvaliadores() {
		return statusConfiguracaoAvaliadores;
	}

	public void setStatusConfiguracaoAvaliadores(StatusConfiguracao statusConfiguracaoAvaliadores) {
		this.statusConfiguracaoAvaliadores = statusConfiguracaoAvaliadores;
	}

	public StatusConfiguracao getStatusConfiguracaoMetas() {
		return statusConfiguracaoMetas;
	}

	public void setStatusConfiguracaoMetas(StatusConfiguracao statusConfiguracaoMetas) {
		this.statusConfiguracaoMetas = statusConfiguracaoMetas;
	}

	public StatusConfiguracao getStatusConfiguracaoEmail() {
		return statusConfiguracaoEmail;
	}

	public void setStatusConfiguracaoEmail(StatusConfiguracao statusConfiguracaoEmail) {
		this.statusConfiguracaoEmail = statusConfiguracaoEmail;
	}

	public StatusConfiguracao getStatusConfiguracaoVisualizacaoMetas() {
		return statusConfiguracaoVisualizacaoMetas;
	}

	public void setStatusConfiguracaoVisualizacaoMetas(StatusConfiguracao statusConfiguracaoVisualizacaoMetas) {
		this.statusConfiguracaoVisualizacaoMetas = statusConfiguracaoVisualizacaoMetas;
	}

	public StatusConfiguracao getStatusConfiguracaoExcecaoResponsabilidades() {
		return statusConfiguracaoExcecaoResponsabilidades;
	}

	public void setStatusConfiguracaoExcecaoResponsabilidades(
			StatusConfiguracao statusConfiguracaoExcecaoResponsabilidades) {
		this.statusConfiguracaoExcecaoResponsabilidades = statusConfiguracaoExcecaoResponsabilidades;
	}

	public StatusConfiguracao getStatusConfiguracaoFluxoAprovacaoDesempenho() {
		return statusConfiguracaoFluxoAprovacaoDesempenho;
	}

	public void setStatusConfiguracaoFluxoAprovacaoDesempenho(
			StatusConfiguracao statusConfiguracaoFluxoAprovacaoDesempenho) {
		this.statusConfiguracaoFluxoAprovacaoDesempenho = statusConfiguracaoFluxoAprovacaoDesempenho;
	}

	public StatusConfiguracao getStatusConfiguracaoMarcosCriticos() {
		return statusConfiguracaoMarcosCriticos;
	}

	public void setStatusConfiguracaoMarcosCriticos(StatusConfiguracao statusConfiguracaoMarcosCriticos) {
		this.statusConfiguracaoMarcosCriticos = statusConfiguracaoMarcosCriticos;
	}

	public StatusConfiguracao getStatusConfiguracaoAuditoria() {
		return statusConfiguracaoAuditoria;
	}

	public void setStatusConfiguracaoAuditoria(StatusConfiguracao statusConfiguracaoAuditoria) {
		this.statusConfiguracaoAuditoria = statusConfiguracaoAuditoria;
	}

	public StatusConfiguracao getStatusConfiguracaoComite() {
		return statusConfiguracaoComite;
	}

	public void setStatusConfiguracaoComite(StatusConfiguracao statusConfiguracaoComite) {
		this.statusConfiguracaoComite = statusConfiguracaoComite;
	}

	public StatusConfiguracao getStatusConfiguracaoFormaAquisicao() {
		return statusConfiguracaoFormaAquisicao;
	}

	public void setStatusConfiguracaoFormaAquisicao(StatusConfiguracao statusConfiguracaoFormaAquisicao) {
		this.statusConfiguracaoFormaAquisicao = statusConfiguracaoFormaAquisicao;
	}

	public List<AvaliadorCiclo> getAvaliadores() {
		return avaliadores;
	}

	public void setAvaliadores(List<AvaliadorCiclo> avaliadores) {
		this.avaliadores = avaliadores;
	}

	public List<ConfiguracaoEmail> getConfiguracoesEmails() {
		return configuracoesEmails;
	}

	public void setConfiguracoesEmails(List<ConfiguracaoEmail> configuracoesEmails) {
		this.configuracoesEmails = configuracoesEmails;
	}

	public int getNivelFaseAtual() {
		return 0;
	}

	public void verificaStatusConfiguracaoGlobal() {
		if (this.tipoConfiguracao == TipoConfiguracaoCiclo.DESEMP) {
			if (statusConfiguracaoCalendario == StatusConfiguracao.C
					&& statusConfiguracaoAvaliadores == StatusConfiguracao.C
					&& statusConfiguracaoMetas == StatusConfiguracao.C
					&& statusConfiguracaoEmail == StatusConfiguracao.C
					&& statusConfiguracaoVisualizacaoMetas == StatusConfiguracao.C
					// && statusConfiguracaoExcecaoResponsabilidades == StatusConfiguracao.C
					&& statusConfiguracaoFluxoAprovacaoDesempenho == StatusConfiguracao.C
					&& statusConfiguracaoMarcosCriticos == StatusConfiguracao.C
					&& statusConfiguracaoAuditoria == StatusConfiguracao.C
					&& statusConfiguracaoComite == StatusConfiguracao.C) {
				this.setStatusConfiguracao(StatusConfiguracao.C);
			} else {
				this.setStatusConfiguracao(StatusConfiguracao.A);
			}
		} else if (this.tipoConfiguracao == TipoConfiguracaoCiclo.DESENV) {
			if (statusConfiguracaoCalendario == StatusConfiguracao.C
					&& statusConfiguracaoAvaliadores == StatusConfiguracao.C
					&& statusConfiguracaoMetas == StatusConfiguracao.C
					&& statusConfiguracaoEmail == StatusConfiguracao.C
					&& statusConfiguracaoVisualizacaoMetas == StatusConfiguracao.C
					// && statusConfiguracaoExcecaoResponsabilidades == StatusConfiguracao.C
					&& statusConfiguracaoFluxoAprovacaoDesempenho == StatusConfiguracao.C
					&& statusConfiguracaoFormaAquisicao == StatusConfiguracao.C
					&& statusConfiguracaoComite == StatusConfiguracao.C) {
				this.setStatusConfiguracao(StatusConfiguracao.C);
			} else {
				this.setStatusConfiguracao(StatusConfiguracao.A);
			}
		}
	}

	public Ciclo getCiclo() {
		return ciclo;
	}

	public void setCiclo(Ciclo ciclo) {
		this.ciclo = ciclo;
	}

	public TipoConfiguracaoCiclo getTipoConfiguracao() {
		return tipoConfiguracao;
	}

	public void setTipoConfiguracao(TipoConfiguracaoCiclo tipoConfiguracao) {
		this.tipoConfiguracao = tipoConfiguracao;
	}

	public Boolean getNaoParametrizarMetasDesenvolvimento() {
		return naoParametrizarMetasDesenvolvimento;
	}

	public void setNaoParametrizarMetasDesenvolvimento(Boolean naoParametrizarMetasDesenvolvimento) {
		this.naoParametrizarMetasDesenvolvimento = naoParametrizarMetasDesenvolvimento;
	}

	public ConfigurarFormaAquisicao getConfiguracaoFormaAquisicao() {
		return configuracaoFormaAquisicao;
	}

	public void setConfiguracaoFormaAquisicao(ConfigurarFormaAquisicao configuracaoFormaAquisicao) {
		this.configuracaoFormaAquisicao = configuracaoFormaAquisicao;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof CicloConfiguracao)) {
			return false;
		}
		CicloConfiguracao other = (CicloConfiguracao) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Ciclo[ id=" + id + " ]";
	}

	@PostUpdate
	public void aposPersistir() throws Exception {
		if (this.getVisualizacaoMeta() != null && this.getVisualizacaoMeta().getId() == null && this.getId() != null) {
			this.getVisualizacaoMeta().setId(this.getId());
		}
	}

	@PreUpdate
	public void prePersist() {
		if (this.getVisualizacaoMeta() != null && this.getVisualizacaoMeta().getId() == null && this.getId() != null) {
			this.getVisualizacaoMeta().setId(this.getId());
		}
		verificaStatusConfiguracaoGlobal();
	}

	public Date getDataInicioPactuacao(TipoMeta tipoMeta, TipoGrupo tipoCalendario) {
		Date retorno = new Date();

		/*
		 * if (this.fasesAtivas != null && !this.fasesAtivas.isEmpty()) { FaseCiclo faseCiclo = new FaseCiclo(new
		 * FaseCicloPk(this.getId(), Fase.P));
		 * 
		 * if (fasesAtivas.contains(faseCiclo)) { int indice = fasesAtivas.indexOf(faseCiclo); return
		 * fasesAtivas.get(indice).getDataInicio(); } }
		 */
		return retorno;
	}

	public Date getDataFimPactuacao(TipoMeta tipoMeta, TipoGrupo tipoCalendario) {
		Date retorno = new Date();
		return retorno;
	}

	public Date getDataInicioRepactuacao(TipoMeta tipoMeta, TipoGrupo tipoCalendario) {
		Date retorno = new Date();
		return retorno;
	}

	public Date getDataFimRepactuacao(TipoMeta tipoMeta, TipoGrupo tipoCalendario) {
		Date retorno = new Date();
		return retorno;
	}

	public Date getDataInicioMonitoramento(TipoMeta tipoMeta, TipoGrupo tipoCalendario) {
		Date retorno = new Date();
		return retorno;
	}

	public Date getDataFimMonitoramento(TipoMeta tipoMeta, TipoGrupo tipoCalendario) {
		Date retorno = new Date();
		return retorno;
	}

	public Date getDataInicioValidacao(TipoMeta tipoMeta, TipoGrupo tipoCalendario) {
		Date retorno = new Date();
		return retorno;
	}

	public Date getDataFimValidacao(TipoMeta tipoMeta, TipoGrupo tipoCalendario) {
		Date retorno = new Date();
		return retorno;
	}

	public Date getDataInicioAuditoria(TipoMeta tipoMeta, TipoGrupo tipoCalendario) {
		Date retorno = new Date();
		return retorno;
	}

	public Date getDataFimAuditoria(TipoMeta tipoMeta, TipoGrupo tipoCalendario) {
		Date retorno = new Date();
		return retorno;
	}

	public boolean isPodeIniciar() {
		if (getStatusConfiguracaoCalendario() == StatusConfiguracao.C
				&& getStatusConfiguracaoAvaliadores() == StatusConfiguracao.C
				&& getStatusConfiguracaoMetas() == StatusConfiguracao.C
				&& getStatusConfiguracaoVisualizacaoMetas() == StatusConfiguracao.C
				&& getStatusConfiguracaoFluxoAprovacaoDesempenho() == StatusConfiguracao.C) {
			return true;
		}
		return false;
	}

}
