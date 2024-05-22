package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.dto.author.AuthorDTO;
import edu.cbsystematics.com.libraryprojectcbs.models.Author;
import edu.cbsystematics.com.libraryprojectcbs.models.Book;
import edu.cbsystematics.com.libraryprojectcbs.service.AuthorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.AUTHOR_HOME_URL;


@Controller
@RequestMapping(AUTHOR_HOME_URL)
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search-authors")
    public String searchAuthors(HttpServletRequest request, Model model) {
        String query = request.getParameter("query");
        List<AuthorDTO> authors = authorService.searchAuthorsWithBookCount(query);

        model.addAttribute("authors", authors);
        return "author/search-authors";
    }

    @PreAuthorize("isAuthenticated()")
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
            return "redirect:/library/authors/search-authors";
        }

        return "author/book-search-results";
    }








}
