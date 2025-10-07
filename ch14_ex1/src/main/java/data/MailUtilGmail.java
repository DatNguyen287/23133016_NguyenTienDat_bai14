package data;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class MailUtilGmail {

    public static void sendMail(String to, String from,
            String subject, String body, boolean bodyIsHTML)
            throws MessagingException {

        // 1. Cấu hình SMTP của Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // 2. Đăng nhập vào tài khoản Gmail gửi thư
        final String username = System.getenv("EMAIL_USERNAME");
        final String password = System.getenv("EMAIL_APP_PASSWORD");// app password ở bước 1

        Session session = Session.getInstance(props,
            new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        
        session.setDebug(true); // Bật log để theo dõi lỗi nếu có

        // 3. Tạo email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);

        if (bodyIsHTML) {
            message.setContent(body, "text/html; charset=UTF-8");
        } else {
            message.setText(body);
        }

        // 4. Gửi email
        Transport.send(message);
        System.out.println("✅ Email đã được gửi tới " + to);
    }
}
