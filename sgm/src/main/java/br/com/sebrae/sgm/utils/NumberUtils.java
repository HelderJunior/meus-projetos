package br.com.sebrae.sgm.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class NumberUtils {

	private NumberUtils() {
	}

	public static String formatBr(BigDecimal numero, Integer casasDecimais) {
		if (numero == null) {
			return "";
		}
		String casas = "";
		for (int i = 0; i < casasDecimais; i++) {
			casas = casas.concat("0");
		}
		
		if(casas.length() > 0){
			casas = "0."+casas;
		}
		DecimalFormat format = new java.text.DecimalFormat("###,###" + casas, new DecimalFormatSymbols(new Locale(
				"pt", "BR")));
		return format.format(numero.doubleValue());
	}

}
