package br.com.sebrae.sgm.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.inject.Named;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import br.com.sebrae.sgm.utils.PropertiesUtils;

@Named
public class MailService implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	public void sendEmail(String destinaray, String subject, String html) throws NamingException, AddressException,
			MessagingException {

		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		Session session = (Session) envCtx.lookup("mail/Session");
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PropertiesUtils.getInstance("app").getProperty("app.mail.from")));
		InternetAddress to[] = new InternetAddress[1];
		to[0] = new InternetAddress(destinaray);
		message.setRecipients(Message.RecipientType.TO, to);
		message.setSubject(subject);
		message.setContent(html, "text/plain");
		Transport.send(message);
	}

	public void sendEmailForTemplate(String destinaray, String subject, String template, Map<String, String> params,
			List<File> attachments) throws NamingException, AddressException, MessagingException, IOException {

		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		Session session = (Session) envCtx.lookup("mail/Session");
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PropertiesUtils.getInstance("app").getProperty("app.mail.from")));
		InternetAddress to[] = new InternetAddress[1];
		to[0] = new InternetAddress(destinaray);
		message.setRecipients(Message.RecipientType.TO, to);

		message.setSubject(subject);

		InputStream in = this.getClass().getResourceAsStream("/" + template);
		String content = IOUtils.toString(in);

		if (params != null && content != null) {
			for (String key : params.keySet()) {
				content = StringUtils.replace(content, "${" + key + "}", params.get(key));
			}
		}

		if (attachments != null) {
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(content, "text/html; charset=utf-8");
			Multipart multipart = new MimeMultipart();

			for (File att : attachments) {
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(att);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(att.getName());
				multipart.addBodyPart(messageBodyPart);
			}

			message.setContent(multipart);

		} else {
			message.setContent(content, "text/html; charset=utf-8");
		}

		Transport.send(message);
	}

	public void sendEmailForTemplateReplyTo(String destinaray, String replyTo, String subject, String template,
			Map<String, String> params, List<File> attachments) throws NamingException, AddressException,
			MessagingException, IOException {

		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		Session session = (Session) envCtx.lookup("mail/Session");
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PropertiesUtils.getInstance("app").getProperty("app.mail.from")));
		InternetAddress to[] = new InternetAddress[1];
		to[0] = new InternetAddress(destinaray);
		message.setRecipients(Message.RecipientType.TO, to);

		message.setReplyTo(new InternetAddress[] { new InternetAddress(replyTo) });

		message.setSubject(subject);

		InputStream in = this.getClass().getResourceAsStream("/" + template);
		String content = IOUtils.toString(in);

		if (params != null && content != null) {
			for (String key : params.keySet()) {
				content = StringUtils.replace(content, "${" + key + "}", params.get(key));
			}
		}

		if (attachments != null) {
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(content, "text/html; charset=utf-8");
			Multipart multipart = new MimeMultipart();

			for (File att : attachments) {
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(att);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(att.getName());
				multipart.addBodyPart(messageBodyPart);
			}

			message.setContent(multipart);

		} else {
			message.setContent(content, "text/html; charset=utf-8");
		}

		Transport.send(message);
	}

	public void sendEmailFromTemplateAnexos(List<String> destinaray, String subject, String template,
			Map<String, String> params, List<File> anexos) throws NamingException, AddressException,
			MessagingException, IOException {

		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		Session session = (Session) envCtx.lookup("mail/Session");
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PropertiesUtils.getInstance("app").getProperty("app.mail.from")));
		InternetAddress to[] = new InternetAddress[destinaray.size()];

		for (int i = 0; i < destinaray.size(); i++) {
			to[i] = new InternetAddress(destinaray.get(i));
		}
		message.setRecipients(Message.RecipientType.BCC, to);
		message.setSubject(subject);

		MimeMultipart mpRoot = new MimeMultipart("mixed");
		MimeMultipart mpContent = new MimeMultipart("alternative");
		MimeBodyPart contentPartRoot = new MimeBodyPart();
		contentPartRoot.setContent(mpContent);
		mpRoot.addBodyPart(contentPartRoot);

		MimeBodyPart mpbHtml = new MimeBodyPart();

		InputStream in = this.getClass().getResourceAsStream("/" + template);
		String content = IOUtils.toString(in);

		if (params != null && content != null) {
			for (String key : params.keySet()) {
				content = StringUtils.replace(content, "${" + key + "}", params.get(key));
			}
		}

		mpbHtml.setContent(content, "text/html; charset=utf-8");
		mpContent.addBodyPart(mpbHtml);

		for (File attach : anexos) {
			MimeBodyPart mpbAnexo = new MimeBodyPart();
			DataSource source = new FileDataSource(attach);
			mpbAnexo.setDisposition(Part.ATTACHMENT);
			mpbAnexo.setDataHandler(new DataHandler(source));
			mpbAnexo.setFileName(attach.getName());
			mpRoot.addBodyPart(mpbAnexo);
		}

		message.setContent(mpRoot);
		message.saveChanges();

		Transport.send(message);
	}

	public void sendEmail(List<String> destinaray, String subject, String html, List<File> anexos)
			throws NamingException, AddressException, MessagingException, IOException {

		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		Session session = (Session) envCtx.lookup("mail/Session");
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PropertiesUtils.getInstance("app").getProperty("app.mail.from")));
		InternetAddress to[] = new InternetAddress[destinaray.size()];

		for (int i = 0; i < destinaray.size(); i++) {
			to[i] = new InternetAddress(destinaray.get(i));
		}
		message.setRecipients(Message.RecipientType.TO, to);
		message.setSubject(subject);

		MimeMultipart mpRoot = new MimeMultipart("mixed");
		MimeMultipart mpContent = new MimeMultipart("alternative");
		MimeBodyPart contentPartRoot = new MimeBodyPart();
		contentPartRoot.setContent(mpContent);
		mpRoot.addBodyPart(contentPartRoot);

		MimeBodyPart mpbHtml = new MimeBodyPart();
		mpbHtml.setContent(html, "text/html; charset=utf-8");
		mpContent.addBodyPart(mpbHtml);

		MimeBodyPart mpbAnexo = new MimeBodyPart();

		for (File attach : anexos) {
			DataSource source = new FileDataSource(attach);
			mpbAnexo.setDisposition(Part.ATTACHMENT);
			mpbAnexo.setDataHandler(new DataHandler(source));
			mpbAnexo.setFileName(attach.getName());
		}

		mpRoot.addBodyPart(mpbAnexo);
		message.setContent(mpRoot);
		message.saveChanges();

		Transport.send(message);
	}

}