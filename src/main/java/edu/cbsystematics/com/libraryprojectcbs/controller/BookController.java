package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.exception.*;
import edu.cbsystematics.com.libraryprojectcbs.models.Book;
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
@RequestMapping("/library/books")
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    @Autowired
    public BookController(BookService bookService, AuthorService authorService) {

        this.bookService = bookService;
        this.authorService = authorService;
    }

    @GetMapping("/list")
    public String displayBooks(Model model) {
        // Retrieve a list of all books
        List<Book> books = bookService.getAllBooks();

        // Add the list of books to the model
        model.addAttribute("books", books);
        return "books/book-list";
    }

    @GetMapping("/create")
    public String showCreateBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "books/book-create";
    }

    @PostMapping("/create")
    public String validateBook(@Valid @ModelAttribute("book") Book book, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Handle validation errors
            ValidationExceptionHandler.handleValidationErrors(result);
            return "books/book-create";
        }

        // Process and save the book
        return createBook(book, redirectAttributes);
    }

    public String createBook(@ModelAttribute Book book, RedirectAttributes redirectAttributes) {
        try {
            bookService.createBook(book);
            redirectAttributes.addFlashAttribute("successMessage", "Book '" + book.getBookName() + "' successfully created.");
            return "redirect:/library/books/success";
        } catch (BookAlreadyExistsException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/library/books/error";
        }
    }

    @GetMapping("/list/{id}/edit")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        // Retrieve the book for the given ID.
        Book book = bookService.getBookById(id).orElse(null);
        // Add Book to the model for rendering the view.
        model.addAttribute("updatedBook", book);
        return "books/book-edit";
    }

    @PostMapping("/list/{id}/edit")
    public String editBook(@PathVariable Long id, @Valid @ModelAttribute("updatedBook") Book updatedBook, BindingResult result, RedirectAttributes redirectAttributes) {
        // Check for validation errors
        if (result.hasErrors()) {
            // Handle validation errors
            ValidationExceptionHandler.handleValidationErrors(result);
            return "books/book-edit";
        }

        // Retrieve the existing book
        Optional<Book> existingBookOptional = bookService.getBookById(id);
        if (existingBookOptional.isPresent()) {
            try {
                // Update book details.
                bookService.updateBook(id, updatedBook);
                // Action attribute.
                redirectAttributes.addAttribute("successMessage", "Book '" + existingBookOptional.get().getBookName() + "' successfully updated.");
                return "redirect:/library/books/success";
            } catch (BookAlreadyExistsException ex) {
                redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
                return "redirect:/library/books/error";
            }
        } else {
            // If the Book is not found, throw BookNotFoundException
            throw new BookNotFoundException("Book not found");
        }
    }

    @GetMapping("/list/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Retrieve the book by ID.
        Optional<Book> existingBookOptional = bookService.getBookById(id);
        if (existingBookOptional.isPresent()) {
            String bookNameD = existingBookOptional.get().getBookName();
            bookService.deleteBook(id);

            // Redirect to the success page with the "deleted" action
            redirectAttributes.addAttribute("successMessage", "Book '" + bookNameD + "' successfully deleted.");
            return "redirect:/library/books/success";
        } else {
            // If the Book is not found, throw BookNotFoundException
            throw new BookNotFoundException("Book not found");
        }
    }

    @GetMapping("/success")
    public String successPage(@ModelAttribute("successMessage") String successMessage, Model model) {
        model.addAttribute("message", successMessage);
        return "books/success-page";
    }

    @ExceptionHandler(BookNotFoundException.class)
    public String handleBookNotFoundException(BookNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "books/error-page";
    }

    @GetMapping("/error")
    public String errorPage(@ModelAttribute("errorMessage") String errorMessage, Model model) {
        if (errorMessage.isEmpty()) {
            errorMessage = "Oops! Something went wrong.";
        }
        model.addAttribute("errorMessage", errorMessage);
        return "books/error-page";
    }

}
