package edu.cbsystematics.com.libraryprojectcbs.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "logs")
public class Logs {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT")
    private Long id;

    @Column(name = "user_number", nullable = false, columnDefinition = "BIGINT")
    private Long userNumber;

    @Column(name = "name", columnDefinition = "varchar(100)")
    private String fullName;

    @Column(name = "role", columnDefinition = "varchar(20)")
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_action")
    private ActionType action;

    @Column(name = "method", columnDefinition = "varchar(100)")
    private String method;

    @Column(name = "parameters", columnDefinition = "TEXT(1000)")
    private String parameters;

    @Column(name = "time", columnDefinition = "BIGINT")
    private Long executionTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userCreator;

    public Logs(String fullName, String role, ActionType action, String method, String parameters, Long executionTime, User userCreator) {
        this.fullName = fullName;
        this.role = role;
        this.action = action;
        this.method = method;
        this.parameters = parameters;
        this.executionTime = executionTime;
        this.userCreator = userCreator;
        this.createdAt = LocalDateTime.now();
    }

    public String getFormattedCreatedAt() {
        return this.createdAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    public void calculateUserSequenceNumber(List<Logs> logs) {
        this.userNumber = (long) (logs.isEmpty() ? 1 : logs.size() + 1);
    }

}
