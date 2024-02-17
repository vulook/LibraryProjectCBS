package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.models.Card;
import edu.cbsystematics.com.libraryprojectcbs.models.User;

import java.util.List;
import java.util.Optional;

public interface CardService {

    // Create a new cart entry
    Card createCard(Card card);

    // Update cart information by its ID
    void updateCard(Long id, User userByUserId, Book bookByBookId);

    // Delete a cart entry by its ID
    void deleteCard(Long id);

    // Retrieve a cart entry by its ID if it exists
    Optional<Card> getCardById(Long id);

    // Retrieve a list of all cart entries
    List<Card> getAllCards();

}