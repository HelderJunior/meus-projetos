package br.com.sebrae.sgm.controller;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.jar.Manifest;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.sebrae.sgm.controller.dto.ArvoreUnidadesDTO;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfiguracaoMetas;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.MetaStatus;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.PropriedadesUsuarioExterno;
import br.com.sebrae.sgm.model.RMColaborador;
import br.com.sebrae.sgm.model.SolucaoEducacionalMeta;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.model.enums.VinculoOcupacional;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.ConfiguracaoMetasService;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.PerfilService;
import br.com.sebrae.sgm.service.SolucaoEducacionalMetaService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioPerfilService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.FacesUtil;
import br.com.sebrae.sgm.utils.UnidadeUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

@SessionScoped
@Named
public class AppBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private CicloService cicloService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private MetaService metaService;

	@Inject
	private PerfilService perfilService;

	@Inject
	private ConfiguracaoMetasService configuracaoMetaService;

	@Inject
	private SolucaoEducacionalMetaService solucaoEducacionalMetaService;

	private Usuario usuarioAutenticado;

	private Usuario gerenteUsuarioLogado;

	private Usuario diretorUsuarioLogado;

	private Stack<String> backStack = new Stack<String>();

	private boolean isIdle;

	private Integer tempoMaximoLogado = 1800;// segundos
	private long tempoRestanteLogado = tempoMaximoLogado;

	private UF ufSelecionada;

	private RMColaborador colaboradorSelecionado;

	private Perfil perfilSelecionado;

	private List<Perfil> perfisUsuarioLogado;

	private Ciclo cicloSelecionado;

	// private List<Ciclo> ciclosUf;

	private List<Unidade> unidadesUf;

	private List<Ciclo> ciclosAbertos = new ArrayList<Ciclo>();
	private List<Ciclo> ciclosFinalizados = new ArrayList<Ciclo>();

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	private ArvoreUnidadesDTO arvoreUnidades;

	@Inject
	private UsuarioPerfilService usuarioPerfilService;

	public void init() {
		Boolean isPrimeiroAcesso = (Boolean) FacesUtil.getHttpSession().getAttribute("primeiro_acesso");

		if (isPrimeiroAcesso != null) {
			FacesUtil
					.addInfoMessage("Como este \u00E9 o seu primeiro acesso ao SGM, sua nova senha foi enviada para o email: "
							+ getUsuarioAutenticado().getEmail());
			FacesUtil.getHttpSession().setAttribute("primeiro_acesso", false);
		}
	}

	public Usuario getUsuarioAutenticado() {

		if (this.usuarioAutenticado == null) {
			try {
				this.usuarioAutenticado = this.usuarioService.loadByCpf(FacesUtil.getHttpServletRequest()
						.getUserPrincipal().getName());

				if (this.usuarioAutenticado == null) {
					this.usuarioAutenticado = this.usuarioService.loadByCpf(FacesUtil.getHttpServletRequest()
							.getUserPrincipal().getName());
				}
			} catch (Exception e) {
				FacesUtil.addErrorMessage(e.getMessage());
			}
		}
		return usuarioAutenticado;
	}

	public Usuario getGerenteUsuarioLogado() {
		if (gerenteUsuarioLogado == null) {
			if (getUsuarioAutenticado().getUnidades() != null && !this.getUsuarioAutenticado().getUnidades().isEmpty()) {
				gerenteUsuarioLogado = usuarioService.findGerente(this.getUsuarioAutenticado().getUnidades().get(0));
			}
		}
		return gerenteUsuarioLogado;
	}

	public Usuario getDiretorUsuarioLogado() {
		if (diretorUsuarioLogado == null) {
			if (getUsuarioAutenticado().getUnidades() != null && !this.getUsuarioAutenticado().getUnidades().isEmpty()) {
				diretorUsuarioLogado = usuarioService.findDiretor(this.getUsuarioAutenticado().getUnidades().get(0));
			}
		}
		return diretorUsuarioLogado;
	}

	public String back() {
		if (!backStack.isEmpty()) {
			return backStack.pop();
		} else {
			return "/pages/index.xhtml";
		}
	}

	public void setBack(String value) {
		backStack.push(value);
	}

	public Date getData() {
		return new Date();
	}

	public void atualizarTempoLogout() {
		// do nothing
	}

	public Boolean usuarioEstaComPerfil(String chavePerfil) {
		if (this.perfilSelecionado != null && this.perfilSelecionado.getChave().equals(chavePerfil)) {
			return true;
		}
		return false;
	}

	public UF getUfSelecionada() {
		return ufSelecionada;
	}

	public List<UF> getUfs() {
		return Arrays.asList(UF.values());
	}

	public void setUfSelecionada(UF ufSelecionada) {
		this.ufSelecionada = ufSelecionada;
	}

	public RMColaborador getColaboradorSelecionado() {
		return colaboradorSelecionado;
	}

	public void setColaboradorSelecionado(RMColaborador colaboradorSelecionado) {
		this.colaboradorSelecionado = colaboradorSelecionado;
	}

	public Perfil getPerfilSelecionado() {
		return perfilSelecionado;
	}

	public void setPerfilSelecionado(Perfil perfilSelecionado) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(perfilSelecionado.getChave()));
		Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(),
				authorities);
		SecurityContextHolder.getContext().setAuthentication(newAuth);
		this.perfilSelecionado = perfilSelecionado;
	}

	public void setUsuarioAutenticado(Usuario usuarioAutenticado) {
		this.usuarioAutenticado = usuarioAutenticado;
	}

	public void uploadFoto(FileUploadEvent event) {
		try {
			String fileName = FilenameUtils.getName(event.getFile().getFileName());
			usuarioAutenticado.setFoto(RandomStringUtils.random(8, true, true) + fileName);

			byte[] fotoBytes = IOUtils.toByteArray(event.getFile().getInputstream());

			usuarioAutenticado.setFotoBytes(fotoBytes);

			FileUtils.writeByteArrayToFile(new File(Constants.FILES_TMP_DIR + File.separator
					+ getUsuarioAutenticado().getFoto()), fotoBytes);

			usuarioService.save(usuarioAutenticado);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao enviar foto, tente novamente mais tarde.");
		}
	}

	public long getTempoRestanteLogado() {
		return tempoRestanteLogado;
	}

	public void setTempoRestanteLogado(long tempoRestanteLogado) {
		this.tempoRestanteLogado = tempoRestanteLogado;
	}

	public Ciclo getCicloSelecionado() {
		if (cicloSelecionado == null) {
			List<Ciclo> ciclosDisponiveis = getCiclosUf();
			if (ciclosDisponiveis != null && !ciclosDisponiveis.isEmpty()) {
				cicloSelecionado = ciclosDisponiveis.get(0);
			}
		}
		if (cicloSelecionado != null) {
			cicloSelecionado = cicloService.load(cicloSelecionado.getId());
		}
		return cicloSelecionado;
	}

	public void setCicloSelecionado(Ciclo cicloSelecionado) {
		this.cicloSelecionado = cicloSelecionado;
	}

	public Boolean isPodeInserirMeta() {
		if (getCicloSelecionado().getStatusPactuacao(TipoConfiguracaoCiclo.DESEMP) == StatusCiclo.F
				&& !(getCicloSelecionado().getStatusRepactuacao(TipoConfiguracaoCiclo.DESEMP) == StatusCiclo.I)) {
			return true;
		}
		return false;
	}
	public String inserirMetaIndividual() {

		if (this.cicloSelecionado == null) {
			FacesUtil.addErrorMessage("\u00C9 necess\u00E1rio selecionar um ciclo para inserir uma meta");
		} else {
			Integer qtdInseridasIndividual = getQtdMetasInseridasIndividual();
			ConfiguracaoMetas configuracaoMetas = this.configuracaoMetaService.findByCicloUnidadeTipoMeta(
					this.cicloSelecionado, this.getUsuarioAutenticado().getUnidade(), TipoMeta.I);
			if (configuracaoMetas != null) {

				if (qtdInseridasIndividual >= configuracaoMetas.getQtdMaximaMetasIndividuaisPactuadasCiclo()) {
					FacesUtil.addErrorMessage("Voc\u00EA atingiu o n\u00FAmero m\u00E1ximo de metas individual permitidas para este ciclo.");
					return "";
				}
				if (isPodeInserirMeta()) {
					FacesUtil.addErrorMessage("\u00C9 necess\u00E1rio que o Administrador refa\u00E7a as configura\u00E7\u00F5es do calendario deste ciclo para que possa ser inserida uma nova meta.");
				}
				else {
					return "/pages/meta-individual/manter.xhtml";
				}
			} else {
				FacesUtil.addErrorMessage("\u00C9 necess\u00E1rio que o Administrador configure as metas deste ciclo para que possa ser inserida uma nova meta.");
			}
		}
		return "";
	}

	public String inserirMetaEquipe() {
		if (this.cicloSelecionado == null) {
			FacesUtil.addErrorMessage("\u00C9 necess\u00E1rio selecionar um ciclo para inserir uma meta");
		} else {
			Integer qtdInseridasEquipe = getQtdMetasInseridasEquipe();
			ConfiguracaoMetas configuracaoMetas = this.configuracaoMetaService.findByCicloUnidadeTipoMeta(
					this.cicloSelecionado, this.getUsuarioAutenticado().getUnidades().get(0), TipoMeta.E);
			if (configuracaoMetas != null) {
				if (qtdInseridasEquipe >= configuracaoMetas.getQtdMaximaMetasIndividuaisPactuadasCiclo()) {
					FacesUtil.addErrorMessage("Voc\u00EA atingiu o n\u00FAmero m\u00E1ximo de metas de equipe permitidas para este ciclo.");
					return "";
				}
				if (isPodeInserirMeta()) {
					FacesUtil.addErrorMessage("\u00C9 necess\u00E1rio que o Administrador refa\u00E7a as configura\u00E7\u00F5es do calendario deste ciclo para que possa ser inserida uma nova meta.");
				}
				else {
					return "/pages/meta-equipe/manter.xhtml";
				}
			} else {
				FacesUtil.addErrorMessage("\u00C9 necess\u00E1rio que o Administrador configure as metas deste ciclo para que possa ser inserida uma nova meta.");
			}
		}
		return "";
	}

	public String inserirMetaDesenvolvimento() {
		if (this.cicloSelecionado == null) {
			FacesUtil.addErrorMessage("\u00C9 necess\u00E1rio selecionar um ciclo para inserir uma meta");
		} else {
			Integer qtdInseridasEquipe = getQtdMetasInseridasDesenvolvimento();
			ConfiguracaoMetas configuracaoMetas = this.configuracaoMetaService.findByCicloUnidadeTipoMeta(
					this.cicloSelecionado, this.getUsuarioAutenticado().getUnidade(), TipoMeta.D);
			if (configuracaoMetas != null) {
				if (qtdInseridasEquipe >= configuracaoMetas.getQtdMaximaMetasIndividuaisPactuadasCiclo()) {
					FacesUtil.addErrorMessage("Voc\u00EA atingiu o n\u00FAmero m\u00E1ximo de metas de desenvolvimento permitidas para este ciclo.");
					return "";
				}
				if (getCicloSelecionado().getStatusPactuacao(TipoConfiguracaoCiclo.DESENV) == StatusCiclo.F
						&& !(getCicloSelecionado().getStatusRepactuacao(TipoConfiguracaoCiclo.DESENV) == StatusCiclo.I)) {

					FacesUtil.addErrorMessage("\u00C9 necess\u00E1rio que o Administrador refa\u00E7a as configura\u00E7\u00F5es do calendario deste ciclo para que possa ser inserida uma nova meta.");
					return "";
				}
				else {
					return "/pages/meta-desenvolvimento/manter.xhtml";
				}
			} else {
				FacesUtil.addErrorMessage("\u00C9 necess\u00E1rio que o Administrador configure as metas deste ciclo para sua Unidade para que possa ser inserida uma nova meta.");
			}
		}
		return "";
	}

	public String inserirNovoCiclo() {
		return "/pages/ciclo/manter.xhtml";
	}

	public List<Ciclo> getCiclosUf() {
		List<Ciclo> ciclosUf = new ArrayList<Ciclo>();
		if (ciclosUf == null || ciclosUf.isEmpty()) {
			ciclosUf = cicloService.findIniciadosByUf(this.ufSelecionada);
		}
		if (ciclosUf == null || ciclosUf.isEmpty()) {
			if (!FacesUtil.getFacesContext().getMessages().hasNext()) {
				FacesUtil
						.addWarnMessage("N\u00E3o existe nenhum ciclo completamente configurado dispon\u00EDvel na sua UF");
			}
		}
		return ciclosUf;
	}

	public List<Unidade> getUnidadesUf() {
		if (unidadesUf == null || unidadesUf.isEmpty()) {
			unidadesUf = unidadeService.findByUfUsuario(this.getUfSelecionada(), this.getUsuarioAutenticado().getId());
		}
		return unidadesUf;
	}

	public void setUnidadesUf(List<Unidade> unidadesUf) {
		this.unidadesUf = unidadesUf;
	}

	public String getNomeBreadcrumbinicial() {
		return perfilSelecionado.getDescricao();
	}

	public List<Ciclo> getCiclosAbertos() {
		try {
			ciclosAbertos = cicloService.findCiclosAbertos(getUfSelecionada());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return ciclosAbertos;
	}

	public List<Ciclo> getCiclosFinalizados() {
		try {
			ciclosFinalizados = cicloService.findCiclosFinalizados(getUfSelecionada());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return ciclosFinalizados;
	}

	public Long getQtdMetasPendentes() {
		try {
			Long retorno = null;
			if(getPerfilSelecionado().getChave().equals(Perfil.COLABORADOR_CHAVE.toString())){
				retorno = metaService.getQtdMetasPendentesColaborador(getCicloSelecionado(), getUsuarioAutenticado());
			}
			if(getPerfilSelecionado().getChave().equals(Perfil.GERENTE.toString())){
				List<String> unidades = usuarioPerfilService.findByUnidadesGerenciaveis(getUsuarioAutenticado());
				retorno = metaService.getQtdMetasPendentesGerente(getCicloSelecionado(), getUsuarioAutenticado(), unidades);
			}

			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasGravadasEquipe() {
		try {
			Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado()
					.getUnidadeAtual(), StatusMeta.SA);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasAprovadasEquipe() {
		try {
			Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado()
					.getUnidadeAtual(), StatusMeta.AP);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasCanceladasEquipe() {
		try {
			Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado()
					.getUnidadeAtual(), StatusMeta.CS);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasEnviadasAprovacaoEquipe() {
		try {
			Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado()
					.getUnidadeAtual(), StatusMeta.PA);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasObservacaoSuperiorEquipe() {
		try {
			Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado()
					.getUnidadeAtual(), StatusMeta.OS);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasObservacaoComiteEquipe() {
		try {
			Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado()
					.getUnidadeAtual(), StatusMeta.OC);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

		public Long getQtdMetasEquipeObservacaoUGP() {
			try {
				Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado()
						.getUnidadeAtual(), StatusMeta.OU);
				return retorno;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			return 0L;
		}

		public Long getQtdMetasEquipePendenteAvalicaoComite() {
			try {
				Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado()
						.getUnidadeAtual(), StatusMeta.PAC);
				return retorno;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			return 0L;
		}

		public Long getQtdPendenteMetasEquipeAvalicaoUGP() {
			try {
				Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado()
						.getUnidadeAtual(), StatusMeta.PU);
				return retorno;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			return 0L;
		}

		public Long getQtdMetasEquipeAvaliacaoComite() {
			try {
				Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado().getUnidadeAtual(),
						StatusMeta.AC);
				return retorno;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			return 0L;
		}

		public Long getQtdMetasEquipeAvaliacaoUGP() {
			try {
				Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado().getUnidadeAtual(),
						StatusMeta.AU);
				return retorno;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			return 0L;
		}


	public Long getQtdMetasValidadasComiteEquipe() {
		try {
			Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado()
					.getUnidadeAtual(), StatusMeta.VC);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasPendenteValidacaoComiteEquipe() {
		try {
			Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado()
					.getUnidadeAtual(), StatusMeta.PV, StatusMeta.VC, StatusMeta.NA, StatusMeta.OC);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasAvaliadasEquipe() {
		try {
			Long retorno = metaService.getQtdMetasEquipe(getCicloSelecionado(), getUsuarioAutenticado()
					.getUnidadeAtual(), StatusMeta.VC);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasGravadasIndividual() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.SA);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasAprovadasIndividual() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.AS);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasEnviadasAprovacaoIndividual() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.PA);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasCanceladasIndividual() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.CS);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasObservacaoSuperiorIndividual() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.OS);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasObservacaoComiteIndividual() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.OC);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasIndividualObservacaoUGP() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.OU);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasIndividualPendenteAvalicaoComite() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.PAC);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdPendenteMetasIndividualAvalicaoUGP() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.PU);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasIndividualAprovadas() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.AP);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasIndividualAvaliacaoComite() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.AC);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasIndividualAvaliacaoUGP() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.AU);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}


	public Long getQtdMetasValidadasComiteIndividual() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.VC);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasPendenteValidacaoComiteIndividual() {
		try {
			Long retorno = metaService.getQtdMetasIndividual(getCicloSelecionado(), getUsuarioAutenticado(),
					StatusMeta.PV, StatusMeta.VC, StatusMeta.NA, StatusMeta.OC);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasPendentesDesenvolvimento() {
		try {
			Long retorno = metaService.getQtdMetasDesenvolvimento(this.cicloSelecionado, this.usuarioAutenticado,
					StatusMeta.SA, StatusMeta.OS, StatusMeta.OC, StatusMeta.OA, StatusMeta.NA);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasAprovadasDesenvolvimento() {
		try {
			Long retorno = metaService.getQtdMetasDesenvolvimento(this.cicloSelecionado, this.usuarioAutenticado,
					StatusMeta.AP);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasCanceladasDesenvolvimento() {
		try {
			Long retorno = metaService.getQtdMetasDesenvolvimento(this.cicloSelecionado, this.usuarioAutenticado,
					StatusMeta.CS);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasDesenvolvimentoEnviadasParaAprovacao() {
		try {
			Long retorno = metaService.getQtdMetasDesenvolvimento(this.cicloSelecionado, this.usuarioAutenticado,
					StatusMeta.PA);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasDesenvolvimentoObservacaoSuperior() {
		try {
			Long retorno = metaService.getQtdMetasDesenvolvimento(this.cicloSelecionado, this.usuarioAutenticado,
					StatusMeta.OS);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Long getQtdMetasDesenvolvimentoObservacaoComite() {
		try {
			Long retorno = metaService.getQtdMetasDesenvolvimento(this.cicloSelecionado, this.usuarioAutenticado,
					StatusMeta.OC);
			return retorno;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0L;
	}

	public Integer getQtdMetasFaltantesCicloEquipe() {
		Integer retorno = 0;
		try {
			if (getCicloSelecionado() != null) {
				ConfiguracaoMetas configuracaoMetas = this.configuracaoMetaService.findByCicloUnidadeTipoMeta(
						this.cicloSelecionado, this.getUsuarioAutenticado().getUnidades().get(0), TipoMeta.E);
				if (configuracaoMetas != null) {
					Integer valorMinimo = configuracaoMetas.getQtdMinimaMetasIndividuaisPactuadasCiclo();
					retorno = valorMinimo - getQtdMetasInseridasEquipe();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return retorno;
	}

	public Integer getQtdMetasFaltantesCicloIndividual() {
		Integer retorno = 0;
		try {
			if (getCicloSelecionado() != null) {
				ConfiguracaoMetas configuracaoMetas = this.configuracaoMetaService.findByCicloUnidadeTipoMeta(
						this.cicloSelecionado, this.getUsuarioAutenticado().getUnidades().get(0), TipoMeta.I);
				if (configuracaoMetas != null) {
					Integer valorMinimo = configuracaoMetas.getQtdMinimaMetasIndividuaisPactuadasCiclo();
					retorno = valorMinimo - getQtdMetasInseridasIndividual();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return retorno;
	}

	public Integer getQtdMetasInseridasIndividual() {
		Integer retorno = 0;
		try {
			if (getCicloSelecionado() != null) {
				List<Meta> metasInseridas = metaService.findAllIndividualByCiclo(getCicloSelecionado(), getUsuarioAutenticado());
				if (metasInseridas != null && !metasInseridas.isEmpty()) {
					retorno = metasInseridas.size();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return retorno;
	}

	public Integer getQtdMetasInseridasEquipe() {
		Integer retorno = 0;
		try {
			if (getCicloSelecionado() != null) {
				List<Meta> metasInseridas = metaService.findAllEquipeByCiclo(getCicloSelecionado());
				if (metasInseridas != null && !metasInseridas.isEmpty()) {
					retorno = metasInseridas.size();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return retorno;
	}

	public Integer getQtdMetasInseridasDesenvolvimento() {
		Integer retorno = 0;
		try {
			if (getCicloSelecionado() != null) {
				List<Meta> metasInseridas = metaService.findAllDesenvolvimentoCiclo(getUsuarioAutenticado(),
						getCicloSelecionado());
				if (metasInseridas != null && !metasInseridas.isEmpty()) {
					retorno = metasInseridas.size();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return retorno;
	}

	public boolean isMetasEquipeAprovadas() {
		try {
			if (getCicloConfiguracaoDesempenho() != null) {
				if (getCicloConfiguracaoDesempenho().getMetas() != null
						&& !getCicloConfiguracaoDesempenho().getMetas().isEmpty()) {
					List<Meta> metas = Lists.newArrayList(Collections2.filter(getCicloConfiguracaoDesempenho()
							.getMetas(), new Predicate<Meta>() {
						@Override
						public boolean apply(Meta arg0) {
							return arg0.getTipo() == TipoMeta.E;
						}
					}));

					if (metas.size() > 0) {
						List<Meta> metasAprovadas = Lists.newArrayList(Collections2.filter(
								getCicloConfiguracaoDesempenho().getMetas(), new Predicate<Meta>() {
									@Override
									public boolean apply(Meta arg0) {
										MetaStatus ms = arg0.getMetaStatusByFase(Fase.P);
										return ms != null && ms.getStatus() == StatusMeta.AS;
									}
								}));

						if (metasAprovadas.size() == metas.size()) {
							return true;
						}
					}

				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	public boolean isMetasIndividualAprovadas() {
		try {
			if (getCicloConfiguracaoDesempenho() != null) {
				if (getCicloConfiguracaoDesempenho().getMetas() != null
						&& !getCicloConfiguracaoDesempenho().getMetas().isEmpty()) {
					List<Meta> metas = Lists.newArrayList(Collections2.filter(getCicloConfiguracaoDesempenho()
							.getMetas(), new Predicate<Meta>() {
						@Override
						public boolean apply(Meta arg0) {
							return arg0.getTipo() == TipoMeta.I;
						}
					}));
					if (metas.size() > 0) {
						List<Meta> metasAprovadas = Lists.newArrayList(Collections2.filter(
								getCicloConfiguracaoDesempenho().getMetas(), new Predicate<Meta>() {
									@Override
									public boolean apply(Meta arg0) {
										MetaStatus ms = arg0.getMetaStatusByFase(Fase.P);
										return ms != null && ms.getStatus() == StatusMeta.AS;
									}
								}));

						if (metasAprovadas.size() == metas.size()) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	public Integer getPorcentagemMetaDesempenhoEquipe() {
		BigDecimal retorno = BigDecimal.ZERO;
		try {
			List<Meta> metasEquipe = metaService.findAllEquipeCicloMonitoramento(getUsuarioAutenticado()
					.getUnidadeAtual(), getCicloSelecionado());
			if (metasEquipe != null && !metasEquipe.isEmpty()) {
				BigDecimal valorAcumulado = BigDecimal.ZERO;
				for (Meta meta : metasEquipe) {
					valorAcumulado = valorAcumulado.add(BigDecimal.valueOf(meta.getPorcentagemConclusao()));
				}
				retorno = valorAcumulado.divide(BigDecimal.valueOf(metasEquipe.size()), 0, RoundingMode.HALF_UP);
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao obter porcentagem de conclus\u00E3o das metas de equipe.");
		}
		return retorno.intValue();
	}

	public Integer getPorcentagemMetaDesempenhoIndividual() {
		BigDecimal retorno = BigDecimal.ZERO;
		try {
			List<Meta> metasIndividual = metaService.findAllIndividuaisCicloMonitoramento(getUsuarioAutenticado(),
					getCicloSelecionado());
			if (metasIndividual != null && !metasIndividual.isEmpty()) {
				BigDecimal valorAcumulado = BigDecimal.ZERO;
				for (Meta meta : metasIndividual) {
					valorAcumulado = valorAcumulado.add(BigDecimal.valueOf(meta.getPorcentagemConclusao()));
				}
				retorno = valorAcumulado.divide(BigDecimal.valueOf(metasIndividual.size()), 0, RoundingMode.HALF_UP);
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao obter porcentagem de conclus\u00E3o das metas de equipe.");
		}
		return retorno.intValue();
	}

	public BigDecimal getPorcentagemHorasFundamentaisExecutadas() {
		return solucaoEducacionalMetaService.getPorcentagemHorasExecutadas(this.getCicloConfiguracaoDesenvolvimento(),
				this.getUsuarioAutenticado(), VinculoOcupacional.F);
	}

	public BigDecimal getPorcentagemHorasComplementaresExecutadas() {
		return solucaoEducacionalMetaService.getPorcentagemHorasExecutadas(this.getCicloConfiguracaoDesenvolvimento(),
				this.getUsuarioAutenticado(), VinculoOcupacional.C);
	}

	public BigDecimal getCargaHorariaPrevista() {
		return solucaoEducacionalMetaService.getQtdCargaHorariaPrevista(this.getCicloConfiguracaoDesenvolvimento(),
				this.getUsuarioAutenticado());
	}

	public BigDecimal getPontuacaoPrevista() {
		return solucaoEducacionalMetaService.getPontuacaoPrevista(this.getCicloConfiguracaoDesenvolvimento(),
				this.getUsuarioAutenticado());
	}

	public BigDecimal getPontuacaoRealizada() {
		return solucaoEducacionalMetaService.getPontuacaoRealizada(this.getCicloConfiguracaoDesenvolvimento(),
				this.getUsuarioAutenticado());
	}

	public ArvoreUnidadesDTO getArvoreUnidades() {
		return UnidadeUtils.arvoreUnidades;
	}

	public Long getQtdSolucoesAprovadas() {
		Long retorno = 0L;
		List<SolucaoEducacionalMeta> solucoesMetasAprovadas = solucaoEducacionalMetaService.findByUsuarioAndStatusMeta(
				getUsuarioAutenticado(), StatusMeta.AS);
		if (solucoesMetasAprovadas != null) {
			retorno = Long.valueOf(solucoesMetasAprovadas.size());
		}
		return retorno;
	}

	public BigDecimal getQtdHorasSolucoesAprovadas() {
		BigDecimal retorno = BigDecimal.ZERO;
		List<SolucaoEducacionalMeta> solucoesMetasAprovadas = solucaoEducacionalMetaService.findByUsuarioAndStatusMeta(
				getUsuarioAutenticado(), StatusMeta.AS);
		if (solucoesMetasAprovadas != null) {
			for (SolucaoEducacionalMeta sem : solucoesMetasAprovadas) {
				if (sem.getQuantidadePrevista() != null) {
					retorno = retorno.add(sem.getQuantidadePrevista());
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getQtdPontosSolucoesAprovadas() {
		BigDecimal retorno = BigDecimal.ZERO;
		List<SolucaoEducacionalMeta> solucoesMetasAprovadas = solucaoEducacionalMetaService.findByUsuarioAndStatusMeta(
				getUsuarioAutenticado(), StatusMeta.AS);
		if (solucoesMetasAprovadas != null) {
			for (SolucaoEducacionalMeta sem : solucoesMetasAprovadas) {
				retorno = retorno.add(sem.getPontuacaoPrevista());
			}
		}
		return retorno;
	}

	public Long getQtdSolucoesNaoAprovadas() {
		Long retorno = 0L;
		List<SolucaoEducacionalMeta> solucoesMetasAprovadas = solucaoEducacionalMetaService.findByUsuarioAndStatusMeta(
				getUsuarioAutenticado(), StatusMeta.NA);
		if (solucoesMetasAprovadas != null) {
			retorno = Long.valueOf(solucoesMetasAprovadas.size());
		}
		return retorno;
	}

	public BigDecimal getQtdHorasSolucoesNaoAprovadas() {
		BigDecimal retorno = BigDecimal.ZERO;
		List<SolucaoEducacionalMeta> solucoesMetasAprovadas = solucaoEducacionalMetaService.findByUsuarioAndStatusMeta(
				getUsuarioAutenticado(), StatusMeta.NA);
		if (solucoesMetasAprovadas != null) {
			for (SolucaoEducacionalMeta sem : solucoesMetasAprovadas) {
				if (sem.getQuantidadePrevista() != null) {
					retorno = retorno.add(sem.getQuantidadePrevista());
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getQtdPontosSolucoesNaoAprovadas() {
		BigDecimal retorno = BigDecimal.ZERO;
		List<SolucaoEducacionalMeta> solucoesMetasAprovadas = solucaoEducacionalMetaService.findByUsuarioAndStatusMeta(
				getUsuarioAutenticado(), StatusMeta.NA);
		if (solucoesMetasAprovadas != null) {
			for (SolucaoEducacionalMeta sem : solucoesMetasAprovadas) {
				retorno = retorno.add(sem.getPontuacaoPrevista());
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public Long getQtdSolucoesPendentes() {
		Long retorno = 0L;
		List<SolucaoEducacionalMeta> solucoesMetasAprovadas = solucaoEducacionalMetaService.findByUsuarioAndStatusMeta(
				getUsuarioAutenticado(), StatusMeta.PA);
		if (solucoesMetasAprovadas != null) {
			retorno = Long.valueOf(solucoesMetasAprovadas.size());
		}
		return retorno;
	}

	public BigDecimal getQtdHorasSolucoesPendentes() {
		BigDecimal retorno = BigDecimal.ZERO;
		List<SolucaoEducacionalMeta> solucoesMetasAprovadas = solucaoEducacionalMetaService.findByUsuarioAndStatusMeta(
				getUsuarioAutenticado(), StatusMeta.PA);
		if (solucoesMetasAprovadas != null) {
			for (SolucaoEducacionalMeta sem : solucoesMetasAprovadas) {
				if (sem.getQuantidadePrevista() != null) {
					retorno = retorno.add(sem.getQuantidadePrevista());
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getQtdPontosSolucoesPendentes() {
		BigDecimal retorno = BigDecimal.ZERO;
		List<SolucaoEducacionalMeta> solucoesMetasAprovadas = solucaoEducacionalMetaService.findByUsuarioAndStatusMeta(
				getUsuarioAutenticado(), StatusMeta.PA);
		if (solucoesMetasAprovadas != null) {
			for (SolucaoEducacionalMeta sem : solucoesMetasAprovadas) {
				retorno = retorno.add(sem.getPontuacaoPrevista());
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public Long getQtdSolucoesCanceladas() {
		Long retorno = 0L;
		List<SolucaoEducacionalMeta> solucoesMetasAprovadas = solucaoEducacionalMetaService.findByUsuarioAndStatusMeta(
				getUsuarioAutenticado(), StatusMeta.CS);
		if (solucoesMetasAprovadas != null) {
			retorno = Long.valueOf(solucoesMetasAprovadas.size());
		}
		return retorno;
	}

	public BigDecimal getQtdHorasSolucoesCanceladas() {
		BigDecimal retorno = BigDecimal.ZERO;
		List<SolucaoEducacionalMeta> solucoesMetasAprovadas = solucaoEducacionalMetaService.findByUsuarioAndStatusMeta(
				getUsuarioAutenticado(), StatusMeta.CS);
		if (solucoesMetasAprovadas != null) {
			for (SolucaoEducacionalMeta sem : solucoesMetasAprovadas) {
				if (sem.getQuantidadePrevista() != null) {
					retorno = retorno.add(sem.getQuantidadePrevista());
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getQtdPontosSolucoesCanceladas() {
		BigDecimal retorno = BigDecimal.ZERO;
		List<SolucaoEducacionalMeta> solucoesMetasAprovadas = solucaoEducacionalMetaService.findByUsuarioAndStatusMeta(
				getUsuarioAutenticado(), StatusMeta.CS);
		if (solucoesMetasAprovadas != null) {
			for (SolucaoEducacionalMeta sem : solucoesMetasAprovadas) {
				retorno = retorno.add(sem.getPontuacaoPrevista());
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public List<Perfil> getPerfisUsuarioLogado() {
		perfisUsuarioLogado = new ArrayList<Perfil>();
		if (getUsuarioAutenticado() != null) {
			perfisUsuarioLogado = perfilService.findByUsuario(getUsuarioAutenticado());
			if (getUsuarioAutenticado().getPropriedadesUsuarioExterno() != null) {
				List<PropriedadesUsuarioExterno> propriedadesUsuarioExterno = getUsuarioAutenticado()
						.getPropriedadesUsuarioExterno();
				if (propriedadesUsuarioExterno != null && !propriedadesUsuarioExterno.isEmpty()) {
					for (PropriedadesUsuarioExterno pue : propriedadesUsuarioExterno) {
						perfisUsuarioLogado.add(pue.getPerfil());
					}
				}
			}
		}
		return perfisUsuarioLogado;
	}

	public CicloConfiguracao getCicloConfiguracaoDesempenho() {
		if (getCicloSelecionado() != null) {
			CicloConfiguracao cc = getCicloSelecionado().getConfiguracaoByTipo(TipoConfiguracaoCiclo.DESEMP);
			return cc;
		}
		return null;
	}

	public CicloConfiguracao getCicloConfiguracaoDesenvolvimento() {
		if (getCicloSelecionado() != null) {
			CicloConfiguracao cc = getCicloSelecionado().getConfiguracaoByTipo(TipoConfiguracaoCiclo.DESENV);
			return cc;
		}
		return null;
	}

	public String getVersao() {
		try {
			ServletContext application = FacesUtil.getServletContext();
			InputStream inputStream = application.getResourceAsStream("/META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(inputStream);
			String versao = manifest.getMainAttributes().getValue("SCM-Revision");
			return versao;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
