package br.com.sebrae.sgm.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtils {

	private PropertiesUtils() {
	}

	private static Map<String, Properties> props = new HashMap<String, Properties>();

	public static Properties getInstance(String nome) {
		if (!props.containsKey(nome)) {
		    try {
		    	Properties p = new Properties();
				InputStream inputStream = PropertiesUtils.class.getResourceAsStream("/"+nome+".properties");
				p.load(inputStream);
				props.put(nome, p);
				return p;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			return props.get(nome);
		}
	}

}
