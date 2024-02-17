package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.Author;

import java.util.List;
import java.util.Optional;


public interface AuthorService {

    // Create a new Author.
    void createAuthor(Author author);

    // Create a new Authors.
    void saveAll(List<Author> authors);

    // Update an existing Author by ID.
    void updateAuthor(Long id, Author updatedAuthor);

    // Delete an Author by ID.
    void deleteAuthor(Long id);

    // Retrieve an Author by their ID.
    Optional<Author> getAuthorById(Long id);

    // Retrieve all Authors.
    List<Author> getAllAuthors();

    // Search for Authors by first or last name.
    List<Author> searchAuthorsByFullName(String query);

    // Retrieve an Author's ID by their first name and last name.
    Long getAuthorIdByFullName(String firstName, String lastName);

}