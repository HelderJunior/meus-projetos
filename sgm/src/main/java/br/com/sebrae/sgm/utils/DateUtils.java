package br.com.sebrae.sgm.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class DateUtils {

	public static final int diferencaEmMeses(Date date1, Date date2) {

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		int m1 = cal1.get(Calendar.YEAR) * 12 + cal1.get(Calendar.MONTH);
		int m2 = cal2.get(Calendar.YEAR) * 12 + cal2.get(Calendar.MONTH);
		return m2 - m1 + 1;
	}

	/**
	 * M�todo Long getDaysDiff(Date start, Date end) responsavel por recuperar DaysDiff.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return DaysDiff.
	 */
	public static Long getDaysDiff(Date start, Date end) {

		start = zeroTime(start);
		end = zeroTime(end);

		Calendar cStart = new GregorianCalendar();
		cStart.setLenient(false);
		cStart.setTime(start);

		Calendar cEnd = new GregorianCalendar();
		cEnd.setLenient(false);
		cEnd.setTime(end);

		Long diff = cEnd.getTimeInMillis() - cStart.getTimeInMillis();

		return diff / (24 * 60 * 60 * 1000);

	}

	/**
	 * Metodo Long getMinutesDiff(Date start, Date end) responsavel por recuperar MinutesDiff.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return MinutesDiff.
	 */
	public static Long getMinutesDiff(Date start, Date end) {

		Calendar cStart = new GregorianCalendar();
		cStart.setLenient(false);
		cStart.setTime(start);
		cStart.clear(Calendar.SECOND);
		cStart.clear(Calendar.MILLISECOND);

		Calendar cEnd = new GregorianCalendar();
		cEnd.setLenient(false);
		cEnd.setTime(end);
		cEnd.clear(Calendar.SECOND);
		cEnd.clear(Calendar.MILLISECOND);

		Long diff = cEnd.getTimeInMillis() - cStart.getTimeInMillis();

		return diff / (60 * 1000);
	}

	/**
	 * Zero time.
	 * 
	 * @param data
	 *            the data
	 * @return the date
	 */
	public static Date zeroTime(Date data) {

		Calendar c = new GregorianCalendar();
		c.setTime(data);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();
	}

	/**
	 * Final hour of date.
	 * 
	 * @param data
	 *            the data
	 * @return the date
	 */
	public static Date finalHourOfDate(Date data) {

		Calendar c = new GregorianCalendar();
		c.setTime(data);
		c.set(Calendar.HOUR, 23);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();
	}

	/**
	 * Zero date.
	 * 
	 * @param data
	 *            the data
	 * @return the date
	 */
	public static Date zeroDate(Date data) {

		Calendar c = new GregorianCalendar();
		c.setTime(data);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, Calendar.JANUARY);
		c.set(Calendar.YEAR, 1970);

		return c.getTime();
	}

	/**
	 * Zerar milisegundos.
	 * 
	 * @param data
	 *            the data
	 * @return the date
	 */
	public static Date zerarMilisegundos(Date data) {

		Calendar c = new GregorianCalendar();
		c.setTime(data);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();

	}

	/**
	 * Soma horas na data.
	 * 
	 * @param data
	 *            the data
	 * @param hora
	 *            the hora
	 * @return the date
	 */
	public static Date somaHorasNaData(Date data, Date hora) {

		long dataEmMilisegundos = data.getTime();

		Calendar horaBase = Calendar.getInstance();
		horaBase.setTime(hora);
		// X horas passadas no parametro 'hora'
		long horaBaseAsXHorasEmMilisegundos = horaBase.getTimeInMillis();
		// meia noite: base para o c�lculo de quantos millisegundos � a
		// 'hora'.
		horaBase.set(Calendar.HOUR_OF_DAY, 0);
		horaBase.set(Calendar.MINUTE, 0);
		horaBase.set(Calendar.SECOND, 0);
		long horaBaseAMeiaNoite = horaBase.getTimeInMillis();
		// 'hora' em milisegundos.
		long xHoras = horaBaseAsXHorasEmMilisegundos - horaBaseAMeiaNoite;
		// adiciona x 'horas' a 'data'.
		Date dataMaisXHoras = new Date(dataEmMilisegundos + xHoras);

		return dataMaisXHoras;

	}

	/**
	 * Soma minutos na data.
	 * 
	 * @param data
	 *            the data
	 * @param minutos
	 *            the minutos
	 * @return the date
	 */
	public static Date somaMinutosNaData(Date data, Long minutos) {

		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.MINUTE, minutos.intValue());

		return c.getTime();

	}

	/**
	 * Soma meses.
	 * 
	 * @param data
	 *            the data
	 * @param meses
	 *            the meses
	 * @return the date
	 */
	public static Date somaMeses(Date data, int meses) {

		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.MONTH, meses);
		return c.getTime();
	}

	/**
	 * Soma dias.
	 * 
	 * @param data
	 *            the data
	 * @param dias
	 *            the dias
	 * @return the date
	 */
	public static Date somaDias(Date data, int dias) {

		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.DAY_OF_YEAR, dias);
		return c.getTime();
	}

	/**
	 * To milisegundos.
	 * 
	 * @param data
	 *            the data
	 * @return the long
	 */
	public static Long toMilisegundos(Date data) {

		Calendar c = new GregorianCalendar();
		c.setTime(data);

		return c.getTimeInMillis();
	}

	/**
	 * M�todo Date getOntem(Date data) responsavel por recuperar Ontem.
	 * 
	 * @param data
	 *            the data
	 * @return Ontem.
	 */
	public static Date getOntem(Date data) {

		return getDiaAnteriorOuConseguinte(data, -1);

	}

	/**
	 * M�todo Date getAmanha(Date data) responsavel por recuperar Amanha.
	 * 
	 * @param data
	 *            the data
	 * @return Amanha.
	 */
	public static Date getAmanha(Date data) {

		return getDiaAnteriorOuConseguinte(data, 1);

	}

	/**
	 * M�todo Date getDiaAnteriorOuConseguinte(Date data, int numeroDeDias) responsavel por recuperar
	 * DiaAnteriorOuConseguinte.
	 * 
	 * @param data
	 *            the data
	 * @param numeroDeDias
	 *            the numero de dias
	 * @return DiaAnteriorOuConseguinte.
	 */
	public static Date getDiaAnteriorOuConseguinte(Date data, int numeroDeDias) {

		Calendar c = new GregorianCalendar();
		c.setTime(data);
		c.add(Calendar.DAY_OF_MONTH, numeroDeDias);

		return c.getTime();

	}

	/*    *//**
	 * Transforma hora minuto em minuto.
	 * 
	 * @param horaMinuto
	 *            the hora minuto
	 * @return the long
	 */
	/*
	 * public static Map<String, Date> normalizaDataEHoraComDataBase(Date dataBase, Date horaInicio, Date horaFim) {
	 * Map<String, Date> result = new HashMap<String, Date>(); Date horaInicioAux = zeroDate(horaInicio); Date
	 * horaFimAux = zeroDate(horaFim); Date dataBaseAux = zeroTime(dataBase); // adicionar hora in�cio no retorno.
	 * result.put(GlobalConstants.DATA_INICIO, somaHorasNaData(dataBaseAux, horaInicioAux)); // se a hora fim for menor
	 * que a hora in�cio, soma um dia na data. if (horaFimAux.before(horaInicioAux)) {
	 * result.put(GlobalConstants.DATA_FIM, somaHorasNaData(getAmanha(dataBaseAux), horaFimAux)); } else {
	 * result.put(GlobalConstants.DATA_FIM, somaHorasNaData(dataBaseAux, horaFimAux)); } return result; }
	 */

	/**
	 * Esse m�todo transforma uma <code>String</code> no formato <b>HH:mm</b> em um valor em minutos.
	 * 
	 * @param horaMinuto
	 *            - String.
	 * @return Long com os minutos gastos nesse tempo passado em <i>horaMinuto</i>.
	 */
	public static Long transformaHoraMinutoEmMinuto(String horaMinuto) {
		// tem que ter um e apenas um ':'
		if (horaMinuto.indexOf(':') >= 0 && (horaMinuto.indexOf(':') == horaMinuto.lastIndexOf(':'))) {
			String[] splitted = horaMinuto.split(":");

			String horaAux = splitted[0].trim();
			String minutoAux = splitted[1].trim();
			try {
				// erro na representacao
				if (StringUtils.isEmpty(horaAux) || StringUtils.isEmpty(minutoAux)) {
					return null;
				}
				// somente numeros.
				if (!horaAux.matches("\\d+") || !minutoAux.matches("\\d+")) {
					return null;
				}

				// if (Long.parseLong(horaAux) < 0 || Long.parseLong(minutoAux) < 0) {
				// return null;
				// }
				return (Long.parseLong(horaAux) * 60) + Long.parseLong(minutoAux);
			} catch (NumberFormatException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Formata minuto em hora minuto.
	 * 
	 * @param minutos
	 *            the minutos
	 * @return the string
	 */
	public static String formataMinutoEmHoraMinuto(Long minutos) {
		Long minuto = Long.valueOf(minutos % 60L);
		Long hora = Long.valueOf(minutos / 60L);
		String retorno = null;

		if (minutos >= 0) {
			if (hora.toString().length() < 2) {
				retorno = "0" + hora + ":";
			} else {
				retorno = hora + ":";
			}
			if (minuto.toString().length() < 2) {
				retorno += "0" + minuto + ":";
			} else {
				retorno += minuto + ":";
			}
		}
		return retorno;

	}

	/**
	 * Transforma hora minuto em date.
	 * 
	 * @param horaMinuto
	 *            the hora minuto
	 * @return the date
	 */
	public static Date transformaHoraMinutoEmDate(String horaMinuto) {
		try {
			String[] splitted = horaMinuto.split(":");
			if (splitted.length != 2) {
				return null;
			}

			int hora = new Integer(splitted[0].trim()).intValue();
			int minuto = new Integer(splitted[1].trim()).intValue();

			Calendar c = new GregorianCalendar();
			c.setTime(zeroDate(zeroTime(new Date())));
			c.set(Calendar.HOUR_OF_DAY, hora);
			c.set(Calendar.MINUTE, minuto);

			return zeroDate(c.getTime());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Transforma date em hora minuto.
	 * 
	 * @param data
	 *            the data
	 * @return the string
	 */
	public static String transformaDateEmHoraMinuto(Date data) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("pt", "BR"));
		return sdf.format(data);
	}

	/**
	 * Formata data ddmmyyy.
	 * 
	 * @param data
	 *            the data
	 * @return the string
	 */
	public static String formataDataDDMMYYY(Date data) {
		return new SimpleDateFormat("dd/MM/yyyy").format(data);
	}

	public static boolean isBetween(Date data, Date dataInicio, Date dataFim) {

		if (data == null || dataInicio == null || dataFim == null) {
			return true;
		}

		DateTime dataComparacao = new DateTime(data).withTime(0, 0, 0, 0);
		DateTime dataInicioComparacao = new DateTime(dataInicio).withTime(0, 0, 0, 0);
		;
		DateTime dataFimComparacao = new DateTime(dataFim).withTime(0, 0, 0, 0);
		;

		if (dataInicioComparacao.compareTo(dataComparacao) <= 0 && dataComparacao.compareTo(dataFimComparacao) <= 0) {
			return true;
		}

		/*
		 * if (data.compareTo(dataInicio) >= 0 && data.compareTo(dataFim) <= 0) { return true; }
		 */

		return false;
	}

	
}
