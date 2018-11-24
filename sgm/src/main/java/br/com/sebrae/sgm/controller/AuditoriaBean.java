package br.com.sebrae.sgm.controller;

import java.util.ArrayList;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.Observacao;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.enums.StatusEnvio;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.model.enums.TipoObservacao;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class AuditoriaBean extends BaseBean {

	@Inject
	private AuditoriaListarBean listarBean;

	@Inject
	private AppBean appBean;

	@Inject
	private MetaService metaService;

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(AuditoriaBean.class);

	private Meta meta;

	private String observacaoAuditor;

	private Observacao observacaoSalva;

	@Override
	public void init() {
		super.init();
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public String getObservacaoAuditor() {
		if (this.observacaoAuditor == null) {
			this.observacaoSalva = meta.getObservacaoByUsuario(appBean.getUsuarioAutenticado());
			if (this.observacaoSalva != null) {
				this.observacaoAuditor = this.observacaoSalva.getDescricao();
			}
		}
		return observacaoAuditor;
	}

	public void setObservacaoAuditor(String observacaoAuditor) {
		this.observacaoAuditor = observacaoAuditor;
	}

	public String enviarObservacao() {
		try {
			if (StringUtils.isBlank(this.observacaoAuditor)) {
				FacesUtil.addErrorMessage("form_auditar_meta:obsAuditor",
						"Observa\u00E7\u00F5es do Auditor: campo de preenchimento obrigat\u00F3rio.");
				return "";
			}
			metaService.enviarObservacaoAuditor(meta, this.observacaoAuditor);
			if (this.observacaoSalva != null) {
				this.meta.getObservacoes().remove(this.observacaoSalva);
				this.metaService.save(this.meta);
			}
			this.listarBean.removerDaLista(meta);
			FacesUtil.addInfoMessage("Observa\u00E7\u00E3o enviada com sucesso.");
			return appBean.back();
		} catch (ValidateException e) {
			FacesUtil.addErrorMessage(e.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public String salvarObservacao() {
		try {
			if (StringUtils.isBlank(this.observacaoAuditor)) {
				FacesUtil.addErrorMessage("form_auditar_meta:obsAuditor",
						"Observa\u00E7\u00F5es do Auditor: campo de preenchimento obrigat\u00F3rio.");
				return "";
			}
			if (observacaoSalva != null) {
				this.observacaoSalva.setDescricao(this.observacaoAuditor);
			} else {
				Observacao obs = new Observacao();
				obs.setMeta(meta);
				obs.setTipo(TipoObservacao.U);
				obs.setDescricao(this.observacaoAuditor);
				obs.setStatus(StatusEnvio.N);
				obs.setChavePerfilDestino(Perfil.COLABORADOR_CHAVE);
				obs.setRemetente(this.appBean.getUsuarioAutenticado());
				if (meta.getObservacoes() == null) {
					meta.setObservacoes(new ArrayList<Observacao>());
				}
				meta.getObservacoes().add(obs);
			}
			this.metaService.save(this.meta);
			FacesUtil.addInfoMessage("Observa\u00E7\u00E3o salva com sucesso.");
			return appBean.back();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public String enviarObservacaoGerente() {
		try {
			if (StringUtils.isBlank(this.observacaoAuditor)) {
				FacesUtil.addErrorMessage("form_auditar_meta:obsAuditor",
						"Observa\u00E7\u00F5es do Auditor: campo de preenchimento obrigat\u00F3rio.");
				return "";
			}
			metaService.enviarObservacaoAuditorGerente(meta, this.observacaoAuditor);
			if (this.observacaoSalva != null) {
				this.meta.getObservacoes().remove(this.observacaoSalva);
				this.metaService.save(this.meta);
			}
			this.listarBean.removerDaLista(meta);
			FacesUtil.addInfoMessage("Observa\u00E7\u00E3o enviada com sucesso.");
			return appBean.back();
		} catch (ValidateException e) {
			FacesUtil.addErrorMessage(e.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public String enviarMetaComiteSGP() {
		try {
			if (this.meta.getTipo() == TipoMeta.E)
				metaService.enviarMetaComiteSGPAuditor(this.meta, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
			else if (this.meta.getTipo() == TipoMeta.I)
				metaService.enviarMetaComiteSGPAuditor(this.meta, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
			this.listarBean.removerDaLista(meta);
			FacesUtil.addInfoMessage("Meta enviada para comit\u00EA SGP com sucesso.");
			return appBean.back();
		} catch (ValidateException e) {
			FacesUtil.addErrorMessage(e.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public String atestarMeta() {
		try {
			if (this.meta.getTipo() == TipoMeta.E)
				metaService.atestarMeta(this.meta, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
			else if (this.meta.getTipo() == TipoMeta.I)
				metaService.atestarMeta(this.meta, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
			this.listarBean.removerDaLista(meta);
			FacesUtil.addInfoMessage("Meta atestada com sucesso.");
			return appBean.back();
		} catch (ValidateException e) {
			FacesUtil.addErrorMessage(e.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

}
