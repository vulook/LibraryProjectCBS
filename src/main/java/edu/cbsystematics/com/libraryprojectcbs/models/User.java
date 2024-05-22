package edu.cbsystematics.com.libraryprojectcbs.models;

import edu.cbsystematics.com.libraryprojectcbs.utils.comparator.ComparatorAge;
import edu.cbsystematics.com.libraryprojectcbs.utils.comparator.ComparatorTerm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SortComparator;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT")
    private long id;

    @Column(name = "first_name", nullable = false, columnDefinition = "varchar(50)")
    private String firstName;

    @Column(name = "last_name", nullable = false, columnDefinition = "varchar(50)")
    private String lastName;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone", nullable = false, columnDefinition = "varchar(20)")
    private String phone;

    @Column(name = "email", nullable = false, columnDefinition = "varchar(100)")
    private String email;

    @Column(name = "password", nullable = false, columnDefinition = "varchar(80)")
    private String password;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "verification_code", columnDefinition = "varchar(80)")
    private String verificationCode;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @Column(name = "reset_date")
    private LocalDateTime passwordResetDate;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Card> carts;

    @OneToMany(mappedBy = "userCreator", cascade = CascadeType.ALL)
    private List<Logs> logs;

    @ManyToOne
    @JoinColumn(name = "user_role", referencedColumnName = "id")
    private UserRole userRole;


    public User(String email, String password, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    public User(String firstName, String lastName, LocalDate birthDate, String phone, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.enabled = false;
    }

    public User(String firstName, String lastName, LocalDate birthDate, String phone, String email, String password, LocalDateTime regDate, boolean enabled, UserRole userRole) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.regDate = regDate;
        this.enabled = enabled;
        this.userRole = userRole;
    }

    @Transient
    private int age;

    @Transient
    private int term;


    // This method returns the age of the current user
    @SortComparator(ComparatorAge.class)
    public Integer getAge() {
        if (this.birthDate == null) {
            return -1;
        }
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(this.birthDate, currentDate);
        return period.getYears();
    }

    // This method returns the term user in the library
    @SortComparator(ComparatorTerm.class)
    public Integer getTerm() {
        if (this.regDate == null) {
            return -1;
        }
        LocalDate currentDate = LocalDate.now();
        LocalDate regDateWithoutTime = this.regDate.toLocalDate();
        long years = ChronoUnit.YEARS.between(regDateWithoutTime, currentDate);
        return (int) years;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", regDate=" + regDate +
                '}';
    }

}
