package br.com.sebrae.sgm.controller;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.dto.MetaDesenvolvimentoComiteDTO;
import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Anexo;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.SolucaoEducacionalMeta;
import br.com.sebrae.sgm.model.enums.StatusSolucaoEducacional;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.SolucaoEducacionalMetaService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ComiteValidacaoDesenvolvimentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ComiteValidacaoDesenvolvimentoBean.class);

	@Inject
	private MetaService metaService;

	@Inject
	private SolucaoEducacionalMetaService solucaoEducacionalMetaService;

	@Inject
	private AppBean appBean;

	private MetaDesenvolvimentoComiteDTO metaGerenciar;

	private SolucaoEducacionalMeta solucaoEducacionalSelecionada;
	private Meta metaSelecionada;

	private int indiceAnexo;
	private int indiceMeta;

	public void inserirObservacao() {
		try {
			if (StringUtils.isBlank(this.metaSelecionada.getObservacaoTemp())) {
				throw new ValidateException("Informe a observa\u00E7\u00E3o para inserir o par\u00E2metro");
			}
			metaService.enviarObservacaoComiteMeta(this.metaSelecionada, this.metaSelecionada.getObservacaoTemp());
			this.metaGerenciar.getMetasComiteDesenvolvimento().remove(this.indiceMeta);
			FacesUtil.addInfoMessage("Observa\u00E7\u00E3o enviada com sucesso.");
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void cancelarObservacao() {
		try {
			this.getMetaGerenciar().getMetasComiteDesenvolvimento().get(indiceMeta).setObservacaoTemp(null);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void salvarSolucao() {
		try {
			validarSalvarSolucao();
			if (this.solucaoEducacionalSelecionada.getNovaCargaHoraria() != null
					&& this.solucaoEducacionalSelecionada.getNovaCargaHoraria().compareTo(BigDecimal.ZERO) > 0) {
				this.solucaoEducacionalSelecionada.setQuantidadeAntiga(this.solucaoEducacionalSelecionada
						.getQuantidadePrevista());
				this.solucaoEducacionalSelecionada.setQuantidadePrevista(this.solucaoEducacionalSelecionada
						.getNovaCargaHoraria());
			}
			this.solucaoEducacionalMetaService.atualizar(this.solucaoEducacionalSelecionada);
			List<SolucaoEducacionalMeta> solucoesMeta = this.metaSelecionada.getSolucoesEducacionais();
			List<SolucaoEducacionalMeta> solucoesNaoAprovadas = new ArrayList<SolucaoEducacionalMeta>();
			for (SolucaoEducacionalMeta sem : solucoesMeta) {
				if (sem.getStatus() != StatusSolucaoEducacional.V) {
					solucoesNaoAprovadas.add(sem);
				}
			}
			if (solucoesNaoAprovadas.isEmpty()) {
				this.metaService.validarMetaComite(this.metaSelecionada, this.metaSelecionada.getCicloConfiguracao()
						.getTipoConfiguracao(), TipoGrupo.C);
				this.metaGerenciar.getMetasComiteDesenvolvimento().remove(this.metaSelecionada);
				FacesUtil.addInfoMessage("Meta validada com sucesso!");
			}
			FacesUtil.addInfoMessage("Solu\u00E7\u00E3o Educacional salva com sucesso.");
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage("form_aprovar_solucao", ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("form_aprovar_solucao",
					"Erro ao executar a a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	private void validarSalvarSolucao() throws ValidateException {
		List<String> erros = new ArrayList<String>();

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	public StreamedContent getArquivo() {
		StreamedContent file = null;
		try {
			Anexo a = this.getSolucaoEducacionalSelecionada().getAnexos().get(indiceAnexo);
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

	public List<StatusSolucaoEducacional> getStatusSolucaoEducacional() {
		return Arrays.asList(StatusSolucaoEducacional.values());
	}

	public MetaDesenvolvimentoComiteDTO getMetaGerenciar() {
		return metaGerenciar;
	}

	public void setMetaGerenciar(MetaDesenvolvimentoComiteDTO metaGerenciar) {
		this.metaGerenciar = metaGerenciar;
	}

	public SolucaoEducacionalMeta getSolucaoEducacionalSelecionada() {
		return solucaoEducacionalSelecionada;
	}

	public void setSolucaoEducacionalSelecionada(SolucaoEducacionalMeta solucaoEducacionalSelecionada) {
		this.solucaoEducacionalSelecionada = solucaoEducacionalSelecionada;
	}

	public int getIndiceAnexo() {
		return indiceAnexo;
	}

	public void setIndiceAnexo(int indiceAnexo) {
		this.indiceAnexo = indiceAnexo;
	}

	public Meta getMetaSelecionada() {
		return metaSelecionada;
	}

	public void setMetaSelecionada(Meta metaSelecionada) {
		this.metaSelecionada = metaSelecionada;
	}

	public int getIndiceMeta() {
		return indiceMeta;
	}

	public void setIndiceMeta(int indiceMeta) {
		this.indiceMeta = indiceMeta;
	}

}
