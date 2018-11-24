package br.com.sebrae.sgm.model.audit.listener;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.sebrae.sgm.model.audit.Revisao;
import br.com.sebrae.sgm.model.enums.TipoLog;

public class AuditListener implements RevisionListener {

	@Override
	public void newRevision(Object revisionEntity) {
		Revisao revisao = (Revisao) revisionEntity;
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (auth != null) {
			String username = auth.getName();
			revisao.setUsuario(username);
		}

		TipoLog tipoLog = TipoAlteracaoTheadLocal.tipoLog.get();
		if (tipoLog != null) {
			revisao.setTipoLog(tipoLog);
		}
	}
}
