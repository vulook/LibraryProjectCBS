package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.aop.Loggable;
import edu.cbsystematics.com.libraryprojectcbs.config.security.UserAuthenticationUtils;
import edu.cbsystematics.com.libraryprojectcbs.dto.author.AuthorBookDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.book.BookDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.book.BookInfoDTO;
import edu.cbsystematics.com.libraryprojectcbs.dto.book.BookMapper;
import edu.cbsystematics.com.libraryprojectcbs.exception.*;
import edu.cbsystematics.com.libraryprojectcbs.models.*;
import edu.cbsystematics.com.libraryprojectcbs.service.BookService;
import edu.cbsystematics.com.libraryprojectcbs.service.CardService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import edu.cbsystematics.com.libraryprojectcbs.utils.PaginationConstants;
import edu.cbsystematics.com.libraryprojectcbs.utils.role.RoleUtilsForStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.*;


@Controller
@RequestMapping(BOOK_HOME_URL)
public class BookController {

    private final BookService bookService;

    private final UserService userService;

    private final CardService cardService;

    private final UserAuthenticationUtils userAuthenticationUtils;

    @Autowired
    public BookController(BookService bookService, UserService userService, CardService cardService, UserAuthenticationUtils userAuthenticationUtils) {
        this.bookService = bookService;
        this.userService = userService;
        this.cardService = cardService;
        this.userAuthenticationUtils = userAuthenticationUtils;
    }


    /*----------------------------------------------------------------*/
    // List to store books selected by the user
    private final List<Book> selectedBooks = new ArrayList<>();

    // Search query
    private String queryBook;
    /*----------------------------------------------------------------*/


    @GetMapping("/")
    public String viewPaginationPage(Authentication authentication, Model model) {

        // Redirect to the bookPaginated method with default parameters
        return bookPaginated(PaginationConstants.DEFAULT_PAGE_NUMBER, PaginationConstants.DEFAULT_FIELD, PaginationConstants.DEFAULT_SORT_DIRECTION, authentication, model);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/page/{pageNo}")
    public String bookPaginated(@PathVariable(value = "pageNo") Integer pageNo,
                                @RequestParam(value = "sortField", required = false) String sortField,
                                @RequestParam(value = "sortDir", required = false) String sortDir,
                                Authentication authentication, Model model) {

        // Get current user's username and roles
        String username = userAuthenticationUtils.getCurrentUsername(authentication);
        String roles = userAuthenticationUtils.getCurrentUserRoles(authentication);

        // Extract user information
        User user = userService.findByEmail(username).orElse(null);
        String fullName = (user != null) ? user.getFirstName() + " " + user.getLastName() : "ANONYMOUS";
        String role = (roles != null) ? RoleUtilsForStatus.getRoleLabel(roles) : "anonymous";

        // Set the default page size
        Integer pageSize = PaginationConstants.DEFAULT_PAGE_SIZE;

        // Retrieve paginated books for the user
        Page<BookInfoDTO> page = bookService.findPaginated(user, pageNo, pageSize, sortField, sortDir);
        List<BookInfoDTO> books = page.getContent();

        // Add pagination attributes to the model
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("ASC") ? "DESC" : "ASC");
        model.addAttribute("page", page);

        int totalPages = page.getTotalPages();

        // Add page numbers for navigation if total pages are greater than 0
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }

        // Add information
        model.addAttribute("fullName", fullName);
        model.addAttribute("role", role);
        model.addAttribute("books", books);
        model.addAttribute("bookSize", selectedBooks.size());
        return "book/books-list";
    }


    @GetMapping("/add-author-book")
    public String showCreateForm(Authentication authentication, Model model) {
        String roles = userAuthenticationUtils.getCurrentUserRoles(authentication);
        String role = (roles != null) ? RoleUtilsForStatus.getRoleLabel(roles) : "anonymous";
        BookDTO bookDTO = new BookDTO();                    // Create a new BookDTO object
        bookDTO.setAuthors(new HashSet<>());                // Initialize an empty Set for authors
        AuthorBookDTO authorBookDTO = new AuthorBookDTO();  // Create a new AuthorBookDTO object
        bookDTO.getAuthors().add(authorBookDTO);            // Add the authorBookDTO to the authors set in the bookDTO

        model.addAttribute("book", bookDTO);
        model.addAttribute("genres", GenreType.values());
        model.addAttribute("role", role);
        return "book/add-author-book";
    }

    @Loggable(value = ActionType.CREATE)
    @PostMapping("/add-author-book")
    public String processCreateBook(@ModelAttribute("book") @Valid BookDTO bookDTO, BindingResult result,
                                    @RequestParam("authors") String authors, RedirectAttributes redirectAttributes, Model model) {

        Set<AuthorBookDTO> authorSet;

        // Check if the ISBN is already in use
        if (bookService.checkIfBookExistsByISBN(bookDTO.getISBN())) {
            result.rejectValue("ISBN", "ISBN.exist", "This ISBN has already in use by another book");
        }

        if (result.hasErrors()) {
            // Handle validation errors
            ValidationExceptionHandler.handleValidationErrors(result);
            model.addAttribute("genres", GenreType.values());    // Add the GenreType to the model
            return "book/add-author-book";
        }


        // Validate authors list
        if (authors == null || authors.isEmpty()) {
            String message = "The Authors not found";
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/library/books/add-author-book";

        } else {
            // Convert the string of authors to a set of AuthorBookDTO
            authorSet = Arrays.stream(authors.split(","))
                    .map(authorName -> {
                        String[] names = authorName.trim().split(" ");
                        if (names.length > 1) {
                            return new AuthorBookDTO(names[0], names[1]);
                        } else {
                            System.out.println("Invalid author format: " + authorName);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Authors print
            int index = 1;
            for (AuthorBookDTO author : authorSet) {
                System.out.println("Author" + index + ": " + author.getFirstName() + " " + author.getLastName());
                index++;
            }
        }


        try {
            // Set the authors in the BookDTO
            bookDTO.setAuthors(authorSet);

            Book bookSave = bookService.addAuthorAndBook(BookMapper.toEntity(bookDTO));

            String authorsString = bookSave.getAuthors().stream()
                    .map(author -> author.getFirstName() + " " + author.getLastName())
                    .collect(Collectors.joining(", "));
            String message = MessageFormat.format("A Book: [{0}] && Authors: [{1}] added successfully!", bookSave.getBookName(), authorsString);
            redirectAttributes.addFlashAttribute("successMessage", message);
            return "redirect:/library/books/add-author-book";

        } catch (Exception ex) {
            String message = "Error saving book. " + ex.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/library/books/add-author-book";
        }
    }


    @GetMapping("/search-books")
    public String searchBooks(HttpServletRequest request, Model model) {
        queryBook = request.getParameter("query");
        List<Book> bookFoundList = bookService.searchBooksByBookName(queryBook);

        model.addAttribute("bookFoundList", bookFoundList);
        return "book/search-books";
    }

    @Loggable(value = ActionType.TAKE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{bookId}/{isSelected}")
    public String selectBookGET(@PathVariable("bookId") Long bookId, @PathVariable("isSelected") Boolean isSelected,
                                RedirectAttributes redirectAttributes) {

        updateBookSelection(bookId, isSelected, redirectAttributes, selectedBooks);
        return "redirect:/library/books/";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search-books/{bookId}/{isSelected}")
    public String selectBookBySearchGet(@PathVariable("bookId") Long bookId, @PathVariable("isSelected") Boolean isSelected,
                                RedirectAttributes redirectAttributes) {

        updateBookSelection(bookId, isSelected, redirectAttributes, selectedBooks);
        return "redirect:/library/books/search-books?query=" + queryBook;
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/author/{authorId}/{bookId}/{isSelected}")
    public String selectBookByAuthorGet(@PathVariable("authorId") Long authorId, @PathVariable("bookId") Long bookId, @PathVariable("isSelected") Boolean isSelected,
                                        RedirectAttributes redirectAttributes) {

        updateBookSelection(bookId, isSelected, redirectAttributes, selectedBooks);
        return "redirect:/library/authors/author/" + authorId + "/books";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/books-selected")
    public String getSelectedBooks(Model model) {

        // Map the selectedBooks list to a list of BookDTO objects
        List<BookInfoDTO> selectedBook = selectedBooks.stream()
                .map(BookInfoDTO::new)
                .toList();

        if (!selectedBook.isEmpty()) {
            model.addAttribute("bookSize", selectedBook.size());
        } else {
            model.addAttribute("bookSize", 0);
        }

        // Add the list of selected books to the model
        model.addAttribute("selectedBooks", selectedBook);
        return "book/books-selected";
    }


    @GetMapping("/alert-header")
    public String alertHeaderBook(RedirectAttributes redirectAttributes, @ModelAttribute("selectedBooks") List<Book> selectedBooks) {
        redirectAttributes.addFlashAttribute("bookSize", selectedBooks.size());
        return "redirect:/library/books/search-books?query=" + queryBook;
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/books-selected")
    public String selectBookPOST(@RequestParam("selectedBooks") List<Long> selectedBookIds,
                                 Authentication authentication, RedirectAttributes redirectAttributes, Model model) {

        // Get current user's username and roles
        String username = userAuthenticationUtils.getCurrentUsername(authentication);
        User user = userService.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        String roles = userAuthenticationUtils.getCurrentUserRoles(authentication);
        String role = (roles != null) ? RoleUtilsForStatus.getRoleLabel(roles) : "anonymous";

        for (Long bookId : selectedBookIds) {
            // Retrieve the book by bookId
            Book book = bookService.getBookById(bookId)
                    .orElseThrow(() -> new BookNotFoundException("Book not found" + bookId));

            // Create a new card for the user and the book
            Card card = new Card(user, book);
            cardService.selectBook(card);

            // Remove the selected book from the selectedBooks list
            selectedBooks.removeIf(b -> Objects.equals(b.getId(), bookId));

            String message = MessageFormat.format("A book {0} is awaiting approval!", book.getBookName());
            model.addAttribute("role", role);
            redirectAttributes.addFlashAttribute("successMessage", message);
        }

        // Redirect to the appropriate URL
        if (!selectedBooks.isEmpty()) {
            return "redirect:/library/books/books-selected";
        } else {
            return "redirect:/library/books/";
        }
    }

    //------------------------------------------------------------------------

    private void updateBookSelection(Long bookId, Boolean isSelected, RedirectAttributes redirectAttributes, List<Book> selectedBooks) {
        try {
            // Check if the maximum number of books has been selected
            if (selectedBooks.size() >= 4 && Boolean.TRUE.equals(isSelected)) {

                // Get the index of the last element
                int lastIndex = selectedBooks.size() - 1;

                // Get the last element
                Book bookSelect = selectedBooks.get(lastIndex);

                // Set isSelected to false
                bookSelect.setSelected(false);

                // Update the status in the database
                bookService.updateIsSelectedStatus(bookSelect.getId(), bookSelect.isSelected());

                // Remove the last element from the list
                selectedBooks.removeIf(b -> Objects.equals(b.getId(), bookSelect.getId()));

                String message = "You can select only up to 4 books";
                redirectAttributes.addFlashAttribute("errorMessage", message);
                return;
            }

            // Get the book by ID
            Optional<Book> selectedBookOptional = bookService.getBookById(bookId);
            selectedBookOptional.ifPresent(book -> {
                if (book.isAvailable()) {
                    // Update the selected status of the book
                    bookService.updateIsSelectedStatus(bookId, isSelected);

                    // Add or remove the book from the selectedBooks
                    if (Boolean.TRUE.equals(isSelected)) {
                        book.setSelected(true);
                        selectedBooks.add(book);
                    } else {
                        book.setSelected(false);
                        selectedBooks.removeIf(b -> Objects.equals(b.getId(), bookId));
                    }

                    // Prepare a message (select/unselect)
                    String status = (Boolean.TRUE.equals(isSelected) ? "selected" : "unselected");
                    String message = MessageFormat.format("The Book ''{0}'' has been {1}", book.getBookName(), status);
                    redirectAttributes.addFlashAttribute("bookSize", selectedBooks.size());
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

