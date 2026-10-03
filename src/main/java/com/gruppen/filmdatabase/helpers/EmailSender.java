package com.gruppen.filmdatabase.helpers;

import com.gruppen.filmdatabase.entity.FilmError;
import com.gruppen.filmdatabase.entity.FilmInvitation;
import com.gruppen.filmdatabase.entity.User;
import com.gruppen.filmdatabase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;
import java.util.Properties;


@Component
public class EmailSender {

    private String twofaEmailSubject = "Login Secret";
    private String twofaEmailBody = "Your login secret is: ";

    private String invitationEmailSubject = "Film Invitation";

    private String errorEmailSubject = "Film Error";
    private String invitationEmailBody = "You have been invited to a movie";

    private String invitationResponseEmailBody = "You have a reply to an invitation you sent.";

    private String filmErrorEmailBody = "You have received an error report for a movie!";

    private String fromEmail = "heisenberg_soko@yahoo.com";
    private String fromEmailPassword = "owbogkwoxocdasgj";

    @Autowired
    private UserRepository userRepository;

    public void sendtwofaEmail(String email, String secretKey) {

        String emailContent = twofaEmailBody + secretKey;

        sendEmail(email, fromEmail, emailContent, twofaEmailSubject);

    }

    public void sendInvitationEmail(FilmInvitation filmInvitation) {

        String emailContent = invitationEmailBody;

        emailContent += "\nTitle: " + filmInvitation.getFilm().getName() + "\n";

        emailContent += "\nDate: " + filmInvitation.getInvitedAt() + "\n";

        emailContent += "\nInviting User: " + filmInvitation.getInvitingUser().getEmail() + "\n";


        sendEmail(filmInvitation.getInvitedUser().getEmail(), fromEmail, emailContent, invitationEmailSubject);
    }

    public void sendInvitationResponseEmail(FilmInvitation filmInvitation) {

        String emailContent = invitationResponseEmailBody;

        emailContent += "\nTitle: " + filmInvitation.getFilm().getName() + "\n";

        emailContent += "\nDate: " + filmInvitation.getInvitedAt() + "\n";

        emailContent += "\nInvited user: " + filmInvitation.getInvitedUser().getEmail() + "\n";

        emailContent += "\nInviting Status: " + filmInvitation.getStatus().getDisplayValue() + "\n";


        sendEmail(filmInvitation.getInvitingUser().getEmail(), fromEmail, emailContent, invitationEmailSubject);
    }

    public void sendFilmErrorEmail(FilmError filmError) {

        String emailContent = filmErrorEmailBody;

        emailContent += "\nTitle: " + filmError.getFilm().getName() + "\n";

        emailContent += "\nDate: " + filmError.getReportedAt() + "\n";

        emailContent += "\nReporting User: " + filmError.getReportedBy().getEmail() + "\n";

        if (filmError.getErrorMessage() != null) {
            emailContent += "\nRemark: " + filmError.getErrorMessage() + "\n";

        }

        List<User> adminUsers = userRepository.findByIsAdmin(true);

        for (User adminUser : adminUsers) {
            sendEmail(adminUser.getEmail(), fromEmail, emailContent, errorEmailSubject);
        }

    }

    private void sendEmail(String toEmail, String fromEmail, String emailBody, String emailSubject) {
        try {
            String smtpHostServer = "smtp.mail.yahoo.com";

            Properties props = System.getProperties();

            props.put("mail.smtp.host", smtpHostServer);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.user", fromEmail);
            props.setProperty("mail.smtp.password", fromEmailPassword);

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(fromEmail, fromEmailPassword);
                        }
                    });

            MimeMessage msg = new MimeMessage(session);

            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(fromEmail, "NoReply"));

            msg.setReplyTo(InternetAddress.parse(fromEmail, false));

            msg.setSubject(emailSubject, "UTF-8");

            msg.setText(emailBody, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            System.out.println("Message is ready");
            Transport.send(msg);

            System.out.println("Email Sent Successfully!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
