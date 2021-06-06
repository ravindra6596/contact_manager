package com.springboot.contactmgm.service;

import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class Email {
	public boolean sendMessage(String subject, String message, String to) {
		// varible for gmail host
		boolean f = false;
		String from = "ravindrapatil.alceatechnologies@gmail.com";
		String host = "smtp.gmail.com";
		// get system properties
		Properties properties = System.getProperties();
		// System.out.println(properties);
		// setting informaition to properties objects

		// host set
		// key and value
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", 465);
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Step 1: to get session objects
		// authenticator is annonymous class
		Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {

			@Override
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication("ravindrapatil.alceatechnologies@gmail.com",
						"ravindra6596");
			}

		});
		session.setDebug(true);
		// step 2: compose the message[text,multimedia]
		MimeMessage m = new MimeMessage(session);
		try {
			// from mail
			m.setFrom(from);
			// adding receipient to message To
			m.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
			// adding subject to message
			m.setSubject(subject);
			// adding text to message only message not attachement
			// m.setText(message);
			m.setContent(message, "text/html");
			// Step 3: send the message using transport class
			javax.mail.Transport.send(m);
			System.out.println("Send Successfully!!!!!");
			f = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}
}
