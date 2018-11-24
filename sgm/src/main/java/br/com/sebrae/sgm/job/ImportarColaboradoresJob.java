package br.com.sebrae.sgm.job;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.service.UsuarioServiceAplication;

public class ImportarColaboradoresJob implements Job {

	protected Logger log = LoggerFactory.getLogger(ImportarColaboradoresJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		UsuarioServiceAplication usuarioService = BeanProvider.getContextualReference(UsuarioServiceAplication.class);
		usuarioService.importarUsuarios();
	}

}
