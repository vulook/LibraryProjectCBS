package edu.cbsystematics.com.libraryprojectcbs.config;

import edu.cbsystematics.com.libraryprojectcbs.models.*;
import edu.cbsystematics.com.libraryprojectcbs.service.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.*;


@Component
public class DatabaseInitializer {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final BookService bookService;
    private final FormService formService;
    private final CardService cardService;


    @Autowired
    public DatabaseInitializer(UserService userService, UserRoleService userRoleService, BookService bookService, FormService formService, CardService cardService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.bookService = bookService;
        this.formService = formService;
        this.cardService = cardService;
    }

    @PostConstruct
    public void init() {

        // Creating and Saving the roles in the database
        UserRole adminRole = new UserRole(ROLE_ADMIN, "Administrator role with full access and control over the system");
        UserRole librarianRole = new UserRole(ROLE_LIBRARIAN, "Librarian role responsible for managing and organizing library resources");
        UserRole readerRole = new UserRole(ROLE_READER, "Reader role with access to browse and borrow library resources");

        userRoleService.createRole(adminRole);
        userRoleService.createRole(librarianRole);
        userRoleService.createRole(readerRole);

        // Creating and Saving users in the database
        User admin = new User("Ben", "Smith", LocalDate.of(1982, 1, 15), "+38 (066) 860-92-21", "admin@example.com", "123", LocalDate.of(2015, 10, 2), adminRole);
        User librarian = new User("Clara", "Brown", LocalDate.of(1992, 5, 5), "+38 (097) 311-02-00", "librarian@example.com", "123", LocalDate.of(2015, 10, 20), librarianRole);
        User reader1 = new User("Jane", "Doe", LocalDate.of(2005, 3, 15), "+38 (098) 962-22-12", "reader1@example.com", "123", LocalDate.of(2023, 5, 15), readerRole);
        User reader2 = new User("Elizabeth", "Roberts", LocalDate.of(2008, 7, 20), "+38 (095) 110-11-10", "reader2@example.com", "123", LocalDate.of(2019, 6, 15), readerRole);
        User reader3 = new User("Liam", "Wang", LocalDate.of(2002, 12, 10), "+38 (066) 545-55-15", "reader3@example.com", "123", LocalDate.of(2022, 10, 15), readerRole);

        userService.createUserDatabaseInit(admin);
        userService.createUserDatabaseInit(librarian);
        userService.createUserDatabaseInit(reader1);
        userService.createUserDatabaseInit(reader2);
        userService.createUserDatabaseInit(reader3);

        // Creating authors
        Author author1 = new Author("George", "Orwell");
        Author author2 = new Author("J.D.", "Salinger");
        Author author3 = new Author("Harper", "Lee");
        Author author4 = new Author("J.R.R.", "Tolkien");
        Author author5 = new Author("Douglas", "Adams");
        Author author6 = new Author("Dan", "Brown");
        Author author7 = new Author("Suzanne", "Collins");
        Author author8 = new Author("J.K.", "Rowling");
        Author author9 = new Author("Stieg", "Larson");
        Author author10 = new Author("John", "Green");
        Author author11 = new Author("Andy", "Weir");
        Author author12 = new Author("Paulo", "Coelho");
        Author author13 = new Author("Rhonda", "Byrne");
        Author author14 = new Author("Timothy", "Ferries");
        Author author15 = new Author("Eric", "Ries");
        Author author16 = new Author("Eckhart", "Tolle");
        Author author17 = new Author("Christopher", "Gerber");
        Author author18 = new Author("Kristin", "Neff");
        Author author19 = new Author("Samuel", "Ratchett");
        Author author20 = new Author("John", "Lloyd");
        Author author21 = new Author("Stephen", "Fry");
        Author author22 = new Author("Mikael", "Blomqvist");
        Author author23 = new Author("Christopher", "Tolkien");
        Author author24 = new Author("Wayne G.", "Hammond");
        Author author25 = new Author("Stephen", "King");
        Author author26 = new Author("Richard", "Matheson");
        Author author27 = new Author("Bill", "Friedrichs");
        Author author28 = new Author("George R.R.", "Martin");
        Author author29 = new Author("Elio", "Garcia");
        Author author30 = new Author("Linda", "Antonsson");
        Author author31 = new Author("Luigi", "Serafini");
        Author author32 = new Author("Piero", "Angela");
        Author author33 = new Author("Roberto", "Vacca");
        Author author34 = new Author("Buffalo", "Bill");
        Author author35 = new Author("Michael", "Baigent");
        Author author36 = new Author("Richard", "Leigh");
        Author author37 = new Author("Henry", "Lincoln");
        Author author38 = new Author("Miles", "Archer");
        Author author39 = new Author("Alexander", "Hamilton");
        Author author40 = new Author("James", "Madison");
        Author author41 = new Author("Zane", "Julius");

        // Creating books
        Book book1 = new Book("1984", Genre.FICTION, 328, 5, 4.5);
        Book book2 = new Book("The Catcher in the Rye", Genre.FICTION, 224, 5, 4.0);
        Book book3 = new Book("To Kill a Mockingbird", Genre.FICTION, 336, 4, 4.8);
        Book book4 = new Book("The Lord of the Rings", Genre.FANTASY, 1178, 2, 4.5);
        Book book5 = new Book("The Hitchhiker's Guide to the Galaxy", Genre.SCIENCE_FICTION, 224, 4, 4.0);
        Book book6 = new Book("The Da Vinci Code", Genre.MYSTERY, 454, 2, 4.5);
        Book book7 = new Book("The Hunger Games", Genre.SCIENCE_FICTION, 374, 3, 4.2);
        Book book8 = new Book("Harry Potter and the Sorcerer's Stone", Genre.FANTASY, 321, 3, 4.7);
        Book book9 = new Book("The Girl with the Dragon Tattoo", Genre.MYSTERY, 712, 2, 4.4);
        Book book10 = new Book("The Fault in Our Stars", Genre.PUBLICATION, 313, 5, 4.3);
        Book book11 = new Book("The Martian", Genre.SCIENCE_FICTION, 390, 1, 4.4);
        Book book12 = new Book("The Alchemist", Genre.FICTION, 202, 2, 4.0);
        Book book13 = new Book("The Secret", Genre.SELF_HELP, 178, 1, 3.7);
        Book book14 = new Book("The 4-Hour Workweek", Genre.SELF_HELP, 448, 2, 4.1);
        Book book15 = new Book("The Lean Startup", Genre.PUBLICATION, 330, 2, 4.2);
        Book book16 = new Book("The Power of Now", Genre.BIOGRAPHY, 241, 2, 4.1);
        Book book17 = new Book("The Mindful Path to Self-Compassion", Genre.SELF_HELP, 312, 5, 4.3);
        Book book18 = new Book("Book by Kristin Neff", Genre.SELF_HELP, 301, 4, 4.4);
        Book book19 = new Book("The Hitchhiker's Guide to the Galaxy", Genre.SCIENCE_FICTION, 670, 5, 4.0);
        Book book20 = new Book("The Lord of the Rings", Genre.FANTASY, 1478, 1, 4.5);
        Book book21 = new Book("The Stand", Genre.HORROR, 1153, 1, 4.6);
        Book book22 = new Book("The Winds of Winter", Genre.FANTASY, 1523, 2, 4.0);
        Book book23 = new Book("The Codex Seraphinianus", Genre.PUBLICATION, 400, 3, 4.2);
        Book book24 = new Book("Gone Girl", Genre.MYSTERY, 534, 2, 4.1);
        Book book25 = new Book("The Bible С++", Genre.SCIENCE_FICTION, 876, 3, 4.8);

        // Save authors and books in the database
        bookService.addAuthorAndBook(author1, book1);
        bookService.addAuthorAndBook(author2, book2);
        bookService.addAuthorAndBook(author3, book3);
        bookService.addAuthorAndBook(author4, book4);
        bookService.addAuthorAndBook(author5, book5);
        bookService.addAuthorAndBook(author6, book6);
        bookService.addAuthorAndBook(author7, book7);
        bookService.addAuthorAndBook(author8, book8);
        bookService.addAuthorAndBook(author9, book9);
        bookService.addAuthorAndBook(author10, book10);
        bookService.addAuthorAndBook(author11, book11);
        bookService.addAuthorAndBook(author12, book12);
        bookService.addAuthorAndBook(author13, book13);
        bookService.addAuthorAndBook(author14, book14);
        bookService.addAuthorAndBook(author15, book15);
        bookService.addAuthorAndBook(author16, book16);
        bookService.addAuthorAndBook(author17, book17);
        bookService.addAuthorAndBook(author18, book18);
        bookService.addAuthorAndBook(author19, book19);
        bookService.addAuthorAndBook(author20, book19);
        bookService.addAuthorAndBook(author21, book19);
        bookService.addAuthorAndBook(author22, book20);
        bookService.addAuthorAndBook(author23, book20);
        bookService.addAuthorAndBook(author24, book20);
        bookService.addAuthorAndBook(author25, book21);
        bookService.addAuthorAndBook(author26, book21);
        bookService.addAuthorAndBook(author27, book22);
        bookService.addAuthorAndBook(author28, book22);
        bookService.addAuthorAndBook(author29, book22);
        bookService.addAuthorAndBook(author30, book22);
        bookService.addAuthorAndBook(author31, book22);
        bookService.addAuthorAndBook(author32, book23);
        bookService.addAuthorAndBook(author33, book23);
        bookService.addAuthorAndBook(author26, book23);
        bookService.addAuthorAndBook(author34, book24);
        bookService.addAuthorAndBook(author35, book24);
        bookService.addAuthorAndBook(author36, book24);
        bookService.addAuthorAndBook(author37, book24);
        bookService.addAuthorAndBook(author38, book24);
        bookService.addAuthorAndBook(author21, book24);
        bookService.addAuthorAndBook(author39, book25);
        bookService.addAuthorAndBook(author40, book25);
        bookService.addAuthorAndBook(author41, book25);

        // Creating and Saving forms in the database
        Form form1 = new Form(LocalDate.of(2023, 1, 3), LocalDate.of(2023, 2, 1), reader1, book17);
        Form form2 = new Form(LocalDate.of(2023, 5, 8), LocalDate.of(2023, 6, 7), reader2, book10);
        Form form3 = new Form(LocalDate.of(2023, 4, 20), LocalDate.of(2023, 5, 16), reader3, book3);
        Form form4 = new Form(LocalDate.of(2023, 9, 1), null, reader1, book12);
        Form form5 = new Form(LocalDate.of(2023, 5, 25), LocalDate.of(2023, 6, 22), reader2, book5);
        Form form6 = new Form(LocalDate.of(2023, 9, 6), null, reader2, book16);
        Form form7 = new Form(LocalDate.of(2023, 5, 2), LocalDate.of(2023, 5, 15), reader3, book15);
        Form form8 = new Form(LocalDate.of(2023, 8, 29), LocalDate.of(2023, 9, 20), reader1, book9);
        Form form9 = new Form(LocalDate.of(2023, 5, 7), LocalDate.of(2023, 5, 15), reader3, book5);
        Form form10 = new Form(LocalDate.of(2023, 9, 15), null, reader3, book23);

        formService.createForm(form1);
        formService.createForm(form2);
        formService.createForm(form3);
        formService.createForm(form4);
        formService.createForm(form5);
        formService.createForm(form6);
        formService.createForm(form7);
        formService.createForm(form8);
        formService.createForm(form9);
        formService.createForm(form10);

        // Creating and Saving cards in the database
        Card card1 = new Card(reader1, book16);
        Card card2 = new Card(reader1, book1);
        Card card3 = new Card(reader2, book8);
        Card card4 = new Card(reader2, book1);
        Card card5 = new Card(reader3, book5);

        cardService.createCard(card1);
        cardService.createCard(card2);
        cardService.createCard(card3);
        cardService.createCard(card4);
        cardService.createCard(card5);
    }

}