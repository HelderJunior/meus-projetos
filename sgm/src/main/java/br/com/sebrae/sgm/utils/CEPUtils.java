package br.com.sebrae.sgm.utils;

public class CEPUtils {

	public static String clean(final String cpf) {
		String cpfLimpo = cpf.trim();
		cpfLimpo = cpfLimpo.replaceAll("\\-", "");
		return cpfLimpo;
	}

	public static String format(String cpf) {
		StringBuilder builder = new StringBuilder(cpf.replaceAll("[^\\d]", ""));
		builder.insert(5, '-');
		return builder.toString();
	}

}
