package edu.cbsystematics.com.libraryprojectcbs.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "books", uniqueConstraints = @UniqueConstraint(columnNames = "isbn"))
public class Book {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "isbn", nullable = false)
    private String ISBN;

    @Column(name = "book_name", nullable = false)
    private String bookName;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private GenreType genreType;

    @Column(name = "page_count", nullable = false)
    private int pageCount;

    @Column(name = "book_amount", nullable = false)
    private int bookAmount;

    @Column(name = "book_available", nullable = false)
    private int bookAvailable;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_selected", nullable = false)
    private boolean isSelected;

    @Transient
    private String readStatus;

    @Transient
    private Long countRead;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book")
    private List<Card> carts;


    public Book(String ISBN, String bookName, GenreType genreType, int pageCount, int bookAmount, int bookAvailable, Set<Author> authors) {
        this.ISBN = ISBN;
        this.bookName = bookName;
        this.genreType = genreType;
        this.pageCount = pageCount;
        this.bookAmount = bookAmount;
        this.bookAvailable = bookAvailable;
        this.isAvailable = calculateAvailability();
        this.authors = authors;
    }

    public Book(String ISBN, String bookName, GenreType genreType, int pageCount, int bookAmount, int bookAvailable, LocalDateTime createdAt) {
        this.ISBN = ISBN;
        this.bookName = bookName;
        this.genreType = genreType;
        this.pageCount = pageCount;
        this.bookAmount = bookAmount;
        this.bookAvailable = bookAvailable;
        this.isAvailable = calculateAvailability();
        this.isSelected = false;
        this.createdAt = createdAt;
    }

    public Book(String ISBN, String bookName, GenreType genreType, int pageCount, int bookAmount, int bookAvailable) {
        this.ISBN = ISBN;
        this.bookName = bookName;
        this.genreType = genreType;
        this.pageCount = pageCount;
        this.bookAmount = bookAmount;
        this.bookAvailable = bookAvailable;
        this.isAvailable = calculateAvailability();
        this.isSelected = false;
        this.createdAt = LocalDateTime.now();
    }


    // Calculates the availability of the book based on the available quantity
    private boolean calculateAvailability() {
        return this.bookAvailable > 0;
    }

    public Book(int bookAvailable) {
        this.bookAvailable = bookAvailable;
        this.isAvailable = calculateAvailability();
    }

    public void setBookAvailable(int bookAvailable) {
        this.bookAvailable = bookAvailable;
        this.isAvailable = bookAvailable > 0;
    }


    public boolean isAvailable() {
        return isAvailable;
    }


    // Returns the set of authors for this book
    public Set<Author> getAuthors() {
        return authors != null ? authors : new HashSet<>();
    }


    // Adds the given author to the set of authors for this book
    public void addAuthor(Author author) {
        if (this.authors == null) {
            this.authors = new HashSet<>();
        }
        this.authors.add(author);
    }


    @Override
    public String toString() {
        return "Book{" +
                "id=" + id + '\'' +
                ", isbn='" + ISBN + '\'' +
                ", bookName='" + bookName + '\'' +
                ", genreType=" + genreType + '\'' +
                ", pageCount=" + pageCount + '\'' +
                ", bookAmount=" + bookAmount + '\'' +
                ", bookAvailable=" + bookAvailable + '\'' +
                ", isAvailable=" + isAvailable + '\'' +
                ", createdAt=" + createdAt + '\'' +
                ", isSelected=" + isSelected + '\'' +
                ", readStatus='" + readStatus + '\'' +
                ", authors=" + authors +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(ISBN, book.ISBN) && Objects.equals(bookName, book.bookName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ISBN, bookName);
    }

}
