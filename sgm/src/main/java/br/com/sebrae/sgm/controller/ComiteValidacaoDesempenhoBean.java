package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.ItemAvaliadoComite;
import br.com.sebrae.sgm.model.ItemAvaliadoComiteMeta;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.service.ConfiguracaoAvaliacaoComiteService;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ComiteValidacaoDesempenhoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ComiteValidacaoDesempenhoBean.class);

	@Inject
	private MetaService metaService;

	@Inject
	private ComiteGestaoMetasBean comiteGestaoMetasBean;

	@Inject
	private AppBean appBean;

	@Inject
	private ConfiguracaoAvaliacaoComiteService configuracaoAvaliacaoComiteService;

	private Meta meta;

	private String obsComite;

	private List<ItemAvaliadoComiteMeta> itensAvaliacaoMeta = new ArrayList<ItemAvaliadoComiteMeta>();

	@Override
	public void init() {
		super.init();
		if (this.meta.getItemsAvaliacaoComite() == null || this.meta.getItemsAvaliacaoComite().isEmpty()) {
			this.itensAvaliacaoMeta = new ArrayList<ItemAvaliadoComiteMeta>();
			
			/*List<ItemAvaliadoComite> listaItens = configuracaoAvaliacaoComiteService.findItensAvaliacaoComite(
					this.meta.getCicloConfiguracao(), this.meta.getUnidade());*/
			List<ItemAvaliadoComite> listaItens = configuracaoAvaliacaoComiteService.findItensAvaliacaoComite(this.meta.getCicloConfiguracao().getCiclo());
			if (listaItens != null && !listaItens.isEmpty()) {
				for (ItemAvaliadoComite iac : listaItens) {
					ItemAvaliadoComiteMeta iacm = new ItemAvaliadoComiteMeta();
					iacm.setDescricao(iac.getDescricao());
					iacm.setMeta(this.meta);
					iacm.setItemAvaliado(iac);
					this.itensAvaliacaoMeta.add(iacm);
				}
			}
		} else {
			this.itensAvaliacaoMeta = this.meta.getItemsAvaliacaoComite();
		}
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public String getObsComite() {
		return obsComite;
	}

	public void setObsComite(String obsComite) {
		this.obsComite = obsComite;
	}

	public String enviarObservacao() {
		try {
			if (StringUtils.isBlank(this.obsComite)) {
				FacesUtil.addErrorMessage("form_meta_validacao:obsComite",
						"Observa\u00E7\u00F5es do Comit\u00EA: campo de preenchimento obrigat\u00F3rio.");
				return "";
			}
			metaService.enviarObservacaoComiteMeta(meta, this.obsComite);
			this.comiteGestaoMetasBean.removerDaLista(meta);
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

	public String validarMeta() {
		try {
			this.meta.setItemsAvaliacaoComite(this.itensAvaliacaoMeta);
			if (this.meta.getTipo() == TipoMeta.E)
				metaService.validarMetaComite(this.meta, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
			else if (this.meta.getTipo() == TipoMeta.I)
				metaService.validarMetaComite(this.meta, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
			this.comiteGestaoMetasBean.removerDaLista(meta);
			FacesUtil.addInfoMessage("Meta validada com sucesso.");
			return appBean.back();
		} catch (ValidateException e) {
			FacesUtil.addErrorMessage(e.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	public List<ItemAvaliadoComiteMeta> getItensAvaliacaoMeta() {
		return itensAvaliacaoMeta;
	}

	public void setItensAvaliacaoMeta(List<ItemAvaliadoComiteMeta> itensAvaliacaoMeta) {
		this.itensAvaliacaoMeta = itensAvaliacaoMeta;
	}

}
