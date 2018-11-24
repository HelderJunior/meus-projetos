package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.Length;

import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.FormaCalculo;
import br.com.sebrae.sgm.model.enums.Mes;
import br.com.sebrae.sgm.model.enums.StatusEnvio;
import br.com.sebrae.sgm.model.enums.StatusExecucaoMeta;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.TipoCampoObservacao;
import br.com.sebrae.sgm.model.enums.TipoDado;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.model.enums.TipoObservacao;

/**
 * @author Diego
 */
@Entity
@Table(name = "META")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Meta.findAll", query = "SELECT m FROM Meta m"),
		@NamedQuery(name = "Meta.findById", query = "SELECT m FROM Meta m WHERE m.id = :id"),
		@NamedQuery(name = "Meta.findByTipo", query = "SELECT m FROM Meta m WHERE m.tipo = :tipo"),
		@NamedQuery(name = "Meta.findByResultadosEsperados", query = "SELECT m FROM Meta m WHERE m.resultadosEsperados = :resultadosEsperados"),
		@NamedQuery(name = "Meta.findByQuantidadePrevista", query = "SELECT m FROM Meta m WHERE m.quantidadePrevista = :quantidadePrevista"),
		@NamedQuery(name = "Meta.findByTipoDado", query = "SELECT m FROM Meta m WHERE m.tipoDado = :tipoDado"),
		@NamedQuery(name = "Meta.findByFormaCalculo", query = "SELECT m FROM Meta m WHERE m.formaCalculo = :formaCalculo"),
		@NamedQuery(name = "Meta.findByMesConclusao", query = "SELECT m FROM Meta m WHERE m.mesConclusao = :mesConclusao"),
		@NamedQuery(name = "Meta.findByUnidadeMedida", query = "SELECT m FROM Meta m WHERE m.unidadeMedida = :unidadeMedida") })
@Audited
public class Meta implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "CODIGO_META")
	private String codigo;

	@Column(name = "TIPO")
	@Enumerated(EnumType.STRING)
	private TipoMeta tipo;

	@Basic(optional = false)
	@Length(max = 1000, message = "O tamanho do campo Resultados Esperados n\u00E3o pode exceder 1000 caracteres")
	@Column(name = "RESULTADOS_ESPERADOS")
	private String resultadosEsperados;

	@Length(max = 1000, message = "O tamanho do campo Evidencia de Entrega n\u00E3o pode exceder 1000 caracteres")
	@Column(name = "EVIDENCIA_ENTREGA")
	private String evidenciaEntrega;

	@Length(max = 200, message = "O tamanho do campo Local de Armazenamento da Evidencia da Entrega n\u00E3o pode exceder 200 caracteres")
	@Column(name = "LOCAL_ARMAZENAMENTO_EVIDENCIA_ENTREGA")
	private String localArmazenamentoEvidenciaEntrega;

	@Length(max = 200, message = "O tamanho do campo Observa\u00E7\u00E3o de Monitoramento n\u00E3o pode exceder 200 caracteres")
	@Column(name = "OBSERVACAO_MONITORAMENTO")
	private String observacaoMonitoramento;

	@Length(max = 1000, message = "O tamanho do campo Justificativa n\u00E3o pode exceder 1000 caracteres")
	@Column(name = "JUSTIFICATIVA")
	private String justificativa;

	@Column(name = "QUANTIDADE_PREVISTA")
	private Integer quantidadePrevista;

	@Column(name = "TIPO_DADO")
	private TipoDado tipoDado;

	@Column(name = "FORMA_CALCULO")
	@Enumerated(EnumType.STRING)
	private FormaCalculo formaCalculo;

	@Column(name = "MES_CONCLUSAO")
	@Enumerated(EnumType.ORDINAL)
	private Mes mesConclusao;

	@Length(max = 500, message = "O tamanho do campo Unidade de Medida n\u00E3o pode exceder 500 caracteres")
	@Column(name = "UNIDADE_MEDIDA")
	private String unidadeMedida;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_EXECUCAO_META")
	private StatusExecucaoMeta statusExecucaoMeta;

	@Length(max = 1000, message = "O tamanho do campo Justificativa da Pend\u00EAncia n\u00E3o pode exceder 1000 caracteres")
	@Column(name = "JUSTIFICATIVA_PENDENCIA")
	private String justificativaPendencia;

	@Length(max = 1000, message = "O tamanho do campo Justificativa de N\u00E3o Valida\u00E7\u00E3o n\u00E3o pode exceder 1000 caracteres")
	@Column(name = "JUSTIFICATIVA_NAO_VALIDACAO")
	private String justificativanaoValidacao;

	@Column(name = "STATUS_ATUAL")
	@Enumerated(EnumType.STRING)
	private StatusMeta statusAtual;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinTable(name = "META_UNIDADE", joinColumns = { @JoinColumn(name = "ID_META", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "UF_UNIDADE", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO_UNIDADE", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToMany
	private List<Unidade> unidadesVinculadas;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinTable(name = "META_PROJETO", joinColumns = { @JoinColumn(name = "ID_META", referencedColumnName = "ID") }, inverseJoinColumns = { @JoinColumn(name = "ID_PROJETO", referencedColumnName = "PratifCod") })
	@ManyToMany
	private List<Projeto> projetosVinculados;

	@OneToMany(mappedBy = "meta", cascade = CascadeType.ALL)
	private List<MarcoCritico> marcosCriticos;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinColumn(name = "ID_INDICADOR", referencedColumnName = "ID")
	@ManyToOne
	private Indicador indicador;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinColumns({ @JoinColumn(name = "UF_UNIDADE_VINCULADA", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "CODIGO_UNIDADE_VINCULADA", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToOne
	private Unidade unidade;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinColumn(name = "ID_COLABORADOR", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private Usuario colaborador;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinColumn(name = "ID_CICLO_CONFIGURACAO", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	private CicloConfiguracao cicloConfiguracao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "meta", fetch = FetchType.EAGER)
	private List<Observacao> observacoes;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "meta")
	private List<ObservacaoCampo> observacoesCampo;

	@OneToMany(mappedBy = "meta", cascade = CascadeType.ALL)
	private List<Anexo> anexosEvidenciaEntrega;

	@OneToMany(mappedBy = "meta", cascade = CascadeType.ALL)
	private List<ItemAvaliadoComiteMeta> itemsAvaliacaoComite;

	@OneToMany(mappedBy = "meta", cascade = CascadeType.ALL)
	private List<MetaStatus> status;

	@OneToMany(mappedBy = "meta", cascade = CascadeType.ALL)
	private List<Entrega> entregas;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinTable(name = "META_COMPETENCIA", joinColumns = { @JoinColumn(name = "ID_META", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "COMPETENCIAID", referencedColumnName = "CompetenciaID"),
			@JoinColumn(name = "COLABORADORCPF", referencedColumnName = "ColaboradorCPF"),
			@JoinColumn(name = "COLABORADORMATRICULA", referencedColumnName = "ColaboradorMatricula"),
			@JoinColumn(name = "UNIDADEUF", referencedColumnName = "UnidadeUF") })
	@ManyToMany
	private List<RMCompetencia> competencias;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinTable(name = "META_OBJETIVO_ESTRATEGICO", joinColumns = { @JoinColumn(name = "ID_META", referencedColumnName = "ID") }, inverseJoinColumns = { @JoinColumn(name = "ID_OBJETIVO_ESTRATEGICO", referencedColumnName = "ID") })
	@ManyToMany
	private List<ObjetivoEstrategico> objetivosEstrategicos;

	//Necessario pois so assim o hibernate consegue ver os dois lados da associação.
	@JoinTable(name = "META_DESENVOLVIMENTO_META", joinColumns = { @JoinColumn(name = "ID_META_DESENVOLVIMENTO", referencedColumnName = "ID") }, inverseJoinColumns = { @JoinColumn(name = "ID_ID_META_DESEMPENHO", referencedColumnName = "ID")})
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Meta> metasInviduaisEquipeVinculadas;

	//Necessario pois so assim o hibernate consegue ver os dois lados da associação.
	@JoinTable(name = "META_DESENVOLVIMENTO_META", joinColumns = { @JoinColumn(name = "ID_ID_META_DESEMPENHO", referencedColumnName = "ID") }, inverseJoinColumns = { @JoinColumn(name = "ID_META_DESENVOLVIMENTO", referencedColumnName = "ID")})
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Meta> metasDesenvolvimento;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "meta")
	private List<SolucaoEducacionalMeta> solucoesEducacionais;

	@Transient
	private Boolean selecionado = Boolean.FALSE;

	@Transient
	private String observacaoTemp;

	public Meta() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public TipoMeta getTipo() {
		return tipo;
	}

	public void setTipo(TipoMeta tipo) {
		this.tipo = tipo;
	}

	public String getResultadosEsperados() {
		return resultadosEsperados;
	}

	public void setResultadosEsperados(String resultadosEsperados) {
		this.resultadosEsperados = resultadosEsperados;
	}

	public Integer getQuantidadePrevista() {
		return quantidadePrevista;
	}

	public void setQuantidadePrevista(Integer quantidadePrevista) {
		this.quantidadePrevista = quantidadePrevista;
	}

	public TipoDado getTipoDado() {
		return tipoDado;
	}

	public void setTipoDado(TipoDado tipoDado) {
		this.tipoDado = tipoDado;
	}

	public FormaCalculo getFormaCalculo() {
		return formaCalculo;
	}

	public void setFormaCalculo(FormaCalculo formaCalculo) {
		this.formaCalculo = formaCalculo;
	}

	public Mes getMesConclusao() {
		return mesConclusao;
	}

	public void setMesConclusao(Mes mesConclusao) {
		this.mesConclusao = mesConclusao;
	}

	public String getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(String unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public List<Unidade> getUnidadesVinculadas() {
		return unidadesVinculadas;
	}

	public void setUnidadesVinculadas(List<Unidade> unidadesVinculadas) {
		this.unidadesVinculadas = unidadesVinculadas;
	}

	public List<Projeto> getProjetosVinculados() {
		return projetosVinculados;
	}

	public void setProjetosVinculados(List<Projeto> projetosVinculados) {
		this.projetosVinculados = projetosVinculados;
	}

	public List<MarcoCritico> getMarcosCriticos() {
		return marcosCriticos;
	}

	public void setMarcosCriticos(List<MarcoCritico> marcosCriticos) {
		this.marcosCriticos = marcosCriticos;
	}

	public Indicador getIndicador() {
		return indicador;
	}

	public void setIndicador(Indicador indicador) {
		this.indicador = indicador;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Usuario getColaborador() {
		return colaborador;
	}

	public void setColaborador(Usuario colaborador) {
		this.colaborador = colaborador;
	}

	public List<Observacao> getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(List<Observacao> observacoes) {
		this.observacoes = observacoes;
	}

	public String getEvidenciaEntrega() {
		return evidenciaEntrega;
	}

	public void setEvidenciaEntrega(String evidenciaEntrega) {
		this.evidenciaEntrega = evidenciaEntrega;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getLocalArmazenamentoEvidenciaEntrega() {
		return localArmazenamentoEvidenciaEntrega;
	}

	public void setLocalArmazenamentoEvidenciaEntrega(String localArmazenamentoEvidenciaEntrega) {
		this.localArmazenamentoEvidenciaEntrega = localArmazenamentoEvidenciaEntrega;
	}

	public List<Anexo> getAnexosEvidenciaEntrega() {
		return anexosEvidenciaEntrega;
	}

	public void setAnexosEvidenciaEntrega(List<Anexo> anexosEvidenciaEntrega) {
		this.anexosEvidenciaEntrega = anexosEvidenciaEntrega;
	}

	public List<MetaStatus> getStatus() {
		return status;
	}

	public void setStatus(List<MetaStatus> status) {
		this.status = status;
	}

	public List<Entrega> getEntregas() {
		return entregas;
	}

	public void setEntregas(List<Entrega> entregas) {
		this.entregas = entregas;
	}

	public String getObservacaoMonitoramento() {
		return observacaoMonitoramento;
	}

	public void setObservacaoMonitoramento(String observacaoMonitoramento) {
		this.observacaoMonitoramento = observacaoMonitoramento;
	}

	public StatusExecucaoMeta getStatusExecucaoMeta() {
		return statusExecucaoMeta;
	}

	public void setStatusExecucaoMeta(StatusExecucaoMeta statusExecucaoMeta) {
		this.statusExecucaoMeta = statusExecucaoMeta;
	}

	public String getObservacaoTemp() {
		return observacaoTemp;
	}

	public void setObservacaoTemp(String observacaoTemp) {
		this.observacaoTemp = observacaoTemp;
	}

	public List<ItemAvaliadoComiteMeta> getItemsAvaliacaoComite() {
		return itemsAvaliacaoComite;
	}

	public void setItemsAvaliacaoComite(List<ItemAvaliadoComiteMeta> itemsAvaliacaoComite) {
		this.itemsAvaliacaoComite = itemsAvaliacaoComite;
	}

	private void gerarCodigoMeta() {
		if (StringUtils.isBlank(this.codigo)) {
			if (this.cicloConfiguracao != null && this.id != null) {
				codigo = "";
				codigo = codigo.concat(this.cicloConfiguracao.getCiclo().getUf().toString());
				codigo = codigo.concat(String.format("%03d", this.cicloConfiguracao.getId()));
				codigo = codigo.concat(String.format("%05d", this.getId()));
			}
		}
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public MetaStatus getMetaStatusByFase(Fase... fases) {
		MetaStatus retorno = null;
		for (Fase fase : fases) {
			if (this.status != null) {
				for (MetaStatus status : this.status) {
					if (status.getFase() == fase)
						retorno = status;
				}
			}
		}

		return retorno;
	}

	public MetaStatus getMetaStatusAtual() {
		MetaStatus retorno = null;
		if (this.status != null && !status.isEmpty()) {
			retorno = this.status.get(this.status.size() - 1);
		}
		return retorno;
	}

	public MetaStatus getMetaStatusByStatus(StatusMeta statusMeta) {
		MetaStatus retorno = null;
		if (this.status != null) {
			for (MetaStatus status : this.status) {
				if (status.getStatus() == statusMeta)
					retorno = status;
			}
		}

		return retorno;
	}

	public StatusMeta getStatusByFase(Fase... fases) {
		StatusMeta retorno = null;
		for (Fase fase : fases) {
			if (this.status != null) {
				for (MetaStatus status : this.status) {
					if (status.getFase() == fase)
						retorno = status.getStatus();
				}
			}
		}

		return retorno;
	}

	public StatusMeta getStatusAtual() {
		if (this.statusAtual != null) {
			return this.statusAtual;
		} else {
			if (this.status != null && !status.isEmpty()) {
				return this.status.get(this.status.size() - 1).getStatus();
			}
			return StatusMeta.SA;
		}
	}

	public StatusMeta getStatusMetaByStatus(StatusMeta statusMeta) {
		StatusMeta retorno = null;
		if (this.status != null) {
			for (MetaStatus status : this.status) {
				if (status.getStatus() == statusMeta)
					retorno = status.getStatus();
			}
		}

		return retorno;
	}

	public void adicionarMetaStatus(MetaStatus st) {
		if (this.status == null) {
			this.status = new ArrayList<MetaStatus>();
		}
		this.status.add(st);
	}

	public StatusMeta getStatusValidacao() {
		MetaStatus statusValidacao = getMetaStatusByFase(Fase.V);

		if (statusValidacao != null) {
			return statusValidacao.getStatus();
		}
		return StatusMeta.PA;
	}

	public List<Observacao> getObservacoesByTipo(TipoObservacao to, String tipoPerfilDestino) {
		List<Observacao> retorno = new ArrayList<Observacao>();
		if (this.observacoes != null) {
			for (Observacao obs : this.observacoes) {
				if (obs.getTipo() == to && tipoPerfilDestino.equals(obs.getChavePerfilDestino())
						&& obs.getStatus() == StatusEnvio.E)
					retorno.add(obs);
			}
		}
		return retorno;
	}

	public List<ObservacaoCampo> getObservacoesCampoByCampo(TipoCampoObservacao to) {
		List<ObservacaoCampo> retorno = new ArrayList<ObservacaoCampo>();
		if (this.observacoesCampo != null) {
			for (ObservacaoCampo obs : this.observacoesCampo) {
				if (obs.getCampo() == to)
					retorno.add(obs);
			}
		}
		return retorno;
	}

	public Observacao getObservacaoByUsuario(Usuario u) {
		if (this.observacoes != null && !this.observacoes.isEmpty()) {
			for (Observacao obs : this.observacoes) {
				if (obs.getRemetente() != null && obs.getRemetente().equals(u))
					return obs;
			}
		}

		return null;
	}

	@PostPersist
	@PreUpdate
	public void prePersist() {
		gerarCodigoMeta();
	}

	public Integer getPorcentagemConclusao() {
		BigDecimal retorno = BigDecimal.ZERO;

		if (this.formaCalculo == FormaCalculo.C) {
			BigDecimal qtdPrevista = BigDecimal.valueOf(this.quantidadePrevista);
			BigDecimal valorAcumulado = BigDecimal.ZERO;

			if (this.entregas != null) {
				for (Entrega e : this.entregas) {
					if (e.getValor() != null)
						valorAcumulado = valorAcumulado.add(e.getValor());
				}
			}

			retorno = valorAcumulado.divide(qtdPrevista, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.00));
		}

		if (this.formaCalculo == FormaCalculo.N) {
			BigDecimal qtdPrevista = BigDecimal.valueOf(this.quantidadePrevista);
			BigDecimal valor = BigDecimal.ZERO;

			if (this.entregas != null) {
				for (Entrega e : this.entregas) {
					if (e.getValor() != null)
						valor = e.getValor();
				}
			}

			retorno = valor.divide(qtdPrevista, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.00));
		}

		if (this.formaCalculo == FormaCalculo.M) {
			BigDecimal qtdPrevista = BigDecimal.valueOf(this.quantidadePrevista);
			BigDecimal valorAcumulado = BigDecimal.ZERO;
			int mesesPrenchidos = 0;

			if (this.entregas != null) {
				for (Entrega e : this.entregas) {
					if (e.getValor() != null) {
						valorAcumulado = valorAcumulado.add(e.getValor());
						mesesPrenchidos++;
					}
				}
			}

			if (mesesPrenchidos > 0) {
				BigDecimal valorMedia = valorAcumulado.divide(BigDecimal.valueOf(mesesPrenchidos), 2,
						RoundingMode.HALF_UP);
				retorno = valorMedia.divide(qtdPrevista, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.00));
			}
		}

		if (retorno.compareTo(BigDecimal.valueOf(100.00)) > 0) {
			retorno = BigDecimal.valueOf(100.00);
		}

		return retorno.intValue();
	}

	public Integer getValorRealizado() {
		BigDecimal retorno = BigDecimal.ZERO;

		if (this.formaCalculo == FormaCalculo.C) {
			BigDecimal valorAcumulado = BigDecimal.ZERO;

			if (this.entregas != null) {
				for (Entrega e : this.entregas) {
					if (e.getValor() != null)
						valorAcumulado = valorAcumulado.add(e.getValor());
				}
			}
			retorno = valorAcumulado;
		}

		if (this.formaCalculo == FormaCalculo.N) {
			BigDecimal valor = BigDecimal.ZERO;

			if (this.entregas != null) {
				for (Entrega e : this.entregas) {
					if (e.getValor() != null)
						valor = e.getValor();
				}
			}
			retorno = valor;
		}

		if (this.formaCalculo == FormaCalculo.M) {
			BigDecimal valorAcumulado = BigDecimal.ZERO;
			int mesesPrenchidos = 0;

			if (this.entregas != null) {
				for (Entrega e : this.entregas) {
					if (e.getValor() != null) {
						valorAcumulado = valorAcumulado.add(e.getValor());
						mesesPrenchidos++;
					}
				}
			}

			if (mesesPrenchidos > 0) {
				BigDecimal valorMedia = valorAcumulado.divide(BigDecimal.valueOf(mesesPrenchidos), 0,
						RoundingMode.HALF_UP);
				retorno = valorMedia;
			}
		}
		return retorno.intValue();
	}

	public List<ObservacaoCampo> getObservacoesCampo() {
		return observacoesCampo;
	}

	public void setObservacoesCampo(List<ObservacaoCampo> observacoesCampo) {
		this.observacoesCampo = observacoesCampo;
	}

	public List<ObservacaoCampo> getObservacoesCampoByTipoAndCampo(TipoObservacao to, TipoCampoObservacao campo) {
		List<ObservacaoCampo> retorno = new ArrayList<ObservacaoCampo>();

		return retorno;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public List<RMCompetencia> getCompetencias() {
		return competencias;
	}

	public void setCompetencias(List<RMCompetencia> competencias) {
		this.competencias = competencias;
	}

	public List<Meta> getMetasInviduaisEquipeVinculadas() {
		return metasInviduaisEquipeVinculadas;
	}

	public void setMetasInviduaisEquipeVinculadas(List<Meta> metasInviduaisEquipeVinculadas) {
		this.metasInviduaisEquipeVinculadas = metasInviduaisEquipeVinculadas;
	}

	public List<SolucaoEducacionalMeta> getSolucoesEducacionais() {
		return solucoesEducacionais;
	}

	public List<Meta> getMetasDesenvolvimento() {
		return metasDesenvolvimento;
	}

	public void setMetasDesenvolvimento(List<Meta> metasDesenvolvimento) {
		this.metasDesenvolvimento = metasDesenvolvimento;
	}

	public void setSolucoesEducacionais(List<SolucaoEducacionalMeta> solucoesEducacionais) {
		this.solucoesEducacionais = solucoesEducacionais;
	}

	public List<ObjetivoEstrategico> getObjetivosEstrategicos() {
		return objetivosEstrategicos;
	}

	public void setObjetivosEstrategicos(List<ObjetivoEstrategico> objetivosEstrategicos) {
		this.objetivosEstrategicos = objetivosEstrategicos;
	}

	public String getJustificativaPendencia() {
		return justificativaPendencia;
	}

	public void setJustificativaPendencia(String justificativaPendencia) {
		this.justificativaPendencia = justificativaPendencia;
	}

	public String getJustificativanaoValidacao() {
		return justificativanaoValidacao;
	}

	public void setJustificativanaoValidacao(String justificativanaoValidacao) {
		this.justificativanaoValidacao = justificativanaoValidacao;
	}

	public void setStatusAtual(StatusMeta statusAtual) {
		this.statusAtual = statusAtual;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Meta other = (Meta) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Meta [id=" + getId() + "]";
	}

}
