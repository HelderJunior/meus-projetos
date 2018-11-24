package br.com.sebrae.sgm.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Anexo;
import br.com.sebrae.sgm.model.Categoria;
import br.com.sebrae.sgm.model.FormaAquisicao;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.MetaStatus;
import br.com.sebrae.sgm.model.ObjetivoEstrategico;
import br.com.sebrae.sgm.model.RMCompetencia;
import br.com.sebrae.sgm.model.SolucaoEducacional;
import br.com.sebrae.sgm.model.SolucaoEducacionalMeta;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.StatusSolucaoEducacional;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.VinculoOcupacional;
import br.com.sebrae.sgm.service.CategoriaService;
import br.com.sebrae.sgm.service.FormaAquisicaoService;
import br.com.sebrae.sgm.service.LogService;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.ObjetivoEstrategicoService;
import br.com.sebrae.sgm.service.RMCompetenciaService;
import br.com.sebrae.sgm.service.SolucaoEducacionalMetaService;
import br.com.sebrae.sgm.service.SolucaoEducacionalService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class GestorValidacaoDesenvolvimentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(GestorValidacaoDesenvolvimentoBean.class);

	@Inject
	private AppBean appBean;
	@Inject
	private MetaService metaService;
	@Inject
	private CategoriaService categoriaService;
	@Inject
	private FormaAquisicaoService formaAquisicaoService;
	@Inject
	private RMCompetenciaService competenciaService;
	@Inject
	private ObjetivoEstrategicoService objetivoEstrategicoService;
	@Inject
	private SolucaoEducacionalService solucaoEducacionalService;

	@Inject
	private GestorGestaoMetasDesenvolvimentoNomeBean gestorGestaoMetasNomeBean;

	@Inject
	private LogService logService;
	
	@Inject
	private SolucaoEducacionalMetaService solucaoEducacionalMetaService;

	private List<Meta> metasEquipeVincular = new ArrayList<Meta>();
	private List<Meta> metasIndividualVincular = new ArrayList<Meta>();
	private List<RMCompetencia> competenciasVincular = new ArrayList<RMCompetencia>();
	private List<ObjetivoEstrategico> objetivosEstrategicosVincular = new ArrayList<ObjetivoEstrategico>();

	private Meta meta;

	private SolucaoEducacionalMeta solucaoSelecionada;
	private Categoria categoria;
	private List<Categoria> categorias;
	private FormaAquisicao formaAquisicao;
	private List<FormaAquisicao> formasAquisicao = new ArrayList<FormaAquisicao>();

	private int indiceAnexo;

	private List<Object[]> registrosLog;

	@Override
	public void init() {
		super.init();
		if (this.meta == null) {
			this.meta = new Meta();
			this.meta.setColaborador((Usuario) appBean.getUsuarioAutenticado());
			this.meta.setStatus(new ArrayList<MetaStatus>());
			br.com.sebrae.sgm.model.MetaStatus ms = new MetaStatus();
			ms.setFase(Fase.P);
			ms.setStatus(StatusMeta.SA);
			ms.setMeta(this.meta);
			this.meta.adicionarMetaStatus(ms);
			if (this.appBean.getUsuarioAutenticado().getUnidades() != null && !this.appBean.getUnidadesUf().isEmpty()) {
				this.meta.setUnidade(this.appBean.getUnidadesUf().get(0));
			}
			this.meta.setMetasInviduaisEquipeVinculadas(new ArrayList<Meta>());
			this.meta.setCompetencias(new ArrayList<RMCompetencia>());
			this.meta.setObjetivosEstrategicos(new ArrayList<ObjetivoEstrategico>());
			this.meta.setCicloConfiguracao(this.appBean.getCicloConfiguracaoDesenvolvimento());
		} else {
			getMetasEquipeVincular();
			getMetasIndividualVincular();

			if (this.meta.getMetasInviduaisEquipeVinculadas() != null
					&& !this.meta.getMetasInviduaisEquipeVinculadas().isEmpty()
					&& (this.metasEquipeVincular != null || this.metasIndividualVincular != null)) {
				List<Meta> metasIndividualEquipeVinculadas = this.meta.getMetasInviduaisEquipeVinculadas();

				for (Meta meta : metasIndividualEquipeVinculadas) {
					if (metasEquipeVincular != null)
						if (metasEquipeVincular.contains(meta)) {
							metasEquipeVincular.get(metasEquipeVincular.indexOf(meta)).setSelecionado(Boolean.TRUE);
						}
					if (metasIndividualVincular != null)
						if (metasIndividualVincular.contains(meta)) {
							metasIndividualVincular.get(metasIndividualVincular.indexOf(meta)).setSelecionado(
									Boolean.TRUE);
						}
				}
			}

			getCompetenciasVincular();
			if (this.meta.getCompetencias() != null && !this.meta.getCompetencias().isEmpty()
					&& this.competenciasVincular != null && !this.competenciasVincular.isEmpty()) {
				List<RMCompetencia> competencias = this.meta.getCompetencias();
				for (RMCompetencia rmCompetencia : competencias) {
					if (this.competenciasVincular.contains(rmCompetencia)) {
						this.competenciasVincular.get(competenciasVincular.indexOf(rmCompetencia)).setSelecionado(
								Boolean.TRUE);
					}
				}
			}

			getObjetivosEstrategicosVincular();

			if (this.meta.getObjetivosEstrategicos() != null && !this.meta.getObjetivosEstrategicos().isEmpty()
					&& this.objetivosEstrategicosVincular != null && !this.objetivosEstrategicosVincular.isEmpty()) {
				List<ObjetivoEstrategico> objetivosEstrategicos = this.meta.getObjetivosEstrategicos();

				for (ObjetivoEstrategico objetivoEstrategico : objetivosEstrategicos) {
					if (this.objetivosEstrategicosVincular.contains(objetivoEstrategico)) {
						this.objetivosEstrategicosVincular.get(
								objetivosEstrategicosVincular.indexOf(objetivoEstrategico))
								.setSelecionado(Boolean.TRUE);
					}
				}
			}
		}
	}

	public String salvarSemValidar() {
		try {
			this.metaService.save(this.meta);
			FacesUtil.addInfoMessage("Meta salva com sucesso.");
			return appBean.back();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a corre\u00E7\u00E3o, tente novamente mais tarde.");
		}
		return "";
	}

	public String enviarComite() {
		try {
			this.metaService.enviarMetaParaComite(meta, meta.getCicloConfiguracao().getTipoConfiguracao(), TipoGrupo.C);
			FacesUtil.addInfoMessage("Meta enviada para Comit\u00EA  com sucesso.");
			if (gestorGestaoMetasNomeBean != null && gestorGestaoMetasNomeBean.getMetas() != null) {
				gestorGestaoMetasNomeBean.getMetas().remove(meta);
			}
			if (gestorGestaoMetasNomeBean != null && gestorGestaoMetasNomeBean.getGerenciaMetaDto() != null) {
				if (gestorGestaoMetasNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao() != null
						&& !gestorGestaoMetasNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao().isEmpty()) {
					
					gestorGestaoMetasNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao().remove(meta);
					gestorGestaoMetasNomeBean.getGerenciaMetaDto().setQtdPendenteAprovacao(
							gestorGestaoMetasNomeBean.getGerenciaMetaDto().getQtdPendenteAprovacao() - 1);
					gestorGestaoMetasNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao()
							.removeAll(this.meta.getSolucoesEducacionais());
					gestorGestaoMetasNomeBean.getGerenciaMetaDto().setQtdSolucoesPendenteAprovacao(
							gestorGestaoMetasNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao().size()
									- this.meta.getSolucoesEducacionais().size());
					
					if(gestorGestaoMetasNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao().size() < 0){
						gestorGestaoMetasNomeBean.getGerenciaMetaDto().setQtdSolucoesPendenteAprovacao(0);
					}
				}
			}
			return appBean.back();
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a operação, tente novamente mais tarde.");
		}
		return "";
	}

	public String validarResultado() {
		try {
			for (SolucaoEducacionalMeta sm : meta.getSolucoesEducacionais()) {
				if (sm != null)
					sm.setStatus(StatusSolucaoEducacional.V);
			}
			this.metaService.aprovarMeta(meta, meta.getCicloConfiguracao().getTipoConfiguracao(), TipoGrupo.C);
			FacesUtil.addInfoMessage("Meta aprovada com sucesso.");

			if (gestorGestaoMetasNomeBean != null && gestorGestaoMetasNomeBean.getMetas() != null) {
				gestorGestaoMetasNomeBean.getMetas().remove(meta);
			}

			if (gestorGestaoMetasNomeBean != null && gestorGestaoMetasNomeBean.getGerenciaMetaDto() != null) {
				if (gestorGestaoMetasNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao() != null
						&& !gestorGestaoMetasNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao().isEmpty()) {
					gestorGestaoMetasNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao().remove(meta);
					gestorGestaoMetasNomeBean.getGerenciaMetaDto().setQtdPendenteAprovacao(
							gestorGestaoMetasNomeBean.getGerenciaMetaDto().getQtdPendenteAprovacao() - 1);
					gestorGestaoMetasNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao()
							.removeAll(this.meta.getSolucoesEducacionais());
					gestorGestaoMetasNomeBean.getGerenciaMetaDto().setQtdSolucoesPendenteAprovacao(
							gestorGestaoMetasNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao().size()
									- this.meta.getSolucoesEducacionais().size());
					
					if(gestorGestaoMetasNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao().size() < 0){
						gestorGestaoMetasNomeBean.getGerenciaMetaDto().setQtdSolucoesPendenteAprovacao(0);
					}
				}
			}
			return appBean.back();
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a operação, tente novamente mais tarde.");
		}
		return "";
	}

	public String naoValidar() {
		try {
			this.metaService.reprovarMeta(meta, meta.getCicloConfiguracao().getTipoConfiguracao(), TipoGrupo.C);
			FacesUtil.addInfoMessage("Meta n\u00E3o validada com sucesso.");
			if (gestorGestaoMetasNomeBean != null && gestorGestaoMetasNomeBean.getMetas() != null) {
				gestorGestaoMetasNomeBean.getMetas().remove(meta);
			}
			if (gestorGestaoMetasNomeBean != null && gestorGestaoMetasNomeBean.getGerenciaMetaDto() != null) {
				if (gestorGestaoMetasNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao() != null
						&& !gestorGestaoMetasNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao().isEmpty()) {
					
					gestorGestaoMetasNomeBean.getGerenciaMetaDto().getMetasPendenteAprovacao().remove(meta);
					gestorGestaoMetasNomeBean.getGerenciaMetaDto().setQtdPendenteAprovacao(
							gestorGestaoMetasNomeBean.getGerenciaMetaDto().getQtdPendenteAprovacao() - 1);
					gestorGestaoMetasNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao()
							.removeAll(this.meta.getSolucoesEducacionais());
					gestorGestaoMetasNomeBean.getGerenciaMetaDto().setQtdSolucoesPendenteAprovacao(
							gestorGestaoMetasNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao().size()
									- this.meta.getSolucoesEducacionais().size());
					
					if(gestorGestaoMetasNomeBean.getGerenciaMetaDto().getSolucoesPendenteAprovacao().size() < 0){
						gestorGestaoMetasNomeBean.getGerenciaMetaDto().setQtdSolucoesPendenteAprovacao(0);
					}
				}
			}
			return appBean.back();
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a operação, tente novamente mais tarde.");
		}
		return "";
	}

	public void prepararAdicaoSolucaoEducacional() {
		this.solucaoSelecionada = new SolucaoEducacionalMeta();
		this.solucaoSelecionada.setSolucaoEducacional(new SolucaoEducacional());
		this.solucaoSelecionada.setMeta(this.meta);
	}

	public void prepararVisualizacaoSolucaoEducacional() {
		try {
			this.categoria = this.getSolucaoSelecionada().getSolucaoEducacional().getFormaAquisicao().getCategoria();
			this.formaAquisicao = this.getSolucaoSelecionada().getSolucaoEducacional().getFormaAquisicao();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public StreamedContent getArquivo() {
		StreamedContent file = null;
		try {
			Anexo a = this.getSolucaoSelecionada().getAnexos().get(indiceAnexo);
			if (a.getArquivo() != null) {
				ByteArrayInputStream fis;
				fis = new ByteArrayInputStream(a.getArquivo());
				file = new DefaultStreamedContent(fis, a.getTipo(), a.getNome());
			} else {
				FacesUtil.addErrorMessage("Nenhum arquivo a ser exibido.");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return file;
	}

	public StreamedContent getArquivo2() {
		StreamedContent file = null;
		try {
			Anexo a = appBean.getCicloConfiguracaoDesenvolvimento().getConfiguracaoFormaAquisicao().getAnexos()
					.get(indiceAnexo);
			if (a.getArquivo() != null) {
				ByteArrayInputStream fis;
				fis = new ByteArrayInputStream(a.getArquivo());
				file = new DefaultStreamedContent(fis, a.getTipo(), a.getNome());
			} else {
				FacesUtil.addErrorMessage("Nenhum arquivo a ser exibido.");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return file;
	}

	public void obterRegistrosLog() {
		try {
			this.registrosLog = this.logService.findByIdMeta(this.meta.getId());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar opera\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}
	
	public void prepararEnviarObservacaoSolucaoSelecionada() {
		this.solucaoSelecionada.setObservacao("");
	}
	
	public void enviarObservacaoAndValidarSolucaoSelecionada() {
		try {
			if (this.solucaoSelecionada.getStatus() == StatusSolucaoEducacional.V) {
				FacesUtil.addErrorMessage("A Solu\u00E7\u00E3o Educacional j\u00E1 se encontra validada.");
			} else {
				this.solucaoSelecionada.setStatus(StatusSolucaoEducacional.V);
				solucaoEducacionalMetaService.atualizar(solucaoSelecionada);
				FacesUtil.addInfoMessage("Solu\u00E7\u00E3o Educacional validada com sucesso.");
				getCategorias();
				getFormasAquisicao();
				getSolucoesEducacionais();
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar opera\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<VinculoOcupacional> getVinculosOcupacionais() {
		return Arrays.asList(VinculoOcupacional.values());
	}

	public List<RMCompetencia> getCompetenciasVincular() {
		if (this.competenciasVincular == null || this.competenciasVincular.isEmpty()) {
			this.competenciasVincular = competenciaService.findAllCompetenciaPorColaborador(this.appBean.getColaboradorSelecionado(), this.appBean
					.getCicloConfiguracaoDesenvolvimento().getCiclo().getVigencia());
		}
		return competenciasVincular;
	}

	public void setCompetenciasVincular(List<RMCompetencia> competenciasVincular) {
		this.competenciasVincular = competenciasVincular;
	}

	public List<ObjetivoEstrategico> getObjetivosEstrategicosVincular() {
		if (this.objetivosEstrategicosVincular == null || this.objetivosEstrategicosVincular.isEmpty()) {
			this.objetivosEstrategicosVincular = this.objetivoEstrategicoService.findAll();
		}
		return objetivosEstrategicosVincular;
	}

	public void setObjetivosEstrategicosVincular(List<ObjetivoEstrategico> objetivosEstrategicosVincular) {
		this.objetivosEstrategicosVincular = objetivosEstrategicosVincular;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public List<Meta> getMetasEquipeVincular() {
		if (this.metasEquipeVincular == null || this.metasEquipeVincular.isEmpty()) {
			this.metasEquipeVincular = metaService.findAllEquipeByCicloMetaAprovada(this.meta.getCicloConfiguracao().getCiclo());
		}
		return metasEquipeVincular;
	}

	public void setMetasEquipeVincular(List<Meta> metasEquipeVincular) {
		this.metasEquipeVincular = metasEquipeVincular;
	}

	public List<Meta> getMetasIndividualVincular() {
		if (this.metasIndividualVincular == null || this.metasIndividualVincular.isEmpty()) {
			this.metasIndividualVincular = metaService.findAllIndividuaisCicloAprovada(appBean.getUsuarioAutenticado(),
					this.meta.getCicloConfiguracao().getCiclo());
		}
		return metasIndividualVincular;
	}

	public void setMetasIndividualVincular(List<Meta> metasIndividualVincular) {
		this.metasIndividualVincular = metasIndividualVincular;
	}

	public SolucaoEducacionalMeta getSolucaoSelecionada() {
		return solucaoSelecionada;
	}

	public void setSolucaoSelecionada(SolucaoEducacionalMeta solucaoSelecionada) {
		this.solucaoSelecionada = solucaoSelecionada;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public List<Categoria> getCategorias() {
		if (categorias == null || categorias.isEmpty()) {
			categorias = categoriaService.findAll();
		}
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	public List<FormaAquisicao> getFormasAquisicao() {
		if (categoria != null && (formasAquisicao == null || formasAquisicao.isEmpty())) {
			formasAquisicao = formaAquisicaoService.findByCategoria(this.categoria);
		}
		return formasAquisicao;
	}

	public List<SolucaoEducacional> getSolucoesEducacionais() {
		List<SolucaoEducacional> retorno = new ArrayList<SolucaoEducacional>();
		if (this.formaAquisicao != null) {
			retorno = this.solucaoEducacionalService.findByFormaAquisicao(this.formaAquisicao);
		}
		return retorno;
	}

	public void setFormasAquisicao(List<FormaAquisicao> formasAquisicao) {
		this.formasAquisicao = formasAquisicao;
	}

	public int getIndiceAnexo() {
		return indiceAnexo;
	}

	public void setIndiceAnexo(int indiceAnexo) {
		this.indiceAnexo = indiceAnexo;
	}

	public FormaAquisicao getFormaAquisicao() {
		return formaAquisicao;
	}

	public void setFormaAquisicao(FormaAquisicao formaAquisicao) {
		this.formaAquisicao = formaAquisicao;
	}

	public List<Object[]> getRegistrosLog() {
		return registrosLog;
	}

	public void setRegistrosLog(List<Object[]> registrosLog) {
		this.registrosLog = registrosLog;
	}

}
