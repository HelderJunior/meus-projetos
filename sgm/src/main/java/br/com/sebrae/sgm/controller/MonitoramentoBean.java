package br.com.sebrae.sgm.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.Observacao;
import br.com.sebrae.sgm.model.enums.StatusExecucaoMeta;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class MonitoramentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(MonitoramentoBean.class);
	
	@Inject
	private MetaService metaService;
	
	@Inject
	private AppBean appBean;

	private Meta meta;

	private Observacao observacaoSelecionada;

	private int indiceAnexo;

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public Observacao getObservacaoSelecionada() {
		return observacaoSelecionada;
	}

	public void setObservacaoSelecionada(Observacao observacaoSelecionada) {
		this.observacaoSelecionada = observacaoSelecionada;
	}

	public int getIndiceAnexo() {
		return indiceAnexo;
	}

	public void setIndiceAnexo(int indiceAnexo) {
		this.indiceAnexo = indiceAnexo;
	}

	
	public void uploadArquivo(FileUploadEvent event) {
		try {
			String fileName = FilenameUtils.getName(event.getFile().getFileName());
			if (this.getMeta().getAnexosEvidenciaEntrega() == null) {
				this.getMeta().setAnexosEvidenciaEntrega(new ArrayList<Anexo>());
			}
			Anexo an = new Anexo();
			an.setNome(RandomStringUtils.random(8, true, true) + fileName);
			an.setNomeExibicao(fileName);
			
			byte[] fileBytes = IOUtils.toByteArray(event.getFile().getInputstream());
			an.setTipo(event.getFile().getContentType());
			an.setArquivo(fileBytes);
			FileUtils
					.writeByteArrayToFile(new File(Constants.FILES_TMP_DIR + File.separator + an.getNome()), fileBytes);
			an.setMeta(this.getMeta());
			this.getMeta().getAnexosEvidenciaEntrega().add(an);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("form_anexo", "Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirArquivo() {
		this.getMeta().getAnexosEvidenciaEntrega().remove(this.indiceAnexo);
	}

	public StreamedContent getArquivo() {
		StreamedContent file = null;
		try {
			Anexo a = this.getMeta().getAnexosEvidenciaEntrega().get(indiceAnexo);
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

	public List<StatusExecucaoMeta> getStatusExecucaoMeta() {
		return Arrays.asList(StatusExecucaoMeta.values());
	}

	public String salvar() {
		try {
			this.metaService.save(meta);
			FacesUtil.addInfoMessage("Meta salva com sucesso!");
			return appBean.back();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

}
