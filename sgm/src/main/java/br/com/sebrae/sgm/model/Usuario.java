package br.com.sebrae.sgm.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.sebrae.sgm.jpa.validator.CPF;
import br.com.sebrae.sgm.model.enums.TipoUsuario;
import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.FacesUtil;

/**
 * @author Diego
 */
@Entity
@Table(name = "USUARIO")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u order by u.nome asc"),
		@NamedQuery(name = "Usuario.findAllExternos", query = "SELECT distinct u FROM Usuario u where u.tipo = 'E' order by u.nome asc"),
		@NamedQuery(name = "Usuario.findAllInternos", query = "SELECT distinct u FROM Usuario u where u.tipo = 'I' and u.nome is not null order by u.nome asc"),
		@NamedQuery(name = "Usuario.findById", query = "SELECT u FROM Usuario u WHERE u.id = :id"),
		@NamedQuery(name = "Usuario.findByCpf", query = "SELECT u FROM Usuario u WHERE u.cpf = :cpf"),
		@NamedQuery(name = "Usuario.findByCpfExterno", query = "SELECT u FROM Usuario u WHERE u.cpf = :cpf and u.tipo = 'E'"),
		@NamedQuery(name = "Usuario.findBySenha", query = "SELECT u FROM Usuario u WHERE u.senha = :senha"),
		@NamedQuery(name = "Usuario.findByEmail", query = "SELECT u FROM Usuario u WHERE u.email = :email"),
		@NamedQuery(name = "Usuario.findByNome", query = "SELECT u FROM Usuario u WHERE u.nome = :nome"),
		@NamedQuery(name = "Usuario.findByAtivo", query = "SELECT u FROM Usuario u WHERE u.ativo = :ativo"),
		@NamedQuery(name = "Usuario.findByMatricula", query = "SELECT u FROM Usuario u WHERE u.matricula = :matricula") })
public class Usuario implements UserDetails {

	private static final long serialVersionUID = 1L;

	public static final String ROOT_PATH = Constants.FILES_DIR + File.separator + "usuario";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "foto")
	private String foto;

	@CPF(message = "CPF inv\u00E1lido")
	@Basic(optional = false)
	@Column(name = "CPF")
	private String cpf;

	@Basic(optional = false)
	@Column(name = "SENHA")
	private String senha;

	@Email(message = "Email inv\u00E1lido")
	@Column(name = "EMAIL")
	private String email;

	@Column(name = "NOME")
	private String nome;

	@Basic(optional = false)
	@Column(name = "ATIVO")
	private Boolean ativo = Boolean.FALSE;

	@Column(name = "MATRICULA")
	private String matricula;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ULTIMO_ACESSO")
	private Date ultimoAcesso;

	@Basic(optional = false)
	@Column(name = "TIPO")
	@Enumerated(EnumType.STRING)
	private TipoUsuario tipo;

	@JoinColumns({ @JoinColumn(name = "UF_ESPACO_OCUPACIONAL", referencedColumnName = "EspacoOcupacionalUF"),
			@JoinColumn(name = "COD_ESPACO_OCUPACIONAL", referencedColumnName = "EspacoOcupacionalCodEspOcup") })
	@ManyToOne(fetch = FetchType.EAGER)
	private EspacoOcupacional espacoOcupacional;

	@JoinColumns({ @JoinColumn(name = "UF_UNIDADE", referencedColumnName = "UnidadeUf"),
			@JoinColumn(name = "COD_UNIDADE", referencedColumnName = "UnidadeCodUnidade") })
	@ManyToOne(fetch = FetchType.EAGER)
	private Unidade unidade;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
	private List<RMColaborador> colaboradores;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
	private List<PropriedadesUsuarioExterno> propriedadesUsuarioExterno;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
	private List<UsuarioPerfil> perfis;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "colaborador")
	private List<Meta> metas;

	@ManyToMany(mappedBy = "usuarios")
	private List<Funcionalidade> funcionalidades;

	@Transient
	private byte[] fotoBytes;

	@Transient
	private File fotoFile;

	@Transient
	private String senhaVo;

	@Transient
	private String senhaConfirmacao;

	@Transient
	@Email(message = "Email inv\u00E1lido")
	private String emailConfirmacao;

	@Transient
	private boolean exibirSenha = false;

	@Transient
	private boolean exibirSenhaConfirmacao = false;
	
	public Usuario() {
	}

	public Usuario(Integer id) {
		this.id = id;
	}

	public Usuario(Integer id, String cpf, String senha, boolean ativo) {
		this.id = id;
		this.cpf = cpf;
		this.senha = senha;
		this.ativo = ativo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNome() {
		return nome;
	}

	public String getNomeLimitado() {
		if (nome != null && nome.length() > 8) {
			return nome.substring(0, 8) + "...";
		}
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public List<UsuarioPerfil> getPerfis() {
		return perfis;
	}

	public void setPerfis(List<UsuarioPerfil> perfis) {
		this.perfis = perfis;
	}

	public List<Meta> getMetas() {
		return metas;
	}

	public void setMetas(List<Meta> metas) {
		this.metas = metas;
	}

	public List<Funcionalidade> getFuncionalidades() {
		return funcionalidades;
	}

	public void setFuncionalidades(List<Funcionalidade> funcionalidades) {
		this.funcionalidades = funcionalidades;
	}

	public Date getUltimoAcesso() {
		return ultimoAcesso;
	}

	public void setUltimoAcesso(Date ultimoAcesso) {
		this.ultimoAcesso = ultimoAcesso;
	}

	public List<PropriedadesUsuarioExterno> getPropriedadesUsuarioExterno() {
		return propriedadesUsuarioExterno;
	}

	public void setPropriedadesUsuarioExterno(List<PropriedadesUsuarioExterno> propriedadesUsuarioExterno) {
		this.propriedadesUsuarioExterno = propriedadesUsuarioExterno;
	}

	public String getSenhaVo() {
		return senhaVo;
	}

	public void setSenhaVo(String senhaVo) {
		this.senhaVo = senhaVo;
	}

	public String getSenhaConfirmacao() {
		return senhaConfirmacao;
	}

	public void setSenhaConfirmacao(String senhaConfirmacao) {
		this.senhaConfirmacao = senhaConfirmacao;
	}

	public String getEmailConfirmacao() {
		return emailConfirmacao;
	}

	public void setEmailConfirmacao(String emailConfirmacao) {
		this.emailConfirmacao = emailConfirmacao;
	}

	public List<RMColaborador> getColaboradores() {
		return colaboradores;
	}

	public void setColaboradores(List<RMColaborador> colaboradores) {
		this.colaboradores = colaboradores;
	}

	public TipoUsuario getTipo() {
		return tipo;
	}

	public void setTipo(TipoUsuario tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "br.com.sebrae.sgm.model.Usuario[ id=" + id + " ]";
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		List<SimpleGrantedAuthority> retorno = new ArrayList<SimpleGrantedAuthority>();

		if (this.getPerfis() != null && !this.getPerfis().isEmpty()) {
			for (UsuarioPerfil p : this.perfis) {
				retorno.add(new SimpleGrantedAuthority(p.getPerfil().getChave()));
			}
		}

		if (this.getFuncionalidades() != null && !this.getFuncionalidades().isEmpty()) {
			for (Funcionalidade f : this.funcionalidades) {
				retorno.add(new SimpleGrantedAuthority("ROLE_" + f.getChave()));
			}
		}

		if (this.getPropriedadesUsuarioExterno() != null && !(this.getPropriedadesUsuarioExterno().isEmpty())) {
			for (PropriedadesUsuarioExterno p : this.getPropriedadesUsuarioExterno()) {
				retorno.add(new SimpleGrantedAuthority(p.getPerfil().getChave()));
			}
		}

		return retorno;
	}

	@Override
	public String getPassword() {
		return this.senha;
	}

	@Override
	public String getUsername() {
		return this.cpf;
	}

	public boolean isAdministradorMaster() {
		if (this.perfis != null) {
			Perfil p = new Perfil(Perfil.ADM_MASTER);
			return perfis.contains(p);
		}
		return false;
	}

	public Perfil getTipoAdministrador() {
		if (perfis != null) {
			for (UsuarioPerfil up : this.perfis) {
				if (up.getPerfil().getChave().equals(Perfil.ADM_MASTER)
						|| up.getPerfil().getChave().equals(Perfil.ADM_UF_NACIONAL)) {
					return up.getPerfil();
				}
			}
		}
		return null;
	}

	public Perfil getTipoChefe() {
		if (perfis != null) {
			if (perfis != null) {
				for (UsuarioPerfil up : this.perfis) {
					if (Arrays.asList(Perfil.PERFIS_CHEFIA).contains(up.getPerfil().getChave())) {
						return up.getPerfil();
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.ativo;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public byte[] getFotoBytes() throws IOException {
		if (fotoBytes == null) {
			if (!StringUtils.isBlank(this.foto) && this.getId() != null) {
				this.fotoBytes = FileUtils.readFileToByteArray(new File(Constants.FILES_DIR + File.separator
						+ "usuario" + File.separator + this.getId() + File.separator + this.getFoto()));
			} else {
				this.fotoBytes = FileUtils.readFileToByteArray(new File(FacesUtil.getExternalContext().getRealPath(
						"/resources/img/sem_foto.jpg")));
			}
		}
		return fotoBytes;
	}

	public void setFotoBytes(byte[] fotoBytes) {
		this.fotoBytes = fotoBytes;
	}

	public File getFotoFile() {
		if (!StringUtils.isBlank(this.foto) && this.getId() != null) {
			this.fotoFile = new File(Constants.FILES_DIR + File.separator + "usuario" + File.separator + this.getId()
					+ File.separator + this.getFoto());
		}
		return fotoFile;
	}

	public void setFotoFile(File fotoFile) {
		this.fotoFile = fotoFile;
	}

	public String getPath() {
		return Usuario.ROOT_PATH + File.separator + this.getId();
	}

	public String getPathComplete() {
		return Usuario.ROOT_PATH + File.separator + this.getId() + File.separator + this.getFoto();
	}

	public String getTempPath() {
		return Constants.FILES_TMP_DIR + File.separator + this.getFoto();
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
			File arquivoFinal = new File(dir + File.separator + getFoto());
			if (arquivoFinal.exists()) {
				if (arquivoFinal.delete()) {
					FileUtils.moveFile(arquivoTemp, arquivoFinal);
				}
			} else {
				FileUtils.moveFile(arquivoTemp, arquivoFinal);
			}
		}
	}

	@PostUpdate
	public void aposAtualizar() throws Exception {
		persistirArquivo();
	}

	@PostRemove
	public void aposRemover() {
		if (this.getFotoFile() != null) {
			this.getFotoFile().delete();
		}
	}

	public List<Perfil> getPerfisSistema() {
		List<Perfil> retorno = new ArrayList<Perfil>();
		if (this.perfis != null) {
			for (UsuarioPerfil up : this.perfis) {
				if (!retorno.contains(up.getPerfil()))
					retorno.add(up.getPerfil());
			}
		}
		if (this.getPropriedadesUsuarioExterno() != null && !this.getPropriedadesUsuarioExterno().isEmpty()) {
			for (PropriedadesUsuarioExterno pue : getPropriedadesUsuarioExterno()) {
				if (!retorno.contains(pue.getPerfil()))
					retorno.add(pue.getPerfil());
			}
		}
		return retorno;
	}

	public boolean isAtivoRm() {
		return true;
	}

	public EspacoOcupacional getEspacoOcupacional() {
		return espacoOcupacional;
	}

	public void setEspacoOcupacional(EspacoOcupacional espacoOcupacional) {
		this.espacoOcupacional = espacoOcupacional;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public List<Unidade> getUnidades() {
		List<Unidade> retorno = new ArrayList<Unidade>();
		retorno.add(this.getUnidade());
		if (this.perfis != null) {
			for (UsuarioPerfil up : this.perfis) {
				if (up.getUnidade() != null)
					if (!retorno.contains(up.getUnidade())) {
						retorno.add(up.getUnidade());
					}

			}
		}
		return retorno;
	}

	public Unidade getUnidadeAtual() {
		return this.unidade;
	}

	public boolean isExibirSenha() {
		return exibirSenha;
	}

	public void setExibirSenha(boolean exibirSenha) {
		this.exibirSenha = exibirSenha;
	}

	public boolean isExibirSenhaConfirmacao() {
		return exibirSenhaConfirmacao;
	}

	public void setExibirSenhaConfirmacao(boolean exibirSenhaConfirmacao) {
		this.exibirSenhaConfirmacao = exibirSenhaConfirmacao;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Usuario)) {
			return false;
		}
		Usuario other = (Usuario) object;
		if ((this.getId() == null && other.getId() != null)
				|| (this.getId() != null && !this.getId().equals(other.getId()))) {
			return false;
		}
		return true;
	}

}
