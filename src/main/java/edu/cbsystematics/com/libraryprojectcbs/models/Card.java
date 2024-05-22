package edu.cbsystematics.com.libraryprojectcbs.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@Table(name = "cards")
public class Card {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT")
    private Long id;

    @Column(name = "approved", nullable = false)
    private boolean approved;

    @Column(name = "canceled", nullable = false)
    private boolean canceled;

    @Column(name = "cancellation_reason", columnDefinition = "varchar(200)")
    private String cancellationReason;

    @Column(name = "librarian_id", columnDefinition = "BIGINT")
    private Long librarianId;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false, referencedColumnName = "id")
    private Book book;

    @OneToOne(mappedBy = "card", cascade = CascadeType.ALL)
    private Form form;

    public Card(User user, Book book) {
        this.user = user;
        this.book = book;
        this.approved = false;
        this.canceled = false;
        this.updatedAt = LocalDateTime.now();
    }

    public Card(boolean approved, boolean canceled, String cancellationReason, Long librarianId, User user, Book book) {
        this.approved = approved;
        this.canceled = canceled;
        this.cancellationReason = cancellationReason;
        this.librarianId = librarianId;
        this.user = user;
        this.book = book;
        this.updatedAt = LocalDateTime.now();
    }


    public String getFormattedCreatedAt() {
        return this.updatedAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return "Card{" +
                "approved=" + approved + '\'' +
                ", canceled=" + canceled + '\'' +
                ", cancellationReason='" + cancellationReason + '\'' +
                ", librarianId=" + librarianId + '\'' +
                '}';
    }


}
