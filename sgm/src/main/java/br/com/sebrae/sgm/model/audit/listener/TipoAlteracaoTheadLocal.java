package br.com.sebrae.sgm.model.audit.listener;

import br.com.sebrae.sgm.model.enums.TipoLog;

public class TipoAlteracaoTheadLocal {
	public static ThreadLocal<TipoLog> tipoLog = new ThreadLocal<TipoLog>();
}
