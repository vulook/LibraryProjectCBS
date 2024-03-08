package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.exception.*;
import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import edu.cbsystematics.com.libraryprojectcbs.service.AuthorService;
import edu.cbsystematics.com.libraryprojectcbs.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/library/authors")
public class AuthorController {

    private final AuthorService authorService;

    private final BookService bookService;

    @Autowired
    public AuthorController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @GetMapping("/list")
    public String displayAuthors(Model model) {
        // Retrieve a list of all authors
        List<Author> authors = authorService.getAllAuthors();
        model.addAttribute("authors", authors);
        return "authors/author-list";
    }

    @GetMapping("/create")
    public String showCreateAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "authors/author-create";
    }

    @PostMapping("/create")
    public String validateAuthor(@Valid @ModelAttribute("author") Author author, BindingResult result, RedirectAttributes redirectAttributes) {
        // Check for validation errors
        if (result.hasErrors()) {
            ValidationExceptionHandler.handleValidationErrors(result);
            return "authors/author-create";
        }

        // Process and save the author
        return createAuthor(author, redirectAttributes);
    }

    public String createAuthor(@ModelAttribute Author author, RedirectAttributes redirectAttributes) {
            authorService.createAuthor(author);
            redirectAttributes.addAttribute("successMessage", "Author '" + author.getFirstName() + " " + author.getLastName() + "' successfully created.");
            return "redirect:/library/authors/success";

    }

    @GetMapping("/list/{id}/edit")
    public String showEditAuthorForm(@PathVariable Long id, Model model) {
        // Retrieve the author for the given ID.
        Author author = authorService.getAuthorById(id).orElse(null);
        model.addAttribute("updatedAuthor", author);
        return "authors/author-edit";
    }

    @PostMapping("/list/{id}/edit")
    public String editAuthor(@PathVariable Long id, @Valid @ModelAttribute("updatedAuthor") Author updatedAuthor, BindingResult result, RedirectAttributes redirectAttributes) {
        // Check for validation errors
        if (result.hasErrors()) {
            ValidationExceptionHandler.handleValidationErrors(result);
            return "authors/author-edit";
        }

        // Retrieve the existing author
        Optional<Author> existingAuthorOptional = authorService.getAuthorById(id);
        if (existingAuthorOptional.isPresent()) {
            Author author = existingAuthorOptional.get();
            try {
                // Update author details.
                authorService.updateAuthor(id, updatedAuthor);
                // Action attribute.
                redirectAttributes.addAttribute("successMessage", "Author '" + author.getFirstName() + " " + author.getLastName() + "' successfully updated.");
                return "redirect:/library/authors/success";
            } catch (AuthorAlreadyExistsException ex) {
                redirectAttributes.addAttribute("errorMessage", ex.getMessage());
                return "redirect:/library/authors/error";
            }
        } else {
            throw new AuthorNotFoundException("Author not found");
        }
    }

    @GetMapping("/list/{id}/delete")
    public String deleteAuthor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Retrieve the Author by ID.
        Optional<Author> existingAuthorOptional = authorService.getAuthorById(id);
        if (existingAuthorOptional.isPresent()) {
            Author author = existingAuthorOptional.get();
            String firstNameD = author.getFirstName();
            String lastNameD = author.getLastName();
            authorService.deleteAuthor(id);

            redirectAttributes.addAttribute("successMessage", "Author '" + firstNameD + " " + lastNameD + "' successfully deleted.");
            return "redirect:/library/authors/success";
        } else {
            throw new AuthorNotFoundException("Author not found");
        }
    }

    @GetMapping("/success")
    public String successPage(@ModelAttribute("successMessage") String successMessage, Model model) {
        model.addAttribute("message", successMessage);
        return "authors/success-page";
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public String handleAuthorNotFoundException(AuthorNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "authors/error-page";
    }

    @GetMapping("/error")
    public String errorPage(@ModelAttribute("errorMessage") String errorMessage, Model model) {
        if (errorMessage.isEmpty()) {
            errorMessage = "Oops! Something went wrong.";
        }
        model.addAttribute("errorMessage", errorMessage);
        return "authors/error-page";
    }

}
