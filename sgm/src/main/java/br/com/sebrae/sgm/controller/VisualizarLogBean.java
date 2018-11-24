package br.com.sebrae.sgm.controller;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.audit.Revisao;
import br.com.sebrae.sgm.model.enums.TipoLog;
import br.com.sebrae.sgm.service.LogService;

@ConversationScoped
@Named
public class VisualizarLogBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(VisualizarLogBean.class);

	@Inject
	private LogService logService;

	private Object[] registroDeLog;
	private Object[] registroRevisaoAnterior;

	private TipoLog tipoLog;

	public Object[] getRegistroDeLog() {
		return registroDeLog;
	}

	public void setRegistroDeLog(Object[] registroDeLog) {
		this.registroDeLog = registroDeLog;
	}

	public TipoLog getTipoLog() {
		return tipoLog;
	}

	public void setTipoLog(TipoLog tipoLog) {
		this.tipoLog = tipoLog;
	}

	public Object[] getRegistroRevisaoAnterior() {
		if (registroRevisaoAnterior == null) {
			registroRevisaoAnterior = logService.findMetaByPreviousRevision(((Revisao) registroDeLog[1]).getId(), ((Meta) registroDeLog[0]).getId());
		}
		return registroRevisaoAnterior;
	}

	public void setRegistroRevisaoAnterior(Object[] registroRevisaoAnterior) {
		this.registroRevisaoAnterior = registroRevisaoAnterior;
	}

}
