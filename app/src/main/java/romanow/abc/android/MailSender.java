package romanow.abc.android;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import romanow.abc.android.service.AppData;

public class MailSender{
    private MainActivity face;
    public MailSender(MainActivity face0){
        face = face0;
        }
    public void sendMail(FileDescription data) throws Exception{
        LoginSettings ws = AppData.ctx().loginSettings();
        final String from = ws.getMailBox();
        String username = from.substring(0,from.indexOf("@")-1);
        String host = ws.getMailHost();
        int port = ws.getMailPort();
        final String pass = ws.getMailPass();
        //------------------------------------------------------------
        Properties props = System.getProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.socketFactory.port", ""+port); //143
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.starttls.enable", "true");
        //com.sun.net.ssl.internal.ssl.
        //Security.addProvider(new Provider());
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //return new PasswordAuthentication("romanow", "streichholz");
                return new PasswordAuthentication(from, pass);
                }
            });
        MimeMessage message = new MimeMessage(session); // email message
        message.setFrom(new InternetAddress(from));     // setting header fields
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(ws.getMailToSend()));
        message.setSubject("Датчик "+data.toString()); // subject line
        String text = "Опоры России гудят "+data.toString()+" "+data.getGps().toString();
        MimeMultipart multipart = new MimeMultipart();
        //Первый кусочек - текст письма
        MimeBodyPart part1 = new MimeBodyPart();
        part1.addHeader("Content-Type", "text/plain; charset=UTF-8");
        part1.setDataHandler(new DataHandler(text, "text/plain; charset=\"utf-8\""));
        multipart.addBodyPart(part1);
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        String fileName = AppData.ctx().androidFileDirectory()+"/"+data.getOriginalFileName();
        FileDataSource source = new FileDataSource(new File(fileName));
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(MimeUtility.encodeWord(fileName));
        multipart.addBodyPart(messageBodyPart);
        message.setContent(multipart);
        message.setText(text);
        message.setSentDate(new java.util.Date());
        Transport.send(message);
        }
}