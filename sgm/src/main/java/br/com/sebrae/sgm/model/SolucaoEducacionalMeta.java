package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;

import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.StatusSolucaoEducacional;
import br.com.sebrae.sgm.model.enums.VinculoOcupacional;

/**
 * 
 * @author Diego
 */
@Entity
@Table(name = "SOLUCAO_EDUCACIONAL_META")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "SolucaoEducacionalMeta.findAll", query = "SELECT s FROM SolucaoEducacionalMeta s"),
		@NamedQuery(name = "SolucaoEducacionalMeta.findByIdSolucaoEducacional", query = "SELECT s FROM SolucaoEducacionalMeta s WHERE s.solucaoEducacionalMetaPK.idSolucaoEducacional = :idSolucaoEducacional"),
		@NamedQuery(name = "SolucaoEducacionalMeta.findByIdMeta", query = "SELECT s FROM SolucaoEducacionalMeta s WHERE s.solucaoEducacionalMetaPK.idMeta = :idMeta"),
		@NamedQuery(name = "SolucaoEducacionalMeta.findByVinculoOcupacional", query = "SELECT s FROM SolucaoEducacionalMeta s WHERE s.vinculoOcupacional = :vinculoOcupacional"),
		@NamedQuery(name = "SolucaoEducacionalMeta.findByStatus", query = "SELECT s FROM SolucaoEducacionalMeta s WHERE s.status = :status"),
		@NamedQuery(name = "SolucaoEducacionalMeta.findByEnderecoPublicacaoComprovante", query = "SELECT s FROM SolucaoEducacionalMeta s WHERE s.enderecoPublicacaoComprovante = :enderecoPublicacaoComprovante"),
		@NamedQuery(name = "SolucaoEducacionalMeta.findByObservacao", query = "SELECT s FROM SolucaoEducacionalMeta s WHERE s.observacao = :observacao"),
		@NamedQuery(name = "SolucaoEducacionalMeta.findByJustificativaPendencia", query = "SELECT s FROM SolucaoEducacionalMeta s WHERE s.justificativaPendencia = :justificativaPendencia") })
@Audited
public class SolucaoEducacionalMeta implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected SolucaoEducacionalMetaPK solucaoEducacionalMetaPK;

	@Basic(optional = false)
	@Enumerated(EnumType.STRING)
	@Column(name = "VINCULO_OCUPACIONAL")
	private VinculoOcupacional vinculoOcupacional;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private StatusSolucaoEducacional status = StatusSolucaoEducacional.N;

	@Column(name = "ENDERECO_PUBLICACAO_COMPROVANTE")
	private String enderecoPublicacaoComprovante;

	@Column(name = "OBSERVACAO")
	private String observacao;

	@Column(name = "JUSTIFICATIVA_PENDENCIA")
	private String justificativaPendencia;

	@Column(name = "QUANTIDADE_ANTIGA")
	private BigDecimal quantidadeAntiga;

	@Column(name = "QUANTIDADE_PREVISTA")
	private BigDecimal quantidadePrevista;

	@Column(name = "QUANTIDADE_REALIZADA")
	private BigDecimal quantidadeRealizada;

	@OneToMany(mappedBy = "solucaoEducacionalMeta", cascade = CascadeType.ALL)
	private List<Anexo> anexos;

	@JoinColumn(name = "ID_META", referencedColumnName = "ID", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private Meta meta;

	@JoinColumn(name = "ID_SOLUCAO_EDUCACIONAL", referencedColumnName = "ID", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private SolucaoEducacional solucaoEducacional;

	@Transient
	private BigDecimal novaCargaHoraria;

	public SolucaoEducacionalMeta() {
	}

	public BigDecimal getPontuacaoPrevista() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.meta != null && this.getQuantidadePrevista() != null) {
			if (meta.getUnidade() != null) {
				List<ConfiguracaoMetas> cfMeta = this.meta.getCicloConfiguracao().getConfiguracoesMetasDesenvolvimento(
						meta.getUnidade());
				if (cfMeta != null && !cfMeta.isEmpty()) {
					ConfiguracaoMetas configuracaoMeta = cfMeta.get(0);
					if (this.getVinculoOcupacional() == VinculoOcupacional.C) {
						retorno = this.getQuantidadePrevista().multiply(configuracaoMeta.getCalculoHorasComplementar());
					} else {
						retorno = this.getQuantidadePrevista().multiply(configuracaoMeta.getCalculoHorasFundamental());
					}
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getPontuacaoRealizada() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.meta != null && this.quantidadeRealizada != null) {
			if (meta.getUnidade() != null) {
				List<ConfiguracaoMetas> cfMeta = this.meta.getCicloConfiguracao().getConfiguracoesMetasDesenvolvimento(
						meta.getUnidade());
				if (cfMeta != null && !cfMeta.isEmpty()) {
					ConfiguracaoMetas configuracaoMeta = cfMeta.get(0);
					if (this.getVinculoOcupacional() == VinculoOcupacional.C) {
						retorno = this.getQuantidadeRealizada()
								.multiply(configuracaoMeta.getCalculoHorasComplementar());
					} else {
						retorno = this.getQuantidadeRealizada().multiply(configuracaoMeta.getCalculoHorasFundamental());
					}
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getNovaPontuacao() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.meta != null && this.novaCargaHoraria != null) {
			if (meta.getUnidade() != null) {
				List<ConfiguracaoMetas> cfMeta = this.meta.getCicloConfiguracao().getConfiguracoesMetasDesenvolvimento(
						meta.getUnidade());
				if (cfMeta != null && !cfMeta.isEmpty()) {
					ConfiguracaoMetas configuracaoMeta = cfMeta.get(0);
					if (this.getVinculoOcupacional() == VinculoOcupacional.C) {
						retorno = this.novaCargaHoraria.multiply(configuracaoMeta.getCalculoHorasComplementar());
					} else {
						retorno = this.novaCargaHoraria.multiply(configuracaoMeta.getCalculoHorasFundamental());
					}
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public SolucaoEducacionalMeta(SolucaoEducacionalMetaPK solucaoEducacionalMetaPK) {
		this.solucaoEducacionalMetaPK = solucaoEducacionalMetaPK;
	}

	public SolucaoEducacionalMeta(int idSolucaoEducacional, int idMeta) {
		this.solucaoEducacionalMetaPK = new SolucaoEducacionalMetaPK(idSolucaoEducacional, idMeta);
	}

	public SolucaoEducacionalMetaPK getSolucaoEducacionalMetaPK() {
		return solucaoEducacionalMetaPK;
	}

	public void setSolucaoEducacionalMetaPK(SolucaoEducacionalMetaPK solucaoEducacionalMetaPK) {
		this.solucaoEducacionalMetaPK = solucaoEducacionalMetaPK;
	}

	public StatusSolucaoEducacional getStatus() {
		return status;
	}

	public void setStatus(StatusSolucaoEducacional status) {
		this.status = status;
	}

	public String getEnderecoPublicacaoComprovante() {
		return enderecoPublicacaoComprovante;
	}

	public void setEnderecoPublicacaoComprovante(String enderecoPublicacaoComprovante) {
		this.enderecoPublicacaoComprovante = enderecoPublicacaoComprovante;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getJustificativaPendencia() {
		return justificativaPendencia;
	}

	public void setJustificativaPendencia(String justificativaPendencia) {
		this.justificativaPendencia = justificativaPendencia;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		if (this.solucaoEducacionalMetaPK == null) {
			this.solucaoEducacionalMetaPK = new SolucaoEducacionalMetaPK();
		}
		this.solucaoEducacionalMetaPK.setIdMeta(meta.getId());
		this.meta = meta;
	}

	public SolucaoEducacional getSolucaoEducacional() {
		return solucaoEducacional;
	}

	public void setSolucaoEducacional(SolucaoEducacional solucaoEducacional) {
		if (this.solucaoEducacionalMetaPK == null) {
			this.solucaoEducacionalMetaPK = new SolucaoEducacionalMetaPK();
		}
		this.solucaoEducacionalMetaPK.setIdSolucaoEducacional(solucaoEducacional.getId());
		this.solucaoEducacional = solucaoEducacional;
	}

	public VinculoOcupacional getVinculoOcupacional() {
		return vinculoOcupacional;
	}

	public void setVinculoOcupacional(VinculoOcupacional vinculoOcupacional) {
		this.vinculoOcupacional = vinculoOcupacional;
	}

	public List<Anexo> getAnexos() {
		return anexos;
	}

	public void setAnexos(List<Anexo> anexos) {
		this.anexos = anexos;
	}

	public BigDecimal getQuantidadeAntiga() {
		return quantidadeAntiga;
	}

	public void setQuantidadeAntiga(BigDecimal quantidadeAntiga) {
		this.quantidadeAntiga = quantidadeAntiga;
	}

	public BigDecimal getQuantidadePrevista() {
		return quantidadePrevista;
	}

	public BigDecimal getQuantidadeAlterada() {
		if (this.quantidadeAntiga != null) {
			return this.quantidadePrevista;
		}
		return null;
	}

	public BigDecimal getQuantidadeAntigaAlterada() {
		if (this.quantidadeAntiga != null) {
			return this.quantidadeAntiga;
		}
		return quantidadePrevista;
	}

	public void setQuantidadePrevista(BigDecimal quantidadePrevista) {
		this.quantidadePrevista = quantidadePrevista;
	}

	public BigDecimal getQuantidadeRealizada() {
		return quantidadeRealizada;
	}

	public void setQuantidadeRealizada(BigDecimal quantidadeRealizada) {
		this.quantidadeRealizada = quantidadeRealizada;
	}

	public BigDecimal getNovaCargaHoraria() {
		return novaCargaHoraria;
	}

	public void setNovaCargaHoraria(BigDecimal novaCargaHoraria) {
		this.novaCargaHoraria = novaCargaHoraria;
	}

	@PrePersist
	public void updateChave() {
		if (this.solucaoEducacionalMetaPK != null) {
			this.solucaoEducacionalMetaPK.setIdMeta(this.meta.getId());
			this.solucaoEducacionalMetaPK.setIdSolucaoEducacional(this.solucaoEducacional.getId());
		}
	}

	@Override
	public SolucaoEducacionalMeta clone() throws CloneNotSupportedException {

		SolucaoEducacionalMeta retorno = new SolucaoEducacionalMeta();
		retorno.setSolucaoEducacionalMetaPK(this.getSolucaoEducacionalMetaPK());
		retorno.setAnexos(this.getAnexos());
		retorno.setEnderecoPublicacaoComprovante(this.getEnderecoPublicacaoComprovante());
		retorno.setJustificativaPendencia(this.getJustificativaPendencia());
		retorno.setMeta(this.getMeta());
		retorno.setObservacao(this.getObservacao());
		retorno.setSolucaoEducacional(this.getSolucaoEducacional());
		retorno.setStatus(this.getStatus());
		retorno.setVinculoOcupacional(this.getVinculoOcupacional());
		retorno.setQuantidadeAntiga(this.getQuantidadeAntiga());
		retorno.setQuantidadePrevista(this.getQuantidadePrevista());
		retorno.setQuantidadeRealizada(this.getQuantidadeRealizada());

		return retorno;
	}

	public boolean isPodeAdicionarAnexo() {
		return this.getStatus() == StatusSolucaoEducacional.V && this.getMeta().getStatusAtual() == StatusMeta.AS;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (solucaoEducacionalMetaPK != null ? solucaoEducacionalMetaPK.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SolucaoEducacionalMeta)) {
			return false;
		}
		SolucaoEducacionalMeta other = (SolucaoEducacionalMeta) object;
		if ((this.getSolucaoEducacionalMetaPK() == null && other.getSolucaoEducacionalMetaPK() != null)
				|| (this.getSolucaoEducacionalMetaPK() != null && !this.getSolucaoEducacionalMetaPK().equals(
						other.getSolucaoEducacionalMetaPK()))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.SolucaoEducacionalMeta[ solucaoEducacionalMetaPK="
				+ getSolucaoEducacionalMetaPK() + " ]";
	}

}
