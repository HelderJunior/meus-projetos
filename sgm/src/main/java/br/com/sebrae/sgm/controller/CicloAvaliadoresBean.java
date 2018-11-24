package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.dto.ArvoreUnidadesDTO;
import br.com.sebrae.sgm.model.AvaliadorCiclo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.service.AvaliadorCicloService;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.PerfilService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioPerfilService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class CicloAvaliadoresBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloAvaliadoresBean.class);

	@Inject
	private PerfilService perfilService;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private CicloService cicloService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private AvaliadorCicloService avaliadorService;

	@Inject
	private AppBean appBean;

	@Inject
	private UsuarioPerfilService usuarioPerfilService;

	@Inject
	private ManterCicloBean manterCicloBean;

	private CicloConfiguracao cicloConfiguracao;

	private CicloConfiguracao outroCicloConfiguracao;

	private AvaliadorCiclo avaliador;

	private List<Usuario> usuariosAvaliadores;
	private List<Perfil> perfisAvaliadores;
	private Perfil perfilAvaliador;
	private Usuario usuarioAvaliador;
	private Unidade unidadeSelecionada;

	private ArvoreUnidadesDTO arvoreUnidades = new ArvoreUnidadesDTO();

	private Boolean visualizando = Boolean.FALSE;

	private Boolean todosExpandidos = Boolean.FALSE;

	@Override
	public void init() {
		super.init();
	}

	public void inserirAlterar() {
		inserirAlterar2(this.cicloConfiguracao);

		if (outroCicloConfiguracao != null) {
			inserirAlterar2(outroCicloConfiguracao);
			this.outroCicloConfiguracao.setStatusConfiguracaoAvaliadores(StatusConfiguracao.C);
		}

		this.cicloConfiguracao.setStatusConfiguracaoAvaliadores(StatusConfiguracao.C);

		try {
			cicloConfiguracaoService.save(cicloConfiguracao);
			cicloService.save(cicloConfiguracao.getCiclo());
		} catch (Exception e) {
			// do nothing
		}

		reset();
	}

	private void inserirAlterar2(CicloConfiguracao cc) {
		try {
			List<AvaliadorCiclo> avaliadoresVinculados = avaliadorService.findByParameters(cc, this.perfilAvaliador,
					this.unidadeSelecionada, this.usuarioAvaliador);
			if (avaliadoresVinculados == null || avaliadoresVinculados.isEmpty()) {
				AvaliadorCiclo av = new AvaliadorCiclo();
				av.setCicloConfiguracao(cc);
				av.setPerfil(this.perfilAvaliador);
				av.setUnidade(this.unidadeSelecionada);
				av.setUsuario(this.usuarioAvaliador);
				this.avaliadorService.insert(av);
				if (cc.getAvaliadores() == null) {
					cc.setAvaliadores(new ArrayList<AvaliadorCiclo>());
				}
				cc.getAvaliadores().add(av);
			} else {
				for (AvaliadorCiclo av : avaliadoresVinculados) {
					av.setPerfil(this.perfilAvaliador);
					av.setUnidade(this.unidadeSelecionada);
					av.setUsuario(this.usuarioAvaliador);
					this.avaliadorService.update(av);
				}
			}
			FacesUtil.addInfoMessage("Avaliador adicionado com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_perfil_interno",
					"Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluir() {
		try {
			this.cicloConfiguracao = this.avaliador.getCicloConfiguracao();
			this.cicloConfiguracao.getAvaliadores().remove(this.avaliador);
			cicloConfiguracaoService.save(this.cicloConfiguracao);
			try {
				this.avaliadorService.delete(this.avaliador);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			this.unidadeSelecionada.setGerenteTemp(null);
			this.unidadeSelecionada.setGerenteAdjuntoTemp(null);
			this.unidadeSelecionada.setDiretorTemp(null);
			this.unidadeSelecionada.setAssessorTemp(null);
			this.unidadeSelecionada.setChefeGabineteTemp(null);
			excluirDoOutro();
			reset();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirDoOutro() {
		try {
			if (outroCicloConfiguracao != null && this.avaliador != null) {
				List<AvaliadorCiclo> avaliadoresVinculados = avaliadorService.findByParameters(outroCicloConfiguracao,
						this.avaliador.getPerfil(), this.avaliador.getUnidade(), this.avaliador.getUsuario());

				if (avaliadoresVinculados != null && !avaliadoresVinculados.isEmpty()) {
					for (AvaliadorCiclo avaliadorCiclo : avaliadoresVinculados) {
						this.outroCicloConfiguracao.getAvaliadores().remove(avaliadorCiclo);
						cicloConfiguracaoService.save(this.outroCicloConfiguracao);
						try {
							this.avaliadorService.delete(avaliadorCiclo);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void reset() {
		this.perfilAvaliador = null;
		this.usuarioAvaliador = null;
		this.avaliador = null;
	}

	public List<Usuario> getUsuariosAvaliadores() {
		this.usuariosAvaliadores = new ArrayList<Usuario>();
		if (this.unidadeSelecionada != null && this.perfilAvaliador != null) {
			usuariosAvaliadores = usuarioPerfilService.findUsuariosCandidatosAvaliadores(this.unidadeSelecionada,
					this.perfilAvaliador);
		}
		return usuariosAvaliadores;
	}

	public void setUsuariosAvaliadores(List<Usuario> usuariosAvaliadores) {
		this.usuariosAvaliadores = usuariosAvaliadores;
	}

	public List<Perfil> getPerfisAvaliadores() {
		if (perfisAvaliadores == null || perfisAvaliadores.isEmpty()) {
			perfisAvaliadores = perfilService.bindByChaves(Arrays.asList(Perfil.PERFIS_CHEFIA));
		}
		return perfisAvaliadores;
	}

	public void setPerfisAvaliadores(List<Perfil> perfisAvaliadores) {
		this.perfisAvaliadores = perfisAvaliadores;
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public Perfil getPerfilAvaliador() {
		return perfilAvaliador;
	}

	public void setPerfilAvaliador(Perfil perfilAvaliador) {
		this.perfilAvaliador = perfilAvaliador;
	}

	public Usuario getUsuarioAvaliador() {
		return usuarioAvaliador;
	}

	public void setUsuarioAvaliador(Usuario usuarioAvaliador) {
		this.usuarioAvaliador = usuarioAvaliador;
	}

	public Unidade getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	private void atualizarArvore() {
		List<AvaliadorCiclo> avaliadores = avaliadorService.findAvaliadoresByCiclo(this.cicloConfiguracao);
		for (AvaliadorCiclo av : avaliadores) {
			if (av.getPerfil().getChave().equals(Perfil.GERENTE)) {
				if (getArvoreUnidades().getUnidades().contains(av.getUnidade())) {
					Unidade un = getArvoreUnidades().getUnidades().get(
							getArvoreUnidades().getUnidades().indexOf(av.getUnidade()));
					un.setGerenteTemp(av);
				}
			}

			if (av.getPerfil().getChave().equals(Perfil.GERENTE_ADJUNTO)) {
				if (getArvoreUnidades().getUnidades().contains(av.getUnidade())) {
					Unidade un = getArvoreUnidades().getUnidades().get(
							getArvoreUnidades().getUnidades().indexOf(av.getUnidade()));
					un.setGerenteAdjuntoTemp(av);
				}
			}

			if (av.getPerfil().getChave().equals(Perfil.DIRETOR)) {
				if (getArvoreUnidades().getUnidades().contains(av.getUnidade())) {
					Unidade un = getArvoreUnidades().getUnidades().get(
							getArvoreUnidades().getUnidades().indexOf(av.getUnidade()));
					un.setDiretorTemp(av);
				}
			}

			if (av.getPerfil().getChave().equals(Perfil.ASSESSOR)) {
				if (getArvoreUnidades().getUnidades().contains(av.getUnidade())) {
					Unidade un = getArvoreUnidades().getUnidades().get(
							getArvoreUnidades().getUnidades().indexOf(av.getUnidade()));
					un.setAssessorTemp(av);
				}
			}
			if (av.getPerfil().getChave().equals(Perfil.CHEFE_GABINETE)) {
				if (getArvoreUnidades().getUnidades().contains(av.getUnidade())) {
					Unidade un = getArvoreUnidades().getUnidades().get(
							getArvoreUnidades().getUnidades().indexOf(av.getUnidade()));
					un.setChefeGabineteTemp(av);
				}
			}
		}
	}

	public AvaliadorCiclo getAvaliador() {
		return avaliador;
	}

	public void setAvaliador(AvaliadorCiclo avaliador) {
		this.avaliador = avaliador;
	}

	public ArvoreUnidadesDTO getArvoreUnidades() {
		return arvoreUnidades;
	}

	public void setArvoreUnidades(ArvoreUnidadesDTO arvoreUnidades) {
		this.arvoreUnidades = arvoreUnidades;
	}

	public void popularArvores() {
		if (arvoreUnidades == null || arvoreUnidades.getRoot() == null) {
			arvoreUnidades = unidadeService.getUnidadesTree(getCicloConfiguracao().getCiclo().getUf());
		}
		arvoreUnidades.limparCache();
		atualizarArvore();
	}

	public Boolean getVisualizando() {
		return visualizando;
	}

	public void setVisualizando(Boolean visualizando) {
		this.visualizando = visualizando;
	}

	public Boolean getTodosExpandidos() {
		return todosExpandidos;
	}

	public void setTodosExpandidos(Boolean todosExpandidos) {
		this.todosExpandidos = todosExpandidos;
	}

	public void onNodeExpand(NodeExpandEvent event) {
		event.getTreeNode().setExpanded(true);
	}

	public void onNodeCollapse(NodeCollapseEvent event) {
		event.getTreeNode().setExpanded(false);
	}

	public CicloConfiguracao getOutroCicloConfiguracao() {
		return outroCicloConfiguracao;
	}

	public void setOutroCicloConfiguracao(CicloConfiguracao outroCicloConfiguracao) {
		this.outroCicloConfiguracao = outroCicloConfiguracao;
	}

	public String continuarConfiguracao(){
		FacesUtil.addInfoMessage("Configura\u00E7\u00E3o realizada com sucesso.");
		return "/pages/ciclo/manter.xhtml";
	}
	public String mensagemSucesso(){
		FacesUtil.addInfoMessage("Par\u00E2metros salvos com sucesso!");
		return "/pages/ciclo/manter.xhtml";
	}
}
