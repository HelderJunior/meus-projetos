package br.com.sebrae.sgm.utils;

public final class StringUtils {

	private StringUtils() {
	}

	public static String limitaString(Integer tamanho, String retorno) {
		if (retorno != null) {
			if (retorno.length() > tamanho) {
				retorno = retorno.substring(0, tamanho).concat("...");
			}
		}
		return retorno;
	}

}
