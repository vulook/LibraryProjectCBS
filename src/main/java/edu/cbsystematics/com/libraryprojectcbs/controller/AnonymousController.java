package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.dto.author.AuthorDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.book.BookInfoDTO;
import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.service.AuthorService;
import edu.cbsystematics.com.libraryprojectcbs.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.MessageFormat;
import java.util.*;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.*;


@Controller
@PreAuthorize("permitAll()")
@RequestMapping(ANONYMOUS_HOME_URL)
public class AnonymousController {

    private final BookService bookService;

    private final AuthorService authorService;

    @Autowired
    public AnonymousController(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }


    /*----------------------------------------------------------------*/
    // List to store books selected by the user
    private final List<Book> selectedBooksAnonymous = new ArrayList<>();

    // Search query
    private String querySearch;
    /*----------------------------------------------------------------*/


    @GetMapping("/author-search")
    public String searchAuthors(HttpServletRequest request, Model model) {
        querySearch = request.getParameter("query");
        List<AuthorDTO> authors = authorService.searchAuthorsWithBookCount(querySearch);

        model.addAttribute("authors", authors);
        return "anonymous/author-search";
    }


    @GetMapping("/author/{authorId}/books")
    public String viewAuthorBooks(@PathVariable Long authorId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Author> authorOptional = authorService.getAuthorById(authorId);
        if (authorOptional.isPresent()) {
            Author author = authorOptional.get();
            Set<Book> authorBooks = author.getBooks();

            model.addAttribute("author", author);
            model.addAttribute("authorBooks", authorBooks);
        } else {
            String message = "The Author not found";
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/library/anonymous/author-search";
        }

        return "anonymous/author-search-book-results";
    }


    @GetMapping("/author/{authorId}/{bookId}/{isSelected}")
    public String selectBookByAuthorGet(@PathVariable("authorId") Long authorId, @PathVariable("bookId") Long bookId, @PathVariable("isSelected") Boolean isSelected,
                                        RedirectAttributes redirectAttributes) {

        updateBookSelection(bookId, isSelected, redirectAttributes, selectedBooksAnonymous);
        return "redirect:/library/anonymous/author/" + authorId + "/books";
    }


    @GetMapping("/books-selected")
    public String getSelectedBooks(Model model) {

        // Map the selectedBooks list to a list of BookDTO objects
        List<BookInfoDTO> selectedBook = selectedBooksAnonymous.stream()
                .map(BookInfoDTO::new)
                .toList();

        if (!selectedBook.isEmpty()) {
            model.addAttribute("bookSize", selectedBook.size());
        } else {
            model.addAttribute("bookSize", 0);
        }

        // Add the list of selected books to the model
        model.addAttribute("selectedBooks", selectedBook);
        return "anonymous/books-selected";
    }


    @GetMapping("/alert-header-author/{authorId}")
    public String alertHeaderAuthor(@PathVariable("authorId") Long authorId, @ModelAttribute("selectedBooksAnonymous") List<Book> selectedBooksAnonymous, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("bookSize", selectedBooksAnonymous.size());
        return "redirect:/library/anonymous/author/" + authorId + "/books";
    }


    @PostMapping("/books-selected")
    public String selectBookPOST(@RequestParam("selectedBooks") List<Long> selectedBookIds) {
        if (selectedBookIds != null) {
            // Clear selectedBooks list
            selectedBooksAnonymous.forEach(bookSelect -> bookSelect.setSelected(false));
            selectedBooksAnonymous.forEach(bookSelect -> bookService.updateIsSelectedStatus(bookSelect.getId(), bookSelect.isSelected()));
            selectedBooksAnonymous.clear();
        }

        // Redirect to the appropriate URL
        return "redirect:/registration";
    }


    @GetMapping("/book-search")
    public String searchBooks(HttpServletRequest request, Model model) {
        querySearch = request.getParameter("query");
        List<Book> bookFoundList = bookService.searchBooksByBookName(querySearch);

        model.addAttribute("bookFoundList", bookFoundList);
        return "anonymous/book-search";
    }


    @GetMapping("/book-search/{bookId}/{isSelected}")
    public String selectBookGet(@PathVariable("bookId") Long bookId, @PathVariable("isSelected") Boolean isSelected,
                                RedirectAttributes redirectAttributes) {


        updateBookSelection(bookId, isSelected, redirectAttributes, selectedBooksAnonymous);
        return "redirect:/library/anonymous/book-search?query=" + querySearch;
    }


    @GetMapping("/alert-header-book")
    public String alertHeaderBook(RedirectAttributes redirectAttributes, @ModelAttribute("selectedBooksAnonymous") List<Book> selectedBooksAnonymous) {
        redirectAttributes.addFlashAttribute("bookSize", selectedBooksAnonymous.size());
        return "redirect:/library/anonymous/book-search?query=" + querySearch;
    }

    // -------------------------------------------------------------------

    private void updateBookSelection(Long bookId, Boolean isSelected, RedirectAttributes redirectAttributes, List<Book> selectedBooksAnonymous) {
        try {
            // Check if the maximum number of books is selected
            if (selectedBooksAnonymous.size() >= 2) {
                // Get the index of the last element
                int lastIndex = selectedBooksAnonymous.size() - 1;

                // Get the last element
                Book bookSelect = selectedBooksAnonymous.get(lastIndex);

                // Set isSelected to false
                bookSelect.setSelected(false);

                // Update the status in the database
                bookService.updateIsSelectedStatus(bookSelect.getId(), bookSelect.isSelected());

                // Remove the last element from the list
                selectedBooksAnonymous.removeIf(b -> Objects.equals(b.getId(), bookSelect.getId()));

                String message = "You can select only up to 2 books";
                redirectAttributes.addFlashAttribute("errorMessage", message);
                return;
            }

            // Get the book by its ID
            Optional<Book> selectedBookOptional = bookService.getBookById(bookId);
            selectedBookOptional.ifPresent(book -> {
                // Check if the book is available
                if (book.isAvailable()) {
                    // Update the selected status of the book
                    bookService.updateIsSelectedStatus(bookId, isSelected);

                    // Add or remove the book from the selectedBooks
                    if (Boolean.TRUE.equals(isSelected)) {
                        book.setSelected(true);
                        selectedBooksAnonymous.add(book);
                    }

                    if (Boolean.FALSE.equals(isSelected)) {
                        book.setSelected(false);
                        selectedBooksAnonymous.removeIf(b -> Objects.equals(b.getId(), bookId));
                    }

                    // Prepare a success message
                    String status = (Boolean.TRUE.equals(isSelected) ? "selected" : "unselected");
                    String message = MessageFormat.format("The Book ''{0}'' has been {1}", book.getBookName(), status);
                    redirectAttributes.addFlashAttribute("bookSize", selectedBooksAnonymous.size());
                    redirectAttributes.addFlashAttribute("successMessage", message);

                } else {
                    String message = MessageFormat.format("The Book ''{0}'' is currently unavailable", book.getBookName());
                    redirectAttributes.addFlashAttribute("errorMessage", message);
                }
            });

        } catch (Exception ex) {
            // Handle any exceptions
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
    }



}
