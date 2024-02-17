package edu.cbsystematics.com.libraryprojectcbs.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
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
    private Genre genre;

    @Column(name = "page_count", nullable = false)
    private int pageCount;

    @Column(name = "book_amount", nullable = false)
    private int bookAmount;

    @Column(name = "ratings")
    private Double ratings;

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

    public Book(String bookName, Genre genre, int pageCount, int bookAmount, Double ratings) {
        this.bookName = bookName;
        this.genre = genre;
        this.pageCount = pageCount;
        this.bookAmount = bookAmount;
        this.ratings = ratings;
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
