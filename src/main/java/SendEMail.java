import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.Properties;

public class SendEMail {
    public static void send(String type, String rec) {
        Properties properties = Props.getProps().getProperties();
        final String user = properties.getProperty("mail.smtp.user");//change accordingly
        final String password = properties.getProperty("mail.smtp.pass");//change accordingly
        final String to = properties.getProperty("mail.smtp.to");//change accordingly

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        //Compose the message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(type + " - " + LocalDate.now());
            message.setText(rec);
            //send the message
            Transport.send(message);
            System.out.println("message sent successfully...");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
