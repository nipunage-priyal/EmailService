package com.service;

import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.config.ProjectConfig;
import com.model.Recipient;

public class SenderService {

	static Supplier<Set<Recipient>> recepientsReader = () -> {
		return FileService.fileReader.apply(ProjectConfig.SEND_TO_IDS).stream().map(str -> str.split(","))
				.map(rec -> new Recipient(rec[0], rec[1], rec[2])).collect(Collectors.toSet());
	};
	final MessageBuilder mb = new MessageBuilder();

	public void sendEmail() {
		final FilterSuppliers fs = new FilterSuppliers();
		final Set<Recipient> recepients = recepientsReader.get().stream().filter(rec -> fs.shouldSend(rec))
				.collect(Collectors.toSet());

		recepients.forEach(rec -> {
			final MimeMessage msg = buildMessage.apply(rec);
			try {
				Transport.send(msg);
				System.out.println("Message sent");
				FileService.fileWriter.apply(ProjectConfig.SENT_EMAILS_FILE, rec.getEmailId());
				FileService.fileWriter.apply(ProjectConfig.AUDIT_FILE, mb.auditMessageBuilder.apply(rec));
			} catch (final MessagingException e) {
				e.printStackTrace();
			}
		});
	}

	Function<Recipient, MimeMessage> buildMessage = (recruiter) ->

	{
		return buildEmail(recruiter);
	};

	private Session getSession() {
		if (session == null)
			session = sessionSupplier.get();
		return session;
	}

	private MimeMessage buildEmail(final Recipient recruiter) {
		final MimeMessage msg = new MimeMessage(getSession());
		try {
			msg.setFrom(new InternetAddress(System.getProperty(ProjectConfig.USER_NAME)));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recruiter.getEmailId()));
			msg.setSubject(ProjectConfig.MAIL_SUBJECT + recruiter.getCompany());
			msg.setSentDate(new Date());
			msg.setText(mb.getMessage(recruiter));
			final BodyPart messageBody = new MimeBodyPart();
			messageBody.setText(mb.getMessage(recruiter));

			final Multipart multipart = new MimeMultipart();

			// Part two is attachment
			final MimeBodyPart messageBodyPart = new MimeBodyPart();
			final String attachment = ProjectConfig.ATTACHING_RESUME;
			final DataSource source = new FileDataSource(attachment);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(ProjectConfig.RESUMENAME);

			multipart.addBodyPart(messageBodyPart);
			multipart.addBodyPart(messageBody);

			// Send the complete message parts
			msg.setContent(multipart);
		} catch (final Exception e) {
			return null;
		}
		return msg;
	};

	static Session session = null;

	static Supplier<Session> sessionSupplier = () ->

	{
		final Authenticator passwordAuthenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(System.getProperty(ProjectConfig.USER_NAME),
						System.getProperty(ProjectConfig.PASSWORD));
			}
		};
		final Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		return Session.getInstance(props, passwordAuthenticator);
	};
}
