package br.com.sebrae.sgm.exception;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import br.com.sebrae.sgm.utils.FacesUtil;

public class DefaultExceptionHandler extends ExceptionHandlerWrapper {

	private ExceptionHandler wrapped;

	public DefaultExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException {
		FacesContext fc = FacesUtil.getFacesContext();
		if (fc.isProjectStage(ProjectStage.Development)) {
			getWrapped().handle();
		} else {
			Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
			while (i.hasNext()) {
				ExceptionQueuedEvent event = i.next();
				ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
				String redirectPage = null;
				Throwable t = context.getException();
				try {
					t.printStackTrace();
					if (t.getCause() instanceof com.sun.faces.context.FacesFileNotFoundException) {
						redirectPage = "/error/404.xhtml";
					} else if (t instanceof org.jboss.weld.context.NonexistentConversationException
							|| t instanceof javax.enterprise.context.NonexistentConversationException
							|| t instanceof org.jboss.weld.context.BusyConversationException) {
						redirectPage = "/pages/index.xhtml";
					} else if (t instanceof ViewExpiredException) {
						redirectPage = "/pages/index.xhtml";
					} else {
						redirectPage = "/error/500.xhtml?nocid=true";
					}
				} finally {
					i.remove();
				}
				try {
					FacesUtil.getExternalContext().redirect(
							FacesUtil.getExternalContext().getRequestContextPath() + redirectPage);
				} catch (IOException ioe) {
					throw new FacesException(ioe.getCause());
				}
				break;
			}
		}

	}

}
