package edu.cbsystematics.com.libraryprojectcbs.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
@DynamicUpdate
@Table(name = "forms")
public class Form {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "book_returned")
    private LocalDate bookReturned;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    public Form(LocalDate startDate, LocalDate bookReturned, User user, Book book) {
        this.startDate = startDate;
        this.bookReturned = bookReturned;
        this.user = user;
        this.book = book;
    }

    // Calculate the return date based on the start date
    // Assuming a reading period of 3 weeks (30 days)
    public LocalDate calculateReturnDate() {
        if (this.startDate != null) {
            return this.startDate.plusDays(30);
        } else {
            throw new IllegalArgumentException("Please enter a start date for borrowing the book.");
        }
    }

}
