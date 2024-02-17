package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.exception.CardNotFoundException;
import edu.cbsystematics.com.libraryprojectcbs.exception.UserRoleAlreadyExistsException;
import edu.cbsystematics.com.libraryprojectcbs.exception.UserRoleNotFoundException;
import edu.cbsystematics.com.libraryprojectcbs.exception.ValidationExceptionHandler;
import edu.cbsystematics.com.libraryprojectcbs.models.Card;
import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import edu.cbsystematics.com.libraryprojectcbs.service.CardService;
import edu.cbsystematics.com.libraryprojectcbs.service.FormService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/library/cards")
public class CardController {

    private final CardService cardService;
    private final FormService formService;

    @Autowired
    public CardController(CardService cardService, FormService formService) {
        this.cardService = cardService;
        this.formService = formService;
    }

    @GetMapping("/list")
    public String displayCards(Model model) {
        // Retrieve a list of cards
        List<Card> cards = cardService.getAllCards();

        // Add the list of cards to the model
        model.addAttribute("cards", cards);

        // Return the view name for displaying
        return "cards/card-list";
    }

    @GetMapping("/list/{id}")
    public String showCardDetails(@PathVariable Long id, Model model) {
        // Retrieve the card details by ID
        Optional<Card> cardOptional = cardService.getCardById(id);

        // Check if the card exists
        if (cardOptional.isPresent()) {
            Card card = cardOptional.get();
            model.addAttribute("card", card);
            return "cards/card-details"; // Return the view for displaying card details
        } else {
            // If the card is not found, CardNotFoundException
            throw new CardNotFoundException("Card not found for ID: " + id);
        }
    }

    @GetMapping("/create")
    public String showCreateCardsForm(Model model) {
        model.addAttribute("card", new Card());
        return "cards/card-create";
    }

/*    @PostMapping("/create")
    public String validateCard(@Valid @ModelAttribute("card") Card card, BindingResult result) {
        if (result.hasErrors()) {
            // Handle validation errors
            ValidationExceptionHandler.handleValidationErrors(result);
            return "cards/rcard-create";
        }

        // Process and save the card
        return createCard(card);
    }*/

/*    public String createCard(@ModelAttribute Card card) {
        try {
            cardService.createCard(card);
        } catch (CardNotFoundException ex) {

        }
    }

    @GetMapping("/list/{id}/edit")
    public String showEditCardForm(@PathVariable Long id, Model model) {
        // Retrieve the cards for the given ID.
        Card card = cardService.getCardById(id).orElse(null);
        // Add card to the model for rendering the view.
        model.addAttribute("updatedCard", card);
        return "cards/card-edit";
    }

    @PostMapping("/list/{id}/edit")
    public String editCard(@PathVariable Long id, @Valid @ModelAttribute("updatedCard") Card updatedCard, BindingResult result, RedirectAttributes redirectAttributes) {
        // Check for validation errors
        if (result.hasErrors()) {
            // Handle validation errors
            ValidationExceptionHandler.handleValidationErrors(result);
            return "cards/card-edit";
        }

        // Retrieve the existing card
        Optional<Card> existingCardOptional = cardService.getCardById(id);
        if (existingCardOptional.isPresent()) {

                // Update card details.
                cardService.updateCard(id, updatedCard);
                // Action attribute.
                redirectAttributes.addAttribute("successMessage", "Role '" + existingRoleOptional.get().getRoleName() + "' successfully updated.");
                return "redirect:/library/roles/success";

        } else {
            // If the card is not found, throw CardNotFoundException
            throw new CardNotFoundException("Card not found");
        }
    }

    @GetMapping("/list/{id}/delete")
    public String deleteUserRole(@PathVariable Long id) {
        // Retrieve the card by ID.
        Optional<Card> existingCardOptional = cardService.getCardById(id);
        if (existingCardOptional.isPresent()) {
            cardService.deleteCard(id);
        } else {
            // If the card is not found, throw CardNotFoundException
            throw new CardNotFoundException("Card not found");
        }
    }*/


}

