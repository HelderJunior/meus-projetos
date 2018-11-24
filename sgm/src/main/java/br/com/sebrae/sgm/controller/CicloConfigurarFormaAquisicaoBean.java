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

import br.com.sebrae.sgm.model.Anexo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfigurarFormaAquisicao;
import br.com.sebrae.sgm.model.FormaAquisicao;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.FormaAquisicaoService;
import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class CicloConfigurarFormaAquisicaoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloConfigurarFormaAquisicaoBean.class);

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private FormaAquisicaoService formaAquisicaoService;
	
	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;

	private Boolean visualizando = Boolean.FALSE;

	private ConfigurarFormaAquisicao formaAquisicao;

	private int indiceAnexo;

	private List<FormaAquisicao> formasAquisicao;
	
	private Boolean selecionarTodos = Boolean.FALSE;
	
	@Override
	public void init() {
		super.init();
		this.selecionarTodos = false;
	}

	public String salvar() {
		try {
			
			if(this.formaAquisicao.getFormasAquisicao() != null && !this.formaAquisicao.getFormasAquisicao().isEmpty()){
				this.formaAquisicao.getFormasAquisicao().clear();
			}else{
				this.formaAquisicao.setFormasAquisicao(new ArrayList<FormaAquisicao>());
			}
			
			for (FormaAquisicao fa : formasAquisicao) {
				if(fa.getSelecionado()){
					this.formaAquisicao.getFormasAquisicao().add(fa);
				}
			}
			
			this.cicloConfiguracao.setConfiguracaoFormaAquisicao(this.formaAquisicao);
			this.cicloConfiguracao.setStatusConfiguracaoFormaAquisicao(StatusConfiguracao.C);
			this.cicloConfiguracao.getConfiguracaoFormaAquisicao().setId(this.cicloConfiguracao.getId());
			
			try {
				cicloConfiguracaoService.save(cicloConfiguracao);
				cicloService.save(cicloConfiguracao.getCiclo());
			} catch (Exception e) {
				// do nothing
			}
			
			FacesUtil.addInfoMessage("Par\u00E2metros salvos com sucesso!");
			return "/pages/ciclo/manter.xhtml";
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
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

	public ConfigurarFormaAquisicao getFormaAquisicao() {
		if (this.cicloConfiguracao.getConfiguracaoFormaAquisicao() != null) {
			this.formaAquisicao = this.cicloConfiguracao.getConfiguracaoFormaAquisicao();
		}
		if (this.formaAquisicao == null) {
			this.formaAquisicao = new ConfigurarFormaAquisicao();
		}
		return formaAquisicao;
	}

	public void setFormaAquisicao(ConfigurarFormaAquisicao formaAquisicao) {
		this.formaAquisicao = formaAquisicao;
	}

	public void uploadArquivo(FileUploadEvent event) {
		try {
			String fileName = FilenameUtils.getName(event.getFile().getFileName());
			if (this.formaAquisicao.getAnexos() == null) {
				this.formaAquisicao.setAnexos(new ArrayList<Anexo>());
			}
			Anexo an = new Anexo();
			an.setNome(RandomStringUtils.random(8, true, true) + fileName);
			an.setNomeExibicao(fileName);
			byte[] fileBytes = IOUtils.toByteArray(event.getFile().getInputstream());
			an.setTipo(event.getFile().getContentType());
			an.setArquivo(fileBytes);
			FileUtils
					.writeByteArrayToFile(new File(Constants.FILES_TMP_DIR + File.separator + an.getNome()), fileBytes);
			an.setConfiguracaoFormaAquisicao(this.formaAquisicao);
			this.formaAquisicao.getAnexos().add(an);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirArquivo() {
		this.getFormaAquisicao().getAnexos().remove(this.indiceAnexo);
	}

	public StreamedContent getArquivo() {
		StreamedContent file = null;
		try {
			Anexo a = this.getFormaAquisicao().getAnexos().get(indiceAnexo);
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

	public List<FormaAquisicao> getFormasAquisicao() {
		if (formasAquisicao == null || formasAquisicao.isEmpty()) {
			formasAquisicao = formaAquisicaoService.findAllAtivas();
			if (this.formaAquisicao.getFormasAquisicao() != null && !this.formaAquisicao.getFormasAquisicao().isEmpty()) {
				if (!formasAquisicao.isEmpty()) {
					for (FormaAquisicao fa : formasAquisicao) {
						if (this.formaAquisicao.getFormasAquisicao().contains(fa)) {
							fa.setSelecionado(true);
						}
					}
				}
			}
		}
		return formasAquisicao;
	}

	public void setFormasAquisicao(List<FormaAquisicao> formasAquisicao) {
		this.formasAquisicao = formasAquisicao;
	}

	public Boolean getSelecionarTodos() {
		return selecionarTodos;
	}

	public void setSelecionarTodos(Boolean selecionarTodos) {
		this.selecionarTodos = selecionarTodos;
	}
	
	public void alterouSelecioanrTodos(){
		for (FormaAquisicao fa : formasAquisicao) {
			fa.setSelecionado(this.selecionarTodos);
		}
	}

}
