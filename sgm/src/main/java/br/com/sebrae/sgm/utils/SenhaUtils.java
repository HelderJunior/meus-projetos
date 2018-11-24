package br.com.sebrae.sgm.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class SenhaUtils {

	public static final String expressao = "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{5,8})";
	public static final String regra = "A senha deve conter no m\u00EDnimo 5 caracteres e no m\u00E1ximo 8, devendo ter letras e n\u00FAmeros e pelo menos uma letra mai\u00FAscula.";

	public static String encrypt(String senha) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.update(senha.getBytes(), 0, senha.length());
		return new BigInteger(1, m.digest()).toString(16);
	}

	public static String generatePassword() {
		return RandomStringUtils.random(8, true, true);
	}

	public static String generateValidateKey() {
		return RandomStringUtils.random(20, true, true);
	}

	public static boolean validate(String senha) {
		if (!StringUtils.isBlank(senha))
			return senha.matches(expressao);
		return false;
	}
}
