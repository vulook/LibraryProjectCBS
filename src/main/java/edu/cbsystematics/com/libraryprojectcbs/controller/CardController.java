package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.config.security.UserAuthenticationUtils;
import edu.cbsystematics.com.libraryprojectcbs.dto.card.CardDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.card.CardInfoDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.form.CardFormDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.form.FormDTO;
import edu.cbsystematics.com.libraryprojectcbs.exception.BookNotFoundException;
import edu.cbsystematics.com.libraryprojectcbs.exception.CardNotFoundException;
import edu.cbsystematics.com.libraryprojectcbs.mail.EmailSendReminderService;
import edu.cbsystematics.com.libraryprojectcbs.mail.EmailService;
import edu.cbsystematics.com.libraryprojectcbs.mail.Mail;
import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.Card;
import edu.cbsystematics.com.libraryprojectcbs.models.Form;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.service.CardService;
import edu.cbsystematics.com.libraryprojectcbs.service.FormService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.*;


@Controller
@RequestMapping(CARD_HOME_URL)
public class CardController {

    private final CardService cardService;

    private final FormService formService;

    private final UserService userService;

    private final EmailService emailService;

    private final EmailSendReminderService emailSendReminderService;

    private final UserAuthenticationUtils userAuthenticationUtils;

    @Autowired
    public CardController(CardService cardService, FormService formService, UserService userService, EmailService emailService, EmailSendReminderService emailSendReminderService, UserAuthenticationUtils userAuthenticationUtils) {
        this.cardService = cardService;
        this.formService = formService;
        this.userService = userService;
        this.emailService = emailService;
        this.emailSendReminderService = emailSendReminderService;
        this.userAuthenticationUtils = userAuthenticationUtils;
    }


    @Secured(ROLE_LIBRARIAN)
    @GetMapping("/cards-list")
    public String getAllCards(Model model) {
        List<CardInfoDTO> cardInfoDTOList = cardService.findAllCardInfo();
        model.addAttribute("cards", cardInfoDTOList);
        return "card/cards-list";
    }

    @Secured(ROLE_LIBRARIAN)
    @GetMapping("/approve-cards")
    public String getPendingApprovalCards(Authentication authentication, Model model) {
        String username = userAuthenticationUtils.getCurrentUsername(authentication);
        // Extract user information
        User librarian = userService.findByEmail(username).orElse(null);
        String fullName = (librarian != null) ? librarian.getFirstName() + " " + librarian.getLastName() : "ANONYMOUS";
        List<CardDTO> pendingApprovalCards = cardService.getPendingApprovalCards();

        model.addAttribute("fullName", fullName);
        model.addAttribute("librarianId", Objects.requireNonNull(librarian).getId());
        model.addAttribute("approvalCards", pendingApprovalCards);
        model.addAttribute("cardApproveSize", pendingApprovalCards.size());
        return "card/approve-cards";
    }

    @Secured(ROLE_LIBRARIAN)
    @PostMapping("/approve-card/{cardId}")
    public String approveCard(@PathVariable Long cardId, @RequestParam Long librarianId, RedirectAttributes redirectAttributes) {
        Optional<Card> cardOptional = cardService.getCardById(cardId);
        if (cardOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Card not found");
            return "redirect:/library/cards/approve-cards";
        }

        Card card = cardOptional.get();
        cardService.approveBooks(card.getId(), librarianId);

        String message = MessageFormat.format("Book {0} was approved", card.getBook().getBookName());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/library/cards/approve-cards";
    }
    @Secured(ROLE_LIBRARIAN)
    @PostMapping("/cancel-approve-card/{cardId}")
    public String cancelApproveCard(@PathVariable Long cardId, @RequestParam Long librarianId, RedirectAttributes redirectAttributes) {
        Optional<Card> cardOptional = cardService.getCardById(cardId);
        if (cardOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Card not found");
            return "redirect:/library/cards/approve-cards";
        }

        Card card = cardOptional.get();
        cardService.cancelApproveBooks(card.getId(), librarianId);

        String message = MessageFormat.format("Book {0} was canceled", card.getBook().getBookName());
        redirectAttributes.addFlashAttribute("errorMessage", message);
        return "redirect:/library/cards/approve-cards";
    }

    @Secured(ROLE_READER)
    @GetMapping("/user-cards")
    public String showFormsForUser(Authentication authentication, Model model) {
        String username = userAuthenticationUtils.getCurrentUsername(authentication);
        // Extract user information
        User user = userService.findByEmail(username).orElse(null);
        String fullName = (user != null) ? user.getFirstName() + " " + user.getLastName() : "ANONYMOUS";
        List<CardFormDTO> userCards = new ArrayList<>(cardService.getFormsForUser(user));
        userCards.sort(Comparator.comparing(CardFormDTO::getId).reversed());

        model.addAttribute("fullName", fullName);
        model.addAttribute("userId", Objects.requireNonNull(user).getId());
        model.addAttribute("userCards", userCards);
        return "card/user-cards";
    }

    @Secured(ROLE_READER)
    @PostMapping("/return-book")
    public String returnBook(@RequestParam("formId") Long formId, RedirectAttributes redirectAttributes) {
        Optional<Form> formOptional = formService.getFormById(formId);
        if (formOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Form not found");
            return "redirect:/library/cards/user-cards";
        }

        Form form = formOptional.get();
        String bookName = form.getCard().getBook().getBookName();
        long days = ChronoUnit.DAYS.between(form.getStartDate(), LocalDate.now());

        formService.returnBook(form.getId());

        String message = MessageFormat.format("Book {0} was returned after {1} days", bookName, days);
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/library/cards/user-cards";

    }

    @Secured(ROLE_LIBRARIAN)
    @GetMapping("/form-approve-return")
    public String getFormDetails(Authentication authentication, Model model) {
        String username = userAuthenticationUtils.getCurrentUsername(authentication);
        // Extract user information
        User user = userService.findByEmail(username).orElse(null);
        String fullName = (user != null) ? user.getFirstName() + " " + user.getLastName() : "ANONYMOUS";
        List<FormDTO> formReturnDetails = cardService.getFormDetails();

        model.addAttribute("librarianId", Objects.requireNonNull(user).getId());
        model.addAttribute("fullName", fullName);
        model.addAttribute("formReturnDetails", formReturnDetails);
        model.addAttribute("formReturnSize", formReturnDetails.size());
        return "card/form-approve-return";
    }


    @Secured(ROLE_LIBRARIAN)
    @PostMapping("/form-approve-return/{formId}")
    public String approveReturn(@PathVariable Long formId, @RequestParam Long librarianId, RedirectAttributes redirectAttributes) {
        Optional<Form> formOptional = formService.getFormById(formId);
        if (formOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Form not found");
            return "redirect:/library/cards/form-approve-return";
        }

        Form form = formOptional.get();
        String bookName = form.getCard().getBook().getBookName();
        long days = ChronoUnit.DAYS.between(form.getStartDate(), LocalDate.now());
        formService.approveReturn(form.getId(), librarianId);

        String message = MessageFormat.format("Book {0} was returned after {1} days", bookName, days);
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/library/cards/form-approve-return";
    }

    @Secured(ROLE_LIBRARIAN)
    @PostMapping("/send-reminder")
    public String sendReminderEmail(@RequestParam("cardId") Long cardId, RedirectAttributes redirectAttributes) {

        // Retrieve the card information from the database
        Optional<Card> cardOptional = cardService.getCardById(cardId);
        if (cardOptional.isEmpty()) {
            // If the card is not found, redirect with an error message
            redirectAttributes.addFlashAttribute("errorMessage", "Card not found with ID: " + cardId);
            return "redirect:/library/cards/cards-list";
        }

        // Extract information from the card
        Card card = cardOptional.get();
        User user = card.getUser();
        Book book = card.getBook();
        Form form = card.getForm();

        // Construct the email content
        String fullName = user.getFirstName() + " " + user.getLastName();
        String email = user.getEmail();
        String ISBN = book.getISBN();
        String bookName = book.getBookName();
        LocalDate startDate = form.getStartDate();
        LocalDate endDate = form.getReturnDate();
        LocalDate currentDate = LocalDate.now();
        Long daysOverdue = ChronoUnit.DAYS.between(currentDate, endDate);

        // Build an email with the reminder details
        Mail mail = emailSendReminderService.buildReminderEmail(fullName, email, ISBN, bookName, startDate, endDate, daysOverdue);

        // Send the email to the user
        emailService.sendEmail(mail);

        // Set a success message and redirect to the card list page
        String message = MessageFormat.format("Reminder email sent successfully! Reader: {0}, Email: {1}", fullName, email);
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/library/cards/cards-list";
    }


    @GetMapping("/cards-list/{id}/delete")
    public String deleteCard(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Retrieve the user by ID.
            Optional<Card> cardOptional = cardService.getCardById(id);
            if (cardOptional.isPresent()) {
                Card card = cardOptional.get();
                Long cardID = card.getId();
                String bookName = card.getBook().getBookName();
                cardService.deleteCard(id);
                redirectAttributes.addAttribute("successMessage", "Card with ID='" + cardID + "' for the book '" + bookName + "' successfully deleted.");
            }
        } catch (CardNotFoundException | BookNotFoundException e) {
            redirectAttributes.addAttribute("errorMessage", e.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addAttribute("errorMessage", "An error occurred! " + ex.getMessage());
        }

        return "redirect:/library/cards/cards-list";
    }

}