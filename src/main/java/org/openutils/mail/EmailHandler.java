package org.openutils.mail;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
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

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

/**
 * 
 * @author Roman
 *
 */
public class EmailHandler
{
	public static final String SUBJECT = "subject";
	public static final String BODY = "body";
	public static final String ATTACHMENT = "attachment";

	private static final String CSV_DELIMITER = ",";

	private static final Logger log = Logger.getLogger("app." + EmailHandler.class.getName() );

	private String smtpAddress;
	private String fromAddress;
	private Session session;

	private String user;
	private String password;

	private InternetAddress[] recipients;
	private boolean debug = true;

	public EmailHandler(Configuration config) throws Exception
	{
		smtpAddress = config.getString(ReportingProperties.REPORTING_SMTP_HOST);
		fromAddress = config.getString(ReportingProperties.REPORTING_FROM);

		Boolean doAuth = config.getBoolean(ReportingProperties.REPORTING_AUTH);
		session = getSession(doAuth);

		String recipientsCsv = config.getString(ReportingProperties.REPORTING_RECIPIENTS);
		if(recipientsCsv != null)
		{
			parseRecipients(recipientsCsv);
		}
	}

	public EmailHandler(Properties sysProperties) throws Exception
	{
		smtpAddress = sysProperties.getProperty(ReportingProperties.REPORTING_SMTP_HOST);
		fromAddress = sysProperties.getProperty(ReportingProperties.REPORTING_FROM);
		user = sysProperties.getProperty(ReportingProperties.REPORTING_USER);
		password = sysProperties.getProperty(ReportingProperties.REPORTING_PASS);

		String authStr = sysProperties.getProperty(ReportingProperties.REPORTING_AUTH);
		Boolean doAuth = new Boolean(authStr);
		session = getSession(doAuth);

		String recipientsCsv = sysProperties.getProperty(ReportingProperties.REPORTING_RECIPIENTS);
		if(recipientsCsv != null)
		{
			parseRecipients(recipientsCsv);
		}
	}

	public void parseRecipients(String recipientsCsv) throws Exception
	{
		String[] recipientAddrs = recipientsCsv.split(CSV_DELIMITER);
		if(recipientAddrs.length == 0)
			throw new IllegalArgumentException("Invalid recipient list");
		else
		{
			log.info("Parsed " + recipientAddrs.length + " recipients.");	
		}

		recipients = new InternetAddress[recipientAddrs.length];
		for(int i = 0; i < recipientAddrs.length; i++)
		{
			recipients[i] = new InternetAddress( recipientAddrs[i].trim() );	
		}
	}

	public void setRecipients(List<? extends EmailRecipient> recipientList) throws Exception
	{
		this.recipients = new InternetAddress[recipientList.size()];

		for(int i = 0; i < recipientList.size(); i++)
		{
			recipients[i] = new InternetAddress( recipientList.get(i).getAddress() );
		}
	}

	public void sendInfoMessage(String subject, String body, String fileName) throws MessagingException
	{
		log.info("Sending email.");

		MimeMessage msg = new MimeMessage(session);
		msg.setFrom( new InternetAddress(fromAddress) );
		msg.setRecipients(Message.RecipientType.TO, recipients);
		msg.setSubject(subject);

		Multipart mp = new MimeMultipart();

		if(body != null)
		{
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(body);
			mp.addBodyPart(mbp1);
		}

		if(fileName != null)
		{
			FileDataSource dataSource = new FileDataSource(fileName);

			MimeBodyPart mbp2 = new MimeBodyPart();
			mbp2.setDataHandler( new DataHandler(dataSource) );

			int index = fileName.lastIndexOf("/");
			if( index!= -1 )
			{
				mbp2.setFileName( fileName.substring(index +1) );	
			}
			else
			{
				mbp2.setFileName(fileName);	
			}

			mp.addBodyPart(mbp2);
		}

		msg.setContent(mp);
		msg.setSentDate( new Date() );

		sendMimeMessage(msg);
	}

	private Session getSession(boolean doAuth)
	{
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", smtpAddress);

		Session session = null;
		if(doAuth)
		{
			props.setProperty("mail.smtp.auth", "true");

			Authenticator auth = new PopupAuthenticator();
			session = Session.getInstance(props, auth);
		}
		else
		{
			session = Session.getInstance(props);
		}

		//TODO: Redirect debug output with: session.setDebugOut(out)
		session.setDebug(debug);

		return session;
	}

	private void sendMimeMessage(MimeMessage msg) throws MessagingException
	{
		Transport.send(msg);
	}

	public void sendErrorMessage(String subject, String body)
	{
		try
		{	
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom( new InternetAddress(fromAddress) );
			msg.setRecipients(Message.RecipientType.TO, recipients);
			msg.setSubject(subject);

			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText("Encountered the following error: \n\n" + body);

			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);

			msg.setContent(mp);
			msg.setSentDate( new Date() );

			sendMimeMessage(msg);
		}
		catch(Exception e)
		{
			log.error(e);
		}
	}

	class PopupAuthenticator extends Authenticator 
	{
		public PasswordAuthentication getPasswordAuthentication() 
		{
			return new PasswordAuthentication(user, password);
		}
	}
}
