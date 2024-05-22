package edu.cbsystematics.com.libraryprojectcbs.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@Service
public class EmailSendReminderService {

    private static final Logger logger = LoggerFactory.getLogger(EmailSendReminderService.class);

    public Mail buildReminderEmail(String fullName, String email, String ISBN, String bookName, LocalDate startDate, LocalDate endDate, Long daysOverdue) {
        // Create a new instance of Mail
        Mail mail = new Mail();
        mail.setFrom("admin@book-wise.com");
        mail.setTo(email);
        mail.setSubject("Reminder: Return the Book to BookWise Library");

        // Email template completion
        Map<String, Object> modelAttributes = new HashMap<>();

        // Add user and book information to the model
        modelAttributes.put("fullName", fullName);
        modelAttributes.put("ISBN", ISBN);
        modelAttributes.put("bookName", bookName);
        modelAttributes.put("startDate", startDate);
        modelAttributes.put("endDate", endDate);
        modelAttributes.put("daysOverdue", daysOverdue);

        // Add a signature to the model
        modelAttributes.put("signature", "BookWise. ");

        // Set the model for the email
        mail.setModel(modelAttributes);
        mail.setTemplateName("email/email-reminder");
        logger.info("The reminder email is ready to be sent to: {}", email);

        return mail;
    }

}