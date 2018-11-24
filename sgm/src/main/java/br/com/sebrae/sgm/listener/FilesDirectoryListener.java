package br.com.sebrae.sgm.listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.PropertiesUtils;


public class FilesDirectoryListener implements ServletContextListener {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent ctx) {
		String directory = Constants.FILES_DIR;

		File filesDirectory = new File(directory);

		if (!filesDirectory.exists()) { 
			if (!filesDirectory.mkdirs()) {
				throw new RuntimeException(PropertiesUtils.getInstance("msg").getProperty("msg_falha_criar_diretorio_arquivos"));
			}
		}
		
		String tmpDiretory = Constants.FILES_TMP_DIR;

		File filesTmpDirectory = new File(tmpDiretory);

		if (!filesTmpDirectory.exists()) { 
			if (!filesTmpDirectory.mkdirs()) {
				throw new RuntimeException(PropertiesUtils.getInstance("msg").getProperty("msg_falha_criar_diretorio_arquivos"));
			}
		}
	}
}
