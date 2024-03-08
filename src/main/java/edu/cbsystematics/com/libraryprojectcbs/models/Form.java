package edu.cbsystematics.com.libraryprojectcbs.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.List;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.READING_PERIOD;


@Entity
@Data
@NoArgsConstructor
@DynamicUpdate
@Table(name = "forms")
public class Form {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT")
    private Long id;

    @Column(name = "user_books_taken_count", nullable = false, columnDefinition = "BIGINT")
    private Integer userBooksTakenCount;

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

    public Form(User user, Book book) {
        this.startDate = LocalDate.now();
        this.user = user;
        this.book = book;
    }

    // Calculate the number of books taken by the user based on the list of Form objects.
    public void calculateUserBooksTakenCount(List<Form> userForms) {
        this.userBooksTakenCount = userForms.isEmpty() ? 1 : userForms.size() + 1;
    }

    // Calculate the return date based on the start date
    // Assuming a 30-day reading period.
    public LocalDate calculateReturnDate() {
        if (this.startDate != null) {
            return this.startDate.plusDays(READING_PERIOD);
        } else {
            throw new IllegalArgumentException("Please enter a start date for borrowing the book.");
        }
    }

}
