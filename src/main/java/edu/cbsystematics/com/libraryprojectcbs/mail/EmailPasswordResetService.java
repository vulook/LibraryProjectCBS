package edu.cbsystematics.com.libraryprojectcbs.mail;

import edu.cbsystematics.com.libraryprojectcbs.aop.Loggable;
import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.repository.UserRepository;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import edu.cbsystematics.com.libraryprojectcbs.utils.period.ExpirationManager;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailPasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(EmailPasswordResetService.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmailPasswordResetService(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    public Mail buildPasswordResetEmail(User user, HttpServletRequest request) {
        // Create a new instance of Mail
        Mail mail = new Mail();
        mail.setFrom("admin@book-wise.com");
        mail.setTo(user.getEmail());
        mail.setSubject("Password reset request");

        // Populate the email template with necessary data
        Map<String, Object> modelAttributes = new HashMap<>();

        // Add the user object to the model
        modelAttributes.put("user", user);

        // Add a signature to the model
        modelAttributes.put("signature", "BookWise. ");

        // Build the verification URL and add it to the model
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String verificationUrl = url + "/reset-password?token=" + user.getVerificationCode();
        logger.info("Generated verification URL: {}", verificationUrl);
        modelAttributes.put("verificationUrl", verificationUrl);

        // Add expiry date to the model
        LocalDateTime expiryDateTime = ExpirationManager.expiryDateTimeVerificationCode(user.getPasswordResetDate());
        String expiryTimeToken = ExpirationManager.getExpiryDateTimeVerificationCode(expiryDateTime);
        modelAttributes.put("expiryTimeToken", expiryTimeToken);

        // Set the model for the email
        mail.setModel(modelAttributes);
        mail.setTemplateName("email/email-password-reset");
        logger.info("The password reset email is ready to be sent to: {}", user.getEmail());

        return mail;
    }


    public String verifyResetPassword(String token) {
        // Find user by verification code
        User user = userService.findByVerificationCode(token);

        // If user not found
        if (user == null) {
            logger.error("Verification failed: User not found for password reset token: {}", token);
            return "User not found for password reset token.";
        }

        // Check if the password reset verification code has expired
        boolean isExpired = ExpirationManager.isExpiredDateTimeVerificationCode(user.getPasswordResetDate());
        if (isExpired) {
            logger.error("Verification failed: Expired password reset token ({}) for: {}", token, user.getEmail());
            return "Password reset token has expired.";
        }

        // Return a success message
        return "SUCCESS";
    }

    @Loggable(value = ActionType.PASSWORD)
    public void changeForgottenPassword(String token, String password) {
        // Find user by verification code
        User user = userService.findByVerificationCode(token);

        // Encode the new password
        String updatedPassword = passwordEncoder.encode(password);

        user.setPassword(updatedPassword);    // Update the user's password
        user.setVerificationCode(null);       // Clear the field
        user.setPasswordResetDate(null);      // Clear the field
        userRepository.save(user);
    }

}