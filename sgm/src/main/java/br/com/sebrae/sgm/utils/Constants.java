package br.com.sebrae.sgm.utils;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

public interface Constants {
	String FILES_DIR_PARAM = System.getProperty(PropertiesUtils.getInstance("app").getProperty("dir_propertie"));
	String FILES_DIR_PATH = PropertiesUtils.getInstance("app").getProperty("dir_path");

	String FILES_DIR = StringUtils.isBlank(Constants.FILES_DIR_PARAM) ? Constants.FILES_DIR_PATH
			: Constants.FILES_DIR_PARAM;
	
	String FILES_TMP_DIR = FILES_DIR + File.separator + "tmp";

	String MSG_FILE = "msg";

	interface CHAVES_CONFIGURACOES {
		String QTD_MAXIMA_METAS_INDIVIDUAIS = "qtd_maxima_metas_individuais";
		String EXIBIR_MARCOS_CRITICOS = "exibir_marcos_criticos";
	}
	
	String KEY_LOG_TIPO_ALTERACAO = "key_log_tipo_alteracao";
}
