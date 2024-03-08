package edu.cbsystematics.com.libraryprojectcbs.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "books")
public class Book {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "book_name", nullable = false)
    private String bookName;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private GenreType genreType;

    @Column(name = "page_count", nullable = false)
    private int pageCount;

    @Column(name = "book_amount", nullable = false)
    private int bookAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book")
    private List<Card> carts;

    @OneToMany(mappedBy = "book")
    private List<Form> forms;

    public Book(String bookName, GenreType genreType, int pageCount, int bookAmount, LocalDateTime createdAt) {
        this.bookName = bookName;
        this.genreType = genreType;
        this.pageCount = pageCount;
        this.bookAmount = bookAmount;
        this.createdAt = createdAt;
    }

    public Book(String bookName, GenreType genreType, int pageCount, int bookAmount) {
        this.bookName = bookName;
        this.genreType = genreType;
        this.pageCount = pageCount;
        this.bookAmount = bookAmount;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", bookName='" + bookName + '\'' +
                ", genreType=" + genreType +
                ", pageCount=" + pageCount +
                ", bookAmount=" + bookAmount +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;

        return getBookName().equals(book.getBookName());
    }

    @Override
    public int hashCode() {
        return getBookName().hashCode();
    }

}
