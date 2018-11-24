package br.com.sebrae.sgm.listener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.job.FinalizarCicloJob;
import br.com.sebrae.sgm.job.FinalizarFazesCicloJob;
import br.com.sebrae.sgm.job.ImportarColaboradoresJob;
import br.com.sebrae.sgm.utils.QuartzUtils;

public class AgendadorTarefasListener implements ServletContextListener {

	protected Logger log = LoggerFactory.getLogger(AgendadorTarefasListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent ctx) {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 2);
			
			QuartzUtils.agendar("importar-colaboradores-job", "importar_colaboradores-agora", cal.getTime(),
					ImportarColaboradoresJob.class, parametros);
			QuartzUtils.agendar("importar-colaboradores-job", "importar_colaboradores", "0 0 0 1/1 * ? *",
					ImportarColaboradoresJob.class, parametros);

			cal.add(Calendar.MINUTE, 8);

			QuartzUtils.agendar("finalizar-ciclo-job", "finalizar_ciclo-agora", cal.getTime(), FinalizarCicloJob.class,
					parametros);
			QuartzUtils.agendar("finalizar-ciclo-job", "finalizar_ciclo", "0 5 0 1/1 * ? *", FinalizarCicloJob.class,
					parametros);

			cal.add(Calendar.MINUTE, 5);
			QuartzUtils.agendar("finalizar-fases-job", "finalizar_fases-agora", cal.getTime(),
					FinalizarFazesCicloJob.class, parametros);
			QuartzUtils.agendar("finalizar-fases-job", "finalizar_fases", "0 10 0 1/1 * ? *",
					FinalizarFazesCicloJob.class, parametros);

			/*
			 * QuartzUtils.agendar("popular-arvore-unidades", "popular_arvore_unidades-agora", new Date(),
			 * PopularArvoreUnidadesJob.class, parametros); QuartzUtils.agendar("popular-arvore-unidades",
			 * "popular_arvore_unidades", "0 0 0/8 1/1 * ? *", PopularArvoreUnidadesJob.class, parametros);
			 */
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}