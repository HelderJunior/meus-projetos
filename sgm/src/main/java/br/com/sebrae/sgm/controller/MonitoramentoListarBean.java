package br.com.sebrae.sgm.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Anexo;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class MonitoramentoListarBean extends BaseBean {

	@Inject
	private CicloService cicloService;

	@Inject
	private AppBean appBean;

	@Inject
	private MetaService metaService;

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(MonitoramentoListarBean.class);

	private Fase faseCicloEquipe;
	private Ciclo cicloEquipe;

	private Fase faseCicloIndividual;
	private Ciclo cicloIndividual;

	private List<Ciclo> ciclos;

	private List<Meta> metasEquipe;

	private List<Meta> metasIndividual;

	private Meta metaSelecionada;

	private int indiceAnexo;

	@Override
	public void init() {
		super.init();
		this.cicloEquipe = appBean.getCicloSelecionado();
		this.cicloIndividual = appBean.getCicloSelecionado();
	}

	public Fase getFaseCicloEquipe() {
		return faseCicloEquipe;
	}

	public void setFaseCicloEquipe(Fase faseCicloEquipe) {
		this.faseCicloEquipe = faseCicloEquipe;
	}

	public Ciclo getCicloEquipe() {
		return cicloEquipe;
	}

	public void setCicloEquipe(Ciclo cicloEquipe) {
		this.cicloEquipe = cicloEquipe;
	}

	public List<Fase> getFasesCicloExistentes() {
		List<Fase> retorno = new ArrayList<Fase>();
		retorno.add(Fase.P);
		retorno.add(Fase.R);
		return retorno;
	}

	public void setCiclos(List<Ciclo> ciclos) {
		this.ciclos = ciclos;
	}

	public List<Ciclo> getCiclos() {
		if (ciclos == null || ciclos.isEmpty()) {
			ciclos = this.cicloService.findIniciadosByUf(appBean.getUfSelecionada());
		}
		return ciclos;
	}

	public List<Meta> getMetasEquipe() {
		if (metasEquipe == null || metasEquipe.isEmpty()) {
			if (getCicloEquipe() != null) {
				metasEquipe = metaService.findAllEquipeCicloMonitoramento(appBean.getUsuarioAutenticado()
						.getUnidadeAtual(), getCicloEquipe());
			}
		}
		return metasEquipe;
	}

	public void setMetasEquipe(List<Meta> metasEquipe) {
		this.metasEquipe = metasEquipe;
	}

	public Meta getMetaSelecionada() {
		return metaSelecionada;
	}

	public void setMetaSelecionada(Meta metaSelecionada) {
		this.metaSelecionada = metaSelecionada;
	}

	public Fase getFaseCicloIndividual() {
		return faseCicloIndividual;
	}

	public void setFaseCicloIndividual(Fase faseCicloIndividual) {
		this.faseCicloIndividual = faseCicloIndividual;
	}

	public Ciclo getCicloIndividual() {
		return cicloIndividual;
	}

	public void setCicloIndividual(Ciclo cicloIndividual) {
		this.cicloIndividual = cicloIndividual;
	}

	public List<Meta> getMetasIndividual() {
		if (metasIndividual == null || metasIndividual.isEmpty()) {
			if (getCicloIndividual() != null) {
				metasIndividual = metaService.findAllIndividuaisCicloMonitoramento(appBean.getUsuarioAutenticado(),
						getCicloIndividual());
			}
		}
		return metasIndividual;
	}

	public void setMetasIndividual(List<Meta> metasIndividual) {
		this.metasIndividual = metasIndividual;
	}

	public void uploadArquivo(FileUploadEvent event) {
		try {
			String fileName = FilenameUtils.getName(event.getFile().getFileName());
			if (this.getMetaSelecionada().getAnexosEvidenciaEntrega() == null) {
				this.getMetaSelecionada().setAnexosEvidenciaEntrega(new ArrayList<Anexo>());
			}
			Anexo an = new Anexo();
			an.setNome(RandomStringUtils.random(8, true, true) + fileName);
			an.setNomeExibicao(fileName);

			byte[] fileBytes = IOUtils.toByteArray(event.getFile().getInputstream());
			an.setTipo(event.getFile().getContentType());
			an.setArquivo(fileBytes);
			FileUtils
					.writeByteArrayToFile(new File(Constants.FILES_TMP_DIR + File.separator + an.getNome()), fileBytes);
			an.setMeta(this.metaSelecionada);
			this.metaSelecionada.getAnexosEvidenciaEntrega().add(an);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_anexo", "Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirArquivo() {
		this.getMetaSelecionada().getAnexosEvidenciaEntrega().remove(this.indiceAnexo);
	}

	public StreamedContent getArquivo() {
		StreamedContent file = null;
		try {
			Anexo a = this.getMetaSelecionada().getAnexosEvidenciaEntrega().get(indiceAnexo);
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

	public int getIndiceAnexo() {
		return indiceAnexo;
	}

	public void setIndiceAnexo(int indiceAnexo) {
		this.indiceAnexo = indiceAnexo;
	}

	public void alterouFaseEquipe() {

	}

	public void alterouFaseIndividual() {

	}

	public void alterouCicloEquipe() {
		try {
			this.metasEquipe = null;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void alterouCicloIndividual() {
		try {
			this.metasIndividual = null;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void salvarMetaAtual() {
		try {
			this.metaService.save(this.metaSelecionada);
			FacesUtil.addInfoMessage("Meta salva com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_anexo", "Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}

	}

	public void confirmarEnviarAprovacao() {
		try {
			if (this.metaSelecionada.getTipo() == TipoMeta.E) {
				metaService.enviarMetaValidacao(this.metaSelecionada, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
				//this.metasEquipe.remove(this.metaSelecionada);
			} else if (this.metaSelecionada.getTipo() == TipoMeta.I) {
				metaService.enviarMetaValidacao(this.metaSelecionada, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
				//this.metasIndividual.remove(this.metaSelecionada);
			}
			FacesUtil.addInfoMessage("Meta enviada para aprova\u00E7\u00E3o com sucesso!");
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage("form_enviar", ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_enviar", "Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

}
