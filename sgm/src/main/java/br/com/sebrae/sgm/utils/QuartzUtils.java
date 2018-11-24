package br.com.sebrae.sgm.utils;

import java.util.Date;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzUtils {

	private static Scheduler scheduler;

	public static Scheduler getScheduler() throws SchedulerException {
		if (scheduler == null) {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
		}
		return scheduler;
	}

	public static void agendar(String nome, String grupo, Date dataHora, Class<? extends Job> classe,
			Map<String, Object> parametros) throws SchedulerException {

		JobDetail job = JobBuilder.newJob(classe).withIdentity(nome, grupo).build();

		if (parametros != null) {
			for (Map.Entry<String, Object> entry : parametros.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				job.getJobDataMap().put(key, value);
			}
		}

		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(nome, grupo).startAt(dataHora).build();
		getScheduler().scheduleJob(job, trigger);
	}

	public static void agendar(String nome, String grupo, String expression, Class<? extends Job> classe,
			Map<String, Object> parametros) throws SchedulerException {

		JobDetail job = JobBuilder.newJob(classe).withIdentity(nome, grupo).build();

		if (parametros != null) {
			for (Map.Entry<String, Object> entry : parametros.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				job.getJobDataMap().put(key, value);
			}
		}

		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(nome, grupo)
				.withSchedule(CronScheduleBuilder.cronSchedule(expression)).build();

		getScheduler().scheduleJob(job, trigger);
	}

	public static void removerJob(String nome, String grupo) throws SchedulerException {
		getScheduler().deleteJob(JobKey.jobKey(nome, grupo));
	}

}
