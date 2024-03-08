package edu.cbsystematics.com.libraryprojectcbs.config;

import edu.cbsystematics.com.libraryprojectcbs.models.*;
import edu.cbsystematics.com.libraryprojectcbs.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.*;


//@Component
public class DatabaseInitializer {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final BookService bookService;
    private final FormService formService;
    private final CardService cardService;


    //@Autowired
    public DatabaseInitializer(UserService userService, UserRoleService userRoleService, BookService bookService, FormService formService, CardService cardService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.bookService = bookService;
        this.formService = formService;
        this.cardService = cardService;
    }

    //@PostConstruct
    public void init() {

        // Creating and Saving the roles in the database
        UserRole adminRole = new UserRole(ROLE_ADMIN, "Administrator role with full access and control over the system");
        UserRole librarianRole = new UserRole(ROLE_LIBRARIAN, "Librarian role responsible for managing and organizing library resources");
        UserRole readerRole = new UserRole(ROLE_READER, "Reader role with access to browse and borrow library resources");
        UserRole workerRole = new UserRole(ROLE_WORKER, "Sweep and mop the floors, Dust furniture and shelves, Take out the trash.");

        userRoleService.createRoleDatabaseInit(adminRole);
        userRoleService.createRoleDatabaseInit(librarianRole);
        userRoleService.createRoleDatabaseInit(readerRole);
        userRoleService.createRoleDatabaseInit(workerRole);

        // Creating and Saving users in the database
        User admin = new User("Ben", "Smith", LocalDate.of(1982, 1, 15), "+38 (066) 860-92-21", "admin@example.com", "123", LocalDate.of(2020, 4, 1), adminRole);
        User librarian1 = new User("Megan", "Robinson", LocalDate.of(1992, 5, 5), "+38 (097) 311-02-00", "libr1@example.com", "123", LocalDate.of(2020, 4, 15), librarianRole);
        User librarian2 = new User("Christopher", "Anderson", LocalDate.of(1982, 5, 16), "+38 (097) 600-60-66", "libr2@example.com", "123", LocalDate.of(2022, 4, 14), librarianRole);
        User reader1 = new User("Jane", "Doe", LocalDate.of(2005, 3, 15), "+38 (098) 962-22-12", "reader1@example.com", "123", LocalDate.of(2022, 12, 20), readerRole);
        User reader2 = new User("Elizabeth", "Roberts", LocalDate.of(2008, 7, 20), "+38 (095) 110-11-10", "reader2@example.com", "123", LocalDate.of(2022, 12, 26), readerRole);
        User reader3 = new User("Liam", "Wang", LocalDate.of(2002, 1, 10), "+38 (066) 545-55-15", "reader3@example.com", "123", LocalDate.of(2023, 8, 2), readerRole);
        User reader4 = new User("Olga", "Greyson", LocalDate.of(1998, 5, 2), "+38 (095) 555-44-33", "reader4@example.com", "123", LocalDate.of(2023, 9, 23), readerRole);
        User reader5 = new User("Noah", "Brown", LocalDate.of(2000, 3, 14), "+38 (063) 333-22-11", "reader5@example.com", "123", LocalDate.of(2023, 9, 7), readerRole);
        User reader6 = new User("Anna", "Fox", LocalDate.of(1999, 12, 13), "+38 (066) 545-55-15", "reader6@example.com", "123", LocalDate.of(2023, 10, 22), readerRole);
        User reader7 = new User("Emilia", "Proper", LocalDate.of(2004, 8, 27), "+38 (097) 777-66-55", "reader7@example.com", "123", LocalDate.of(2023, 11, 1), readerRole);
        User reader8 = new User("Ava", "Willow", LocalDate.of(2006, 2, 19), "+38 (093) 444-33-22", "reader8@example.com", "123", LocalDate.of(2023, 11, 9), readerRole);
        User reader9 = new User("William", "Griffin", LocalDate.of(1999, 10, 11), "+38 (067) 222-11-00", "reader9@example.com", "123", LocalDate.of(2023, 11, 28), readerRole);
        User reader10 = new User("Sophia", "Pink", LocalDate.of(2001, 7, 4), "+38 (099) 111-00-99", "reader10@example.com", "123", LocalDate.of(2023, 12, 1), readerRole);
        User reader11 = new User("James", "Oran", LocalDate.of(2003, 4, 16), "+38 (068) 888-77-66", "reader11@example.com", "123", LocalDate.of(2023, 12, 5), readerRole);
        User reader12 = new User("Isabella", "Yellow", LocalDate.of(2005, 11, 28), "+38 (096) 666-55-44", "reader12@example.com", "123", LocalDate.of(2024, 1, 25), readerRole);
        User reader13 = new User("Benjamin", "Purple", LocalDate.of(2001, 6, 20), "+38 (098) 555-44-33", "reader13@example.com", "123", LocalDate.of(2024, 1, 25), readerRole);
        User reader14 = new User("Olivia", "Whitman", LocalDate.of(2003, 3, 14), "+38 (063) 444-33-22", "reader14@example.com", "123", LocalDate.of(2024, 1, 8), readerRole);
        User reader15 = new User("Gracie", "Gretchen", LocalDate.of(1999, 10, 5), "+38 (095) 333-22-11", "reader15@example.com", "123", LocalDate.of(2024, 2, 1), readerRole);
        User reader16 = new User("Emma", "Pink", LocalDate.of(2004, 7, 22), "+38 (067) 222-11-00", "reader16@example.com", "123", LocalDate.of(2024, 2, 1), readerRole);
        User worker1 = new User("Alexander", "Greyson", LocalDate.of(2000, 2, 1), "+38 (097) 111-00-99", "worker1@example.com", "123", LocalDate.of(2024, 2, 5), workerRole);
        User worker2 = new User("Sophia", "Red", LocalDate.of(2002, 5, 17), "+38 (066) 000-99-88", "worker2@example.com", "123", LocalDate.of(2024, 2, 5), workerRole);
        User reader19 = new User("Elijah", "Black", LocalDate.of(1998, 8, 30), "+38 (093) 999-88-77", "reader19@example.com", "123", LocalDate.of(2024, 2, 10), readerRole);
        User reader20 = new User("Gracie", "Grant", LocalDate.of(2006, 1, 10), "+38 (068) 888-77-66", "reader20@example.com", "123", LocalDate.of(2024, 2, 19), readerRole);
        User reader21 = new User("Whittaker", "Winter", LocalDate.of(2009, 4, 24), "+38 (099) 777-66-55", "reader21@example.com", "123", LocalDate.of(2024, 3, 1), readerRole);
        User reader22 = new User("Mia", "Grayer", LocalDate.of(2003, 9, 7), "+38 (064) 666-55-44", "reader22@example.com", "123", LocalDate.of(2024, 3, 10), readerRole);
        User reader23 = new User("Lana", "Bellow", LocalDate.of(2007, 12, 20), "+38 (092) 555-44-33", "reader23@example.com", "123", LocalDate.of(2024, 3, 12), readerRole);

        userService.createUserDatabaseInit(admin);
        userService.createUserDatabaseInit(librarian1);
        userService.createUserDatabaseInit(librarian2);
        userService.createUserDatabaseInit(reader1);
        userService.createUserDatabaseInit(reader2);
        userService.createUserDatabaseInit(reader3);
        userService.createUserDatabaseInit(reader4);
        userService.createUserDatabaseInit(reader5);
        userService.createUserDatabaseInit(reader6);
        userService.createUserDatabaseInit(reader7);
        userService.createUserDatabaseInit(reader8);
        userService.createUserDatabaseInit(reader9);
        userService.createUserDatabaseInit(reader10);
        userService.createUserDatabaseInit(reader11);
        userService.createUserDatabaseInit(reader12);
        userService.createUserDatabaseInit(reader13);
        userService.createUserDatabaseInit(reader14);
        userService.createUserDatabaseInit(reader15);
        userService.createUserDatabaseInit(reader16);
        userService.createUserDatabaseInit(worker1);
        userService.createUserDatabaseInit(worker2);
        userService.createUserDatabaseInit(reader19);
        userService.createUserDatabaseInit(reader20);
        userService.createUserDatabaseInit(reader21);
        userService.createUserDatabaseInit(reader22);
        userService.createUserDatabaseInit(reader23);

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
        Book book1 = new Book("1984", GenreType.FICTION, 328, 5, LocalDateTime.of(2022, 3, 12, 12, 0));
        Book book2 = new Book("The Catcher in the Rye", GenreType.FICTION, 224, 5, LocalDateTime.of(2022, 3, 12, 12, 0));
        Book book3 = new Book("To Kill a Mockingbird", GenreType.FICTION, 336, 4, LocalDateTime.of(2022, 3, 12, 12, 0));
        Book book4 = new Book("The Lord of the Rings", GenreType.FANTASY, 1178, 2, LocalDateTime.of(2022, 3, 12, 12, 0));
        Book book5 = new Book("The Hitchhiker's Guide to the Galaxy", GenreType.SCIENCE_FICTION, 224, 4, LocalDateTime.of(2023, 6, 12, 12, 0));
        Book book6 = new Book("The Da Vinci Code", GenreType.MYSTERY, 454, 2, LocalDateTime.of(2023, 6, 12, 12, 0));
        Book book7 = new Book("The Hunger Games", GenreType.SCIENCE_FICTION, 374, 3, LocalDateTime.of(2023, 6, 12, 12, 0));
        Book book8 = new Book("Harry Potter and the Sorcerer's Stone", GenreType.FANTASY, 321, 3, LocalDateTime.of(2024, 3, 12, 12, 0));
        Book book9 = new Book("The Girl with the Dragon Tattoo", GenreType.MYSTERY, 712, 2, LocalDateTime.of(2023, 3, 12, 12, 0));
        Book book10 = new Book("The Fault in Our Stars", GenreType.PUBLICATION, 313, 5, LocalDateTime.of(2023, 9, 1, 12, 0));
        Book book11 = new Book("The Martian", GenreType.SCIENCE_FICTION, 390, 1, LocalDateTime.of(2023, 9, 1, 12, 0));
        Book book12 = new Book("The Alchemist", GenreType.FICTION, 202, 2, LocalDateTime.of(2023, 9, 12, 1, 0));
        Book book13 = new Book("The Secret", GenreType.SELF_HELP, 178, 1, LocalDateTime.of(2023, 9, 12, 1, 0));
        Book book14 = new Book("The 4-Hour Workweek", GenreType.SELF_HELP, 448, 2, LocalDateTime.of(2023, 9, 1, 12, 0));
        Book book15 = new Book("The Lean Startup", GenreType.PUBLICATION, 330, 2, LocalDateTime.of(2023, 12, 1, 12, 0));
        Book book16 = new Book("The Power of Now", GenreType.BIOGRAPHY, 241, 2, LocalDateTime.of(2023, 12, 1, 12, 0));
        Book book17 = new Book("The Mindful Path to Self-Compassion", GenreType.SELF_HELP, 312, 5, LocalDateTime.of(2023, 12, 1, 12, 0));
        Book book18 = new Book("Book by Kristin Neff", GenreType.SELF_HELP, 301, 4, LocalDateTime.of(2024, 2, 1, 2, 0));
        Book book19 = new Book("The Hitchhiker's Guide to the Galaxy", GenreType.SCIENCE_FICTION, 670, 5, LocalDateTime.of(2024, 2, 1, 12, 0));
        Book book20 = new Book("The Lord of the Rings", GenreType.FANTASY, 1478, 1, LocalDateTime.of(2024, 2, 1, 12, 0));
        Book book21 = new Book("The Stand", GenreType.HORROR, 1153, 1, LocalDateTime.of(2024, 2, 1, 12, 0));
        Book book22 = new Book("The Winds of Winter", GenreType.FANTASY, 1523, 2, LocalDateTime.of(2024, 3, 1, 12, 0));
        Book book23 = new Book("The Codex Seraphinianus", GenreType.PUBLICATION, 400, 3, LocalDateTime.of(2024, 3, 1, 12, 0));
        Book book24 = new Book("Gone Girl", GenreType.MYSTERY, 534, 2, LocalDateTime.of(2024, 3, 1, 12, 0));
        Book book25 = new Book("The Bible С++", GenreType.SCIENCE_FICTION, 876, 3, LocalDateTime.of(2024, 3, 1, 12, 0));

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