package com.wjb.zero.util.core;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @description EmailEmitter
 * @author wjb
 */
@Component
public class EmailEmitter {

	/**
	 * @description 日志
	 */
	private Logger logger = LoggerFactory.getLogger(EmailEmitter.class);

	/**
	 * @description sendMessage
	 * @author wjb
	 */
	public void sendMessage(String subject, String content, String recipient) throws Exception {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp"); //
		props.setProperty("mail.smtp.host", emailSendServer); //
		props.setProperty("mail.smtp.auth", "true"); //

		Session session = Session.getInstance(props);
		session.setDebug(true);

		try {
			MimeMessage message = createMimeMessage(session, subject, content, recipient);
			Transport transport = session.getTransport();
			transport.connect(emailSendAccount, emailSendPwd);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			logger.error("邮件发送失败::" + e.getMessage());
			throw new Exception("邮件发送失败！！");
		}
	}

	/**
	 * @description
	 * @author wjb
	 */
	public MimeMessage createMimeMessage(Session session, String subject, String content, String receiveMail)
			throws Exception {
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(emailSendAccount, emailSendNickname, "UTF-8"));
			String[] rcp = receiveMail.split(",");
			for (int i = 0; i < rcp.length; i++) {
				message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(rcp[i], null, "UTF-8"));
			}
			// message.setRecipient(MimeMessage.RecipientType.TO, new
			// InternetAddress(receiveMail, null, "UTF-8"));
			message.setSubject(subject, "UTF-8");
			message.setContent(content, "text/html;charset=UTF-8");
			message.setSentDate(new Date());
			message.saveChanges();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			logger.error("邮件创建失败::" + e.getMessage());
			throw new Exception("邮件创建失败！！");
		}
		return message;
	}

	/**
	 * @description sendMessage
	 * @author wjb
	 */
	public void sendMessage(String subject, String content, List<String> recipient) throws Exception {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp"); //
		props.setProperty("mail.smtp.host", emailSendServer); //
		props.setProperty("mail.smtp.auth", "true"); //

		Session session = Session.getInstance(props);
		session.setDebug(true);

		try {
			MimeMessage message = createMimeMessage(session, subject, content, recipient);
			Transport transport = session.getTransport();
			transport.connect(emailSendAccount, emailSendPwd);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			logger.error("邮件发送失败::" + e.getMessage());
			throw new Exception("邮件发送失败！！");
		}
	}

	/**
	 * @description
	 * @author wjb
	 */
	public MimeMessage createMimeMessage(Session session, String subject, String content, List<String> receiveMail)
			throws Exception {
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(emailSendAccount, emailSendNickname, "UTF-8"));
			for (int i = 0; i < receiveMail.size(); i++) {
				message.addRecipient(MimeMessage.RecipientType.TO,
						new InternetAddress(receiveMail.get(i), null, "UTF-8"));
			}
			message.setSubject(subject, "UTF-8");
			message.setContent(content, "text/html;charset=UTF-8");
			message.setSentDate(new Date());
			message.saveChanges();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			logger.error("邮件创建失败::" + e.getMessage());
			throw new Exception("邮件创建失败！！");
		}
		return message;
	}

	private String emailSendAccount;
	private String emailSendPwd;
	private String emailSendNickname;
	private String emailSendServer;

	@PostConstruct
	public void initMethod() {
		System.out.println(">>>>>>>>>initMethod<<<<<<<<<<<");
		emailSendAccount = ConfigurationUtils.getStringValueFromProperties("email_send_account");
		emailSendPwd = ConfigurationUtils.getStringValueFromProperties("email_send_pwd");
		emailSendNickname = ConfigurationUtils.getStringValueFromProperties("email_send_nickname");
		emailSendServer = ConfigurationUtils.getStringValueFromProperties("email_send_server");
		System.out.println("=================" + emailSendAccount);
	}

}
