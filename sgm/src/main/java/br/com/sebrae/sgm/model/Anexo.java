package br.com.sebrae.sgm.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.utils.Constants;

/**
 * @author Diego
 */
@Entity
@Table(name = "ANEXO")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Anexo.findAll", query = "SELECT a FROM Anexo a"),
		@NamedQuery(name = "Anexo.findById", query = "SELECT a FROM Anexo a WHERE a.id = :id"),
		@NamedQuery(name = "Anexo.findByNome", query = "SELECT a FROM Anexo a WHERE a.nome = :nome"),
		@NamedQuery(name = "Anexo.findByTipo", query = "SELECT a FROM Anexo a WHERE a.tipo = :tipo") })
@Audited
public class Anexo implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(Anexo.class);

	public static final String ROOT_PATH = Constants.FILES_DIR + File.separator + "anexo";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "NOME")
	private String nome;

	@Column(name = "NOME_EXIBICAO")
	private String nomeExibicao;

	@Basic(optional = false)
	@Column(name = "TIPO")
	private String tipo;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinColumn(name = "ID_CONFIGURACAO_AUDITORIA", referencedColumnName = "ID")
	@ManyToOne
	private ConfiguracaoAuditoria configuracaoAuditoria;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinColumn(name = "ID_CONFIGURACAO_FORMA_AQUISICAO", referencedColumnName = "ID")
	@ManyToOne
	private ConfigurarFormaAquisicao configuracaoFormaAquisicao;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@JoinColumns({ @JoinColumn(name = "ID_SOLUCAO_EDUCACIONAL", referencedColumnName = "ID_SOLUCAO_EDUCACIONAL"),
			@JoinColumn(name = "ID_META_SOLUCAO_EDUCACIONAL", referencedColumnName = "ID_META") })
	@ManyToOne
	private SolucaoEducacionalMeta solucaoEducacionalMeta;

	@JoinColumn(name = "ID_META", referencedColumnName = "ID")
	@ManyToOne
	private Meta meta;

	@Transient
	private byte[] arquivo;

	@Transient
	private File arquivoFile;

	public Anexo() {
	}

	public Anexo(Integer id) {
		this.id = id;
	}

	public Anexo(Integer id, String nome, String tipo) {
		this.id = id;
		this.nome = nome;
		this.tipo = tipo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public ConfiguracaoAuditoria getConfiguracaoAuditoria() {
		return configuracaoAuditoria;
	}

	public void setConfiguracaoAuditoria(ConfiguracaoAuditoria configuracaoAuditoria) {
		this.configuracaoAuditoria = configuracaoAuditoria;
	}

	public ConfigurarFormaAquisicao getConfiguracaoFormaAquisicao() {
		return configuracaoFormaAquisicao;
	}

	public void setConfiguracaoFormaAquisicao(ConfigurarFormaAquisicao configuracaoFormaAquisicao) {
		this.configuracaoFormaAquisicao = configuracaoFormaAquisicao;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public SolucaoEducacionalMeta getSolucaoEducacionalMeta() {
		return solucaoEducacionalMeta;
	}

	public void setSolucaoEducacionalMeta(SolucaoEducacionalMeta solucaoEducacionalMeta) {
		this.solucaoEducacionalMeta = solucaoEducacionalMeta;
	}

	public String getNomeExibicao() {
		return nomeExibicao;
	}

	public void setNomeExibicao(String nomeExibicao) {
		this.nomeExibicao = nomeExibicao;
	}

	public byte[] getArquivo() {
		if (arquivo == null) {
			File f = getArquivoFile();
			if (f != null) {
				try {
					arquivo = FileUtils.readFileToByteArray(f);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return arquivo;
	}

	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}

	public File getArquivoFile() {
		if (!StringUtils.isBlank(this.nome) && this.getId() != null) {
			this.arquivoFile = new File(getPathComplete());
		}
		return arquivoFile;
	}

	public void setArquivoFile(File arquivoFile) {
		this.arquivoFile = arquivoFile;
	}

	public String getPath() {
		return Usuario.ROOT_PATH + File.separator + this.getId();
	}

	public String getPathComplete() {
		return Usuario.ROOT_PATH + File.separator + this.getId() + File.separator + this.getNome();
	}

	public String getTempPath() {
		return Constants.FILES_TMP_DIR + File.separator + this.getNome();
	}

	public File getFotoFileTemp() {
		String tempPath = getTempPath();
		File retorno = new File(tempPath);
		if (retorno.exists()) {
			return retorno;
		}
		return null;
	}

	@PostPersist
	public void aposPersistir() throws Exception {
		persistirArquivo();
	}

	private void persistirArquivo() throws Exception {
		File dir = new File(getPath());
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new Exception("Nao foi possivel criar o diretorio: " + getPath());
			}
		}
		File arquivoTemp = getFotoFileTemp();
		if (arquivoTemp != null) {
			File arquivoFinal = new File(dir + File.separator + getNome());
			if (arquivoFinal.exists()) {
				if (arquivoFinal.delete()) {
					FileUtils.moveFile(arquivoTemp, arquivoFinal);
				}
			} else {
				FileUtils.moveFile(arquivoTemp, arquivoFinal);
			}
		}
	}

	public String getTipoDescricao() {
		if (tipo != null) {
			if (tipo.contains("excel") || tipo.contains("xls") || tipo.contains("spreadsheetml")) {
				return "Excel";
			}

			if (tipo.contains("pdf")) {
				return "PDF";
			}

			if (tipo.contains("word") || tipo.contains("wordprocessingml")) {
				return "Word";
			}

			if (tipo.contains("jpg") || tipo.contains("png")) {
				return "Imagem";
			}

			if (tipo.contains("powerpoint") || tipo.contains("presentationml")) {
				return "PowerPoint";
			}

			if (tipo.contains("txt")) {
				return "Texto";
			}
		}

		return tipo;
	}

	@PostUpdate
	public void aposAtualizar() throws Exception {
		persistirArquivo();
	}

	@PostRemove
	public void aposRemover() {
		if (this.getArquivoFile() != null) {
			this.getArquivoFile().delete();
		}
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Anexo)) {
			return false;
		}
		Anexo other = (Anexo) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Anexo[ id=" + id + " ]";
	}

}
