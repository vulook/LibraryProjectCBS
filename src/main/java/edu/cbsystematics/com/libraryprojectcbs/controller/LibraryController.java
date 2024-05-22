package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.aop.Loggable;
import edu.cbsystematics.com.libraryprojectcbs.config.security.UserAuthenticationUtils;
import edu.cbsystematics.com.libraryprojectcbs.dto.login.PasswordForgotDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.login.PasswordResetDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.login.UserRegistrationDTO;
import edu.cbsystematics.com.libraryprojectcbs.exception.ValidationExceptionHandler;
import edu.cbsystematics.com.libraryprojectcbs.exception.VerificationCodeCreationException;
import edu.cbsystematics.com.libraryprojectcbs.mail.EmailPasswordResetService;
import edu.cbsystematics.com.libraryprojectcbs.mail.EmailService;
import edu.cbsystematics.com.libraryprojectcbs.mail.EmailVerificationService;
import edu.cbsystematics.com.libraryprojectcbs.mail.Mail;
import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import edu.cbsystematics.com.libraryprojectcbs.service.BookService;
import edu.cbsystematics.com.libraryprojectcbs.service.CardService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserRoleService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import edu.cbsystematics.com.libraryprojectcbs.utils.role.RoleUtilsForStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.MIN_AGE;
import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.ROLE_READER;


@Controller
@RequestMapping("/")
public class LibraryController {

    private final UserService userService;

    private final BookService bookService;

    private final CardService cardService;

    private final UserRoleService userRoleService;

    private final EmailService emailService;

    private final EmailVerificationService verificationService;

    private final EmailPasswordResetService passwordResetService;

    private final UserAuthenticationUtils userAuthenticationUtils;

    @Autowired
    public LibraryController(UserService userService, BookService bookService, CardService cardService, UserRoleService userRoleService, EmailService emailService, EmailVerificationService verificationService, EmailPasswordResetService passwordResetService, UserAuthenticationUtils userAuthenticationUtils) {
        this.userService = userService;
        this.bookService = bookService;
        this.cardService = cardService;
        this.userRoleService = userRoleService;
        this.emailService = emailService;
        this.verificationService = verificationService;
        this.passwordResetService = passwordResetService;
        this.userAuthenticationUtils = userAuthenticationUtils;
    }


    // This method maps the GET request to the start page
    @GetMapping()
    public String showStartPage(Model model) {
        return "index";
    }


    // This method maps the GET request to the "/library" endpoint
    @GetMapping("/library")
    public String showPublicPage(Model model, Authentication authentication) {
        String roles = userAuthenticationUtils.getCurrentUserRoles(authentication);
        String role = (roles != null) ? RoleUtilsForStatus.getRoleLabel(roles) : "anonymous";

        UserRole reader = userRoleService.findRoleByName(ROLE_READER);
        int countUser = userService.getTotalUsersByRoleId(reader.getId());
        int countBook = bookService.getAllBooks().size();
        Long countReadingBooks = cardService.countReadingBooks();

        model.addAttribute("countReadingBooks", countReadingBooks);
        model.addAttribute("countUser", countUser);
        model.addAttribute("countBook", countBook);
        model.addAttribute("role", role);
        return "public/library";
    }


    // This method maps the GET request to the "/login" endpoint
    @GetMapping("/login")
    public String getLoginPage(HttpServletRequest request, Model model) {

        // Get an error message from session
        String errorMessage = (String) request.getSession().getAttribute("err");
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            request.getSession().removeAttribute("err");  // Remove a error message from session
        }

        // Get a logout message from session
        String logout = (String) request.getSession().getAttribute("logout");
        if (logout != null) {
            model.addAttribute("logout", logout);
            request.getSession().removeAttribute("logout");  // Remove a logout message from session
        }

        // Get a reset message from session
        String resetMessage = (String) request.getSession().getAttribute("reset");
        if (resetMessage != null) {
            model.addAttribute("resetMessage", resetMessage);
            request.getSession().removeAttribute("reset");  // Remove a reset message from session
        }

        return "logging/login";
    }


    //****************************  Registration Form **************************** //

    @ModelAttribute("user")
    public UserRegistrationDTO userRegistrationDTO() {
        return new UserRegistrationDTO();
    }


    // This method maps the GET request to the "/registration" endpoint
    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("age", MIN_AGE);
        return "logging/registration";
    }


    // This method maps the POST request to the "/registration" endpoint
    @PostMapping("/registration")
    public String registerNewUser(@ModelAttribute("user") @Valid UserRegistrationDTO userRegistrationDTO,
                                  BindingResult result, HttpServletRequest request) {

        // Check if email is already in use
        if (emailExist(userRegistrationDTO.getEmail())) {
            result.rejectValue("email", "user.exist", "There is already an account registered with that email");
        }

        // If there are validation errors, return to the registration page
        if (result.hasErrors()) {
            ValidationExceptionHandler.handleValidationErrors(result);
            return "logging/registration";
        }

        // Save the user registration details if everything is valid
        User savedUser = userService.createUserRegistration(userRegistrationDTO);

        // Build an email with the verification instructions for the new user.
        Mail mail = verificationService.buildVerificationEmail(savedUser, request);

        // Send the verification email to the user.
        emailService.sendEmail(mail);

        return "redirect:/registration?success";
    }


    // This method maps the GET request to the "/verify" endpoint
    @GetMapping("/verify")
    public String verifyUser(@RequestParam("verificationCode") String verificationCode, RedirectAttributes redirectAttributes) {

        // Verify user in the service
        String result = verificationService.verifyUser(verificationCode);

        if ("SUCCESS".equals(result)) {

            redirectAttributes.addFlashAttribute("verificationSuccess", true);

        } else {

            redirectAttributes.addFlashAttribute("verificationError", result);
        }

        return "redirect:/login";
    }


    //****************************  Password Forgot Form **************************** //


    // Create a new instance of PasswordForgotDTO
    @ModelAttribute("passwordForgotForm")
    public PasswordForgotDTO passwordForgotDTO() {
        return new PasswordForgotDTO();
    }

    @GetMapping("/forgot-password")
    public String displayPasswordForgotPage() {
        return "logging/password-forgot";
    }


    // Handles POST requests to "/forgot-password"
    @Loggable(value = ActionType.PASSWORD)
    @PostMapping("/forgot-password")
    public String sendPasswordResetEmail(@ModelAttribute("passwordForgotForm") @Valid PasswordForgotDTO form, BindingResult result, HttpServletRequest request) {
        // Check email
        if (!emailExist(form.getEmail())) {
            result.rejectValue("email", "user.notExist", "We don't have an account associated with that email address!");
        }

        // If there are errors, return to the forgot password page
        if (result.hasErrors()) {
            ValidationExceptionHandler.handleValidationErrors(result);
            return "logging/password-forgot";
        }

        // Find the user by email
        Optional<User> user = userService.findByEmail(form.getEmail());

        // Create a new password reset VerificationCode if user is present
        user.ifPresentOrElse(
                u -> {
                    userService.createPasswordResetToken(u);

                    if (u.getVerificationCode() == null || u.getPasswordResetDate() == null) {
                        throw new VerificationCodeCreationException("Error creating password reset verificationCode.");

                    } else {
                        // Build an email with the password reset instructions
                        Mail mail = passwordResetService.buildPasswordResetEmail(u, request);
                        // Send the email containing the password reset link
                        emailService.sendEmail(mail);
                    }
                },

                () -> result.rejectValue("email", "user.notExist", "We don't have an account associated with that email address.")
        );

        // Redirect to the forgot password page with a success message
        return "redirect:/forgot-password?success";
    }


    //****************************  Password Reset Form **************************** //

    @ModelAttribute("passwordResetForm")
    public PasswordResetDTO passwordResetDTO() {
        return new PasswordResetDTO();
    }


    @GetMapping("/reset-password")
    public String displayResetPasswordPage(@RequestParam(required = false) String token, Model model) {

        // Verify the password reset request using the provided token
        String result = passwordResetService.verifyResetPassword(token);

        // If verification fails
        if (!"SUCCESS".equals(result)) {

            model.addAttribute("error", result);

        } else {

            model.addAttribute("token", token);
        }

        return "logging/password-reset";
    }


    @PostMapping("/reset-password")
    public String handlePasswordReset(@ModelAttribute("passwordResetForm") @Valid PasswordResetDTO form,
                                      BindingResult result, RedirectAttributes redirectAttributes, HttpSession session) {

        // Check for validation errors
        if (result.hasErrors()) {
            ValidationExceptionHandler.handleValidationErrors(result);
            redirectAttributes.addFlashAttribute(BindingResult.class.getName() + ".passwordResetForm", result);
            redirectAttributes.addFlashAttribute("passwordResetForm", form);

            return "redirect:/reset-password?token=" + form.getToken();
        }

        // Get the token and password from the form
        String token = form.getToken();
        String password = form.getPassword();
        passwordResetService.changeForgottenPassword(token, password);

        // Set session attribute
        String resetMessage = "Your password has been reset successfully";
        session.setAttribute("reset", resetMessage);

        // Redirect to the login page with a reset success message
        return "redirect:/login?reset";
    }



    // Check if an email exists in the database
    private boolean emailExist(String email) {
        return userService.findByEmail(email).isPresent();
    }


}