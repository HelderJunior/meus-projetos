package br.com.sebrae.sgm.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.enums.UF;

public abstract class BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@Inject
	protected Conversation conversation;

	public List<UF> getUfs() {
		return Arrays.asList(UF.values());
	}

	public void init() {
		beginConversation();
	}

	@PreDestroy
	public void destroy() {
		log.info("Destruindo o bean " + this.getClass().getCanonicalName() + " Conversation ID: "
				+ getConversation().getId());
	}

	/**
	 * Inicia uma conversacao longa.
	 */
	private void beginConversation() {
		if(conversation.isTransient()) {
			log.info("Inicializando a conversacao no Bean " + this.getClass().getCanonicalName());
			conversation.begin();
			log.info("Conversação iniciada - ID:" + conversation.getId());
		}
		conversation.setTimeout(1800000L);
	}

	public Conversation getConversation() {
		return conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}
}
