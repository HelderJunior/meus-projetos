package br.com.sebrae.sgm.model.audit;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import br.com.sebrae.sgm.model.audit.listener.AuditListener;
import br.com.sebrae.sgm.model.enums.TipoLog;

@Entity
@Table(name = "REVISAO")
@RevisionEntity(AuditListener.class)
public class Revisao implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@RevisionNumber
	@Column(name = "ID")
	private int id;

	@RevisionTimestamp
	@Column(name = "DATA_HORA_ALTERACAO")
	private long dataHoraAlteracao;

	@Column(name = "USUARIO")
	private String usuario;

	/*
	 * @ElementCollection
	 * 
	 * @JoinTable(name = "ENTIDADES_ALTERADAS", joinColumns = @JoinColumn(name = "REVISAO"))
	 * 
	 * @Column(name = "NOME_ENTIDADE")
	 * 
	 * @ModifiedEntityNames private Set<String> modifiedEntityNames;
	 */

	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_LOG")
	private TipoLog tipoLog;

	public Revisao() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getDataHoraAlteracao() {
		return dataHoraAlteracao;
	}

	public void setDataHoraAlteracao(long dataHoraAlteracao) {
		this.dataHoraAlteracao = dataHoraAlteracao;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/*
	 * public Set<String> getModifiedEntityNames() { return modifiedEntityNames; }
	 * 
	 * public void setModifiedEntityNames(Set<String> modifiedEntityNames) { this.modifiedEntityNames =
	 * modifiedEntityNames; }
	 */

	@Transient
	public Date getDataAlteracao() {
		return new Date(this.dataHoraAlteracao);
	}

	public TipoLog getTipoLog() {
		return tipoLog;
	}

	public void setTipoLog(TipoLog tipoLog) {
		this.tipoLog = tipoLog;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Revisao other = (Revisao) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
