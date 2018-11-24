package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "CONFIGURACAO_FORMA_AQUISICAO")
@XmlRootElement
public class ConfigurarFormaAquisicao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "VALIDA_PARA_CICLO")
	private Boolean validaParaCiclo = Boolean.TRUE;

	@JoinColumn(name = "ID", referencedColumnName = "ID", insertable = false, updatable = false)
	@OneToOne(optional = false)
	private CicloConfiguracao cicloConfiguracao;

	@OneToMany(mappedBy = "configuracaoFormaAquisicao", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Anexo> anexos;

	@JoinTable(name = "CONFIGURACAO_FORMA_AQUISICAO_ITEMS", joinColumns = { @JoinColumn(name = "ID_CONFIGURACAO_FORMA_AQUISICAO", referencedColumnName = "ID") }, inverseJoinColumns = { @JoinColumn(name = "ID_FORMA_AQUISICAO", referencedColumnName = "ID") })
	@ManyToMany
	private List<FormaAquisicao> formasAquisicao;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getValidaParaCiclo() {
		return validaParaCiclo;
	}

	public void setValidaParaCiclo(Boolean validaParaCiclo) {
		this.validaParaCiclo = validaParaCiclo;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public List<Anexo> getAnexos() {
		return anexos;
	}

	public void setAnexos(List<Anexo> anexos) {
		this.anexos = anexos;
	}

	public List<FormaAquisicao> getFormasAquisicao() {
		return formasAquisicao;
	}

	public void setFormasAquisicao(List<FormaAquisicao> formasAquisicao) {
		this.formasAquisicao = formasAquisicao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigurarFormaAquisicao other = (ConfigurarFormaAquisicao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
