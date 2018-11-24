package br.com.sebrae.sgm.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.dto.GerenciarMetaDTO;
import br.com.sebrae.sgm.model.Anexo;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.SolucaoEducacionalMeta;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class GestorGestaoMetasDesenvolvimentoNomeBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(GestorGestaoMetasDesenvolvimentoNomeBean.class);

	@Inject
	private AppBean appBean;

	private GerenciarMetaDTO gerenciaMetaDto;

	private List<Meta> metas = new ArrayList<Meta>();
	private SolucaoEducacionalMeta solucaoSelecionada;

	private int indiceAnexo;

	@Override
	public void init() {
		super.init();

		metas = new ArrayList<Meta>();
		if (gerenciaMetaDto != null) {
			/*if (gerenciaMetaDto.getMetasAprovadas() != null) {
				for (Meta m : gerenciaMetaDto.getMetasAprovadas()) {
					if (!metas.contains(m))
						metas.add(m);
				}
			}*/
			
			/*
			if (gerenciaMetaDto.getSolucoesAprovadas() != null) {
				for (SolucaoEducacionalMeta se : gerenciaMetaDto.getSolucoesAprovadas()) {
					if (!metas.contains(se.getMeta()))
						metas.add(se.getMeta());
				}
			}
			*/
			
			if (gerenciaMetaDto.getMetasPendenteAprovacao() != null) {
				for (Meta m : gerenciaMetaDto.getMetasPendenteAprovacao()) {
					if (!metas.contains(m))
						metas.add(m);
				}
			}
			
			if (gerenciaMetaDto.getSolucoesPendenteAprovacao() != null) {
				for (SolucaoEducacionalMeta se : gerenciaMetaDto.getSolucoesPendenteAprovacao()) {
					if (!metas.contains(se.getMeta()))
						metas.add(se.getMeta());
				}
			}
			
			if (gerenciaMetaDto.getMetasObservacaoComite() != null) {
				for (Meta m : gerenciaMetaDto.getMetasObservacaoComite()) {
					if (!metas.contains(m))
						metas.add(m);
				}
			}
			
			if (gerenciaMetaDto.getMetasObservacaoSuperior() != null) {
				for (Meta m : gerenciaMetaDto.getMetasObservacaoSuperior()) {
					if (!metas.contains(m))
						metas.add(m);
				}
			}
			
			if (gerenciaMetaDto.getSolucoesRealizadas() != null) {
				for (SolucaoEducacionalMeta se : gerenciaMetaDto.getSolucoesRealizadas()) {
					if (!metas.contains(se.getMeta()))
						metas.add(se.getMeta());
				}
			}
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

	public List<Meta> getMetas() {
		return metas;
	}

	public void setMetas(List<Meta> metas) {
		this.metas = metas;
	}

	public SolucaoEducacionalMeta getSolucaoSelecionada() {
		return solucaoSelecionada;
	}

	public void setSolucaoSelecionada(SolucaoEducacionalMeta solucaoSelecionada) {
		this.solucaoSelecionada = solucaoSelecionada;
	}

	public int getIndiceAnexo() {
		return indiceAnexo;
	}

	public void setIndiceAnexo(int indiceAnexo) {
		this.indiceAnexo = indiceAnexo;
	}

	public GerenciarMetaDTO getGerenciaMetaDto() {
		return gerenciaMetaDto;
	}

	public void setGerenciaMetaDto(GerenciarMetaDTO gerenciaMetaDto) {
		this.gerenciaMetaDto = gerenciaMetaDto;
	}

}
