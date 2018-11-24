package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "PERFIL")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Perfil.findAll", query = "SELECT p FROM Perfil p"),
		@NamedQuery(name = "Perfil.findByChave", query = "SELECT p FROM Perfil p WHERE p.chave = :chave"),
		@NamedQuery(name = "Perfil.findByDescricao", query = "SELECT p FROM Perfil p WHERE p.descricao = :descricao") })
public class Perfil implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String AUDITOR = "ROLE_AUDITOR";
	public static final String COMITE = "ROLE_COMITE";
	public static final String AVALIADOR_EXTERNO = "ROLE_AVALIADOR_EXT";

	public static final String ADM_MASTER = "ROLE_ADM_MASTER";
	public static final String ADM_UF_NACIONAL = "ROLE_ADM_UF_NAC";

	public static final String GERENTE = "ROLE_GERENTE";
	public static final String GERENTE_ADJUNTO = "ROLE_GERENTE_ADJ";
	public static final String DIRETOR = "ROLE_DIRETOR";
	public static final String ASSESSOR = "ROLE_ASSESSOR";
	public static final String CHEFE_GABINETE = "ROLE_CHEFE_GAB";

	public static final String COLABORADOR_CHAVE = "ROLE_COLABORADOR";
	public static final String USUARIO_EXTERNO_CHAVE = "ROLE_USUARIO_EXTERNO";

	public static final String[] PERFIS_CHEFIA = new String[] { GERENTE, GERENTE_ADJUNTO, DIRETOR, ASSESSOR,
			CHEFE_GABINETE };

	public static final String[] PERFIS_CHEFIA_COLABORADOR = new String[] { GERENTE, GERENTE_ADJUNTO, DIRETOR,
			ASSESSOR, CHEFE_GABINETE, COLABORADOR_CHAVE };

	public static final String[] PERFIS_USUARIO_EXTERNO = new String[] { AUDITOR, COMITE, AVALIADOR_EXTERNO, USUARIO_EXTERNO_CHAVE };
	
	public static final String[] CONFIGURAR_PERFIS_USUARIO_EXTERNO = new String[] { AUDITOR, COMITE, USUARIO_EXTERNO_CHAVE };

	public static final String[] PERFIS_AVALIADORES_EXTERNO = new String[] { AUDITOR, COMITE, AVALIADOR_EXTERNO };
	
	public static final String[] PERFIS_AVALIADORES_EXTERNO_EXCETO_AVALIADOR_EXTERNO = new String[] { AUDITOR, COMITE };

	public static final String[] PERFIS_CHEFIA_USUARIO_EXTERNO = new String[] { GERENTE, GERENTE_ADJUNTO, DIRETOR,
			ASSESSOR, CHEFE_GABINETE, AUDITOR, COMITE, AVALIADOR_EXTERNO, COLABORADOR_CHAVE };

	@Id
	@Basic(optional = false)
	@Column(name = "CHAVE")
	private String chave;

	@Column(name = "DESCRICAO")
	private String descricao;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "perfil")
	private List<UsuarioPerfil> usuarioPerfilList;

	@ManyToMany(mappedBy = "perfil")
	private List<PropriedadesUsuarioExterno> propriedadesUsuarioExterno;

	public Perfil() {
	}

	public Perfil(String chave) {
		this.chave = chave;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@XmlTransient
	public List<UsuarioPerfil> getUsuarioPerfilList() {
		return usuarioPerfilList;
	}

	public void setUsuarioPerfilList(List<UsuarioPerfil> usuarioPerfilList) {
		this.usuarioPerfilList = usuarioPerfilList;
	}

	public List<PropriedadesUsuarioExterno> getPropriedadesUsuarioExterno() {
		return propriedadesUsuarioExterno;
	}

	public void setPropriedadesUsuarioExterno(List<PropriedadesUsuarioExterno> propriedadesUsuarioExterno) {
		this.propriedadesUsuarioExterno = propriedadesUsuarioExterno;
	}

	public boolean isExterno() {
		return Arrays.asList(Perfil.PERFIS_USUARIO_EXTERNO).contains(this.getChave());
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (chave != null ? chave.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Perfil)) {
			return false;
		}
		Perfil other = (Perfil) object;
		if ((this.chave == null && other.chave != null) || (this.chave != null && !this.chave.equals(other.chave))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Perfil[ chave=" + chave + " ]";
	}
}
