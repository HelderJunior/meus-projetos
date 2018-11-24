package br.com.sebrae.sgm.utils;

import java.io.Serializable;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

public class ConversationTimeoutSetter implements Serializable {

	private static final long serialVersionUID = 1L;
	@Inject
	private Conversation conversation;

/*	public void conversationInitialized(@Observes @Initialized(ConversationScoped.class) HttpServletRequest pay) {
		conversation.setTimeout(1800000L);
	}*/

}
