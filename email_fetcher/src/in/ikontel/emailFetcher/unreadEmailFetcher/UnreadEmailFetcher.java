package in.ikontel.emailFetcher.unreadEmailFetcher;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class UnreadEmailFetcher {
	 private Properties properties;

	    public UnreadEmailFetcher(String propertiesFilePath) throws IOException {
	        properties = new Properties();
	        properties.load(new FileInputStream(propertiesFilePath));
	    }

	    public void fetchUnreadEmails() {
	        String host = properties.getProperty("mail.host");
	        String port = properties.getProperty("mail.port");
	        String username = properties.getProperty("mail.username");
	        String password = properties.getProperty("mail.password");

	        Properties mailProps = new Properties();
	        mailProps.put("mail.store.protocol", "imaps");
	        mailProps.put("mail.imaps.host", host);
	        mailProps.put("mail.imaps.port", port);
	        mailProps.put("mail.imaps.ssl.enable", "true");
	        mailProps.put("mail.imap.connectiontimeout", "60000"); // Connection timeout in milliseconds (60 seconds)
	        mailProps.put("mail.imap.timeout", "60000"); 

	        try {
	            Session emailSession = Session.getDefaultInstance(mailProps);
	            Store store = emailSession.getStore("imaps");
	            store.connect(username, password);

	            // Open Inbox Folder
	            Folder inbox = store.getFolder("INBOX");
	            inbox.open(Folder.READ_ONLY);

	            // Fetch Unread Emails
	            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

	            // Iterate over each email
	            for (Message message : messages) {
	                String from = ((MimeMessage) message).getFrom()[0].toString();
	                String subject = message.getSubject();
	                String content = message.getContent().toString();
	                saveEmailToDatabase(from, subject, content);
	            }

	            inbox.close(false);
	            store.close();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    public void saveEmailToDatabase(String from, String subject, String content) {
	        String dbUrl = properties.getProperty("db.url");
	        String dbUsername = properties.getProperty("db.username");
	        String dbPassword = properties.getProperty("db.password");

	        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
	            String sql = "INSERT INTO emails (sender, subject, body) VALUES (?, ?, ?)";
	            try (PreparedStatement statement = conn.prepareStatement(sql)) {
	                statement.setString(1, from);
	                statement.setString(2, subject);
	                statement.setString(3, content);
	                statement.executeUpdate();
	                System.out.println("Email saved to database.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public static void main(String[] args) {
	        if (args.length != 1) {
	            System.out.println("Usage: java -jar EmailFetcher.jar <path-to-config.properties>");
	            return;
	        }

	        String propertiesFilePath = args[0];

	        try {
	        	UnreadEmailFetcher fetcher = new UnreadEmailFetcher(propertiesFilePath);
	            fetcher.fetchUnreadEmails();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
}
