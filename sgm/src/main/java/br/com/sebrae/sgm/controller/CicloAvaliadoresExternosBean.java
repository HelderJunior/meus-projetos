package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.SessionScoped;
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
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;

@SessionScoped
@Named
public class CicloAvaliadoresExternosBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloAvaliadoresExternosBean.class);

	@Inject
	private PerfilService perfilService;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private AvaliadorCicloService avaliadorService;

	@Inject
	private AppBean appBean;

	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;

	private AvaliadorCiclo avaliador;

	private List<Usuario> usuariosAvaliadores;
	private List<Perfil> perfisAvaliadores;
	private Perfil perfilAvaliador;
	private Usuario usuarioAvaliador;
	private Unidade unidadeSelecionada;

	private Boolean visualizando = Boolean.FALSE;
	private ArvoreUnidadesDTO arvoreUnidades = new ArvoreUnidadesDTO();

	private Boolean todosExpandidos = Boolean.FALSE;

	@Override
	public void init() {
		super.init();
	}

	public void inserirAlterar() {
		try {
			List<AvaliadorCiclo> avaliadoresVinculados = avaliadorService.findByParameters(this.cicloConfiguracao,
					this.perfilAvaliador, this.unidadeSelecionada, this.usuarioAvaliador);
			if (avaliadoresVinculados == null || avaliadoresVinculados.isEmpty()) {
				AvaliadorCiclo av = new AvaliadorCiclo();
				av.setCicloConfiguracao(this.cicloConfiguracao);
				av.setPerfil(this.perfilAvaliador);
				av.setUnidade(this.unidadeSelecionada);
				av.setUsuario(this.usuarioAvaliador);
				this.avaliadorService.insert(av);
				this.cicloConfiguracao.setStatusConfiguracaoAvaliadores(StatusConfiguracao.C);
				if (this.cicloConfiguracao.getAvaliadores() == null) {
					this.cicloConfiguracao.setAvaliadores(new ArrayList<AvaliadorCiclo>());
				}
				this.cicloConfiguracao.getAvaliadores().add(av);
			} else {
				for (AvaliadorCiclo av : avaliadoresVinculados) {
					av.setPerfil(this.perfilAvaliador);
					av.setUnidade(this.unidadeSelecionada);
					av.setUsuario(this.usuarioAvaliador);
					this.avaliadorService.update(av);
				}
			}
			FacesUtil.addInfoMessage("Avaliador adicionado com sucesso.");
			try {
				cicloConfiguracaoService.save(cicloConfiguracao);
				cicloService.save(cicloConfiguracao.getCiclo());
			} catch (Exception e) {
				// do nothing
			}
			reset();
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

			this.unidadeSelecionada.setComiteTemp(null);
			this.unidadeSelecionada.setAvaliadorExternoTemp(null);
			this.unidadeSelecionada.setAuditorTemp(null);
			reset();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
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

		if (this.unidadeSelecionada != null) {
			this.usuariosAvaliadores = usuarioService.findExternosByPerfilUF(this.perfilAvaliador, this.getCicloConfiguracao().getCiclo().getUf());
		}

		return usuariosAvaliadores;
	}

	public void setUsuariosAvaliadores(List<Usuario> usuariosAvaliadores) {
		this.usuariosAvaliadores = usuariosAvaliadores;
	}

	public List<Perfil> getPerfisAvaliadores() {
		if (perfisAvaliadores == null || perfisAvaliadores.isEmpty()) {
			perfisAvaliadores = perfilService.bindByChaves(Arrays.asList(Perfil.PERFIS_AVALIADORES_EXTERNO_EXCETO_AVALIADOR_EXTERNO));
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

	public Boolean getVisualizando() {
		return visualizando;
	}

	public void setVisualizando(Boolean visualizando) {
		this.visualizando = visualizando;
	}

	public Perfil getPerfilAvaliador() {
		return perfilAvaliador;
	}

	public void setPerfilAvaliador(Perfil perfilAvaliador) {
		this.perfilAvaliador = perfilAvaliador;
	}

	public Unidade getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	private void atualizarArvore() {
		List<AvaliadorCiclo> avaliadores = avaliadorService.findAvaliadoresExternosByCiclo(this.cicloConfiguracao);
		for (AvaliadorCiclo av : avaliadores) {
			if (av.getPerfil().getChave().equals(Perfil.COMITE)) {
				if (getArvoreUnidades().getUnidades().contains(av.getUnidade())) {
					Unidade un = getArvoreUnidades().getUnidades().get(
							getArvoreUnidades().getUnidades().indexOf(av.getUnidade()));

					un.setComiteTemp(av);
				}
			}

			if (av.getPerfil().getChave().equals(Perfil.AUDITOR)) {
				if (getArvoreUnidades().getUnidades().contains(av.getUnidade())) {
					Unidade un = getArvoreUnidades().getUnidades().get(
							getArvoreUnidades().getUnidades().indexOf(av.getUnidade()));
					un.setAuditorTemp(av);
				}
			}

			if (av.getPerfil().getChave().equals(Perfil.AVALIADOR_EXTERNO)) {
				if (getArvoreUnidades().getUnidades().contains(av.getUnidade())) {
					Unidade un = getArvoreUnidades().getUnidades().get(
							getArvoreUnidades().getUnidades().indexOf(av.getUnidade()));
					un.setAvaliadorExternoTemp(av);
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

	public Usuario getUsuarioAvaliador() {
		return usuarioAvaliador;
	}

	public void setUsuarioAvaliador(Usuario usuarioAvaliador) {
		this.usuarioAvaliador = usuarioAvaliador;
	}

	public ArvoreUnidadesDTO getArvoreUnidades() {
		return arvoreUnidades;
	}

	public void setArvoreUnidades(ArvoreUnidadesDTO arvoreUnidades) {
		this.arvoreUnidades = arvoreUnidades;
	}

	public void popularArvoreUnidaes() {
		if (arvoreUnidades == null || arvoreUnidades.getRoot() == null) {
			arvoreUnidades = unidadeService.getUnidadesTree(cicloConfiguracao.getCiclo().getUf());
		}
		arvoreUnidades.limparCache();
		atualizarArvore();
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

}
