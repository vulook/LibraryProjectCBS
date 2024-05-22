package edu.cbsystematics.com.libraryprojectcbs.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.List;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.READING_PERIOD;


@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@Table(name = "forms")
public class Form {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT")
    private Long id;

    @Column(name = "user_books_taken_count", columnDefinition = "BIGINT")
    private Integer userBooksTakenCount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "book_returned")
    private LocalDate bookReturned;

    @Column(name = "is_returned", nullable = false)
    private boolean isReturned;

    @Column(name = "librarian_id")
    private Long librarianId;

    @OneToOne
    @JoinColumn(name = "card_id")
    private Card card;


    public Form(Card card, LocalDate startDate, LocalDate bookReturned, Long librarianId) {
        this.card = card;
        this.startDate = startDate;
        this.returnDate = calculateReturnDate();
        this.bookReturned = bookReturned;
        this.isReturned = calculateIsReturned();
        this.librarianId = librarianId;
    }

    public Form(Card card, LocalDate startDate, LocalDate bookReturned) {
        this.card = card;
        this.startDate = startDate;
        this.returnDate = calculateReturnDate();
        this.bookReturned = bookReturned;
        this.isReturned = calculateIsReturned();
    }

    public Form(Card card) {
        this.card = card;
        this.startDate = LocalDate.now();
        this.returnDate = calculateReturnDate();
        this.isReturned = calculateIsReturned();
    }

    // Calculate the return date based on the start date
    // Assuming a 30-day reading period.
    private LocalDate calculateReturnDate() {
        if (this.startDate != null) {
            return this.startDate.plusDays(READING_PERIOD);
        } else {
            throw new IllegalArgumentException("Enter start date to calculate reading time.");
        }
    }

    private boolean calculateIsReturned() {
        return bookReturned != null; // Book is considered returned if bookReturned date is set
    }


    // Calculate the number of books taken by the user based on the list of Form objects.
    public void calculateUserBooksTakenCount(List<Form> userForms) {
        this.userBooksTakenCount = userForms.isEmpty() ? 1 : userForms.size() + 1;
    }

    @Override
    public String toString() {
        return "Form{" +
                "userBooksTakenCount=" + userBooksTakenCount +
                ", startDate=" + startDate +
                ", returnDate=" + returnDate +
                ", bookReturned=" + bookReturned +
                ", isReturned=" + isReturned +
                '}';
    }

}
