package edu.cbsystematics.com.libraryprojectcbs.models;

import edu.cbsystematics.com.libraryprojectcbs.config.comporator.ComparatorAge;
import edu.cbsystematics.com.libraryprojectcbs.config.comporator.ComparatorTerm;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SortComparator;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Entity
@Data
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

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone", nullable = false, columnDefinition = "varchar(20)")
    private String phone;

    @Column(name = "email", nullable = false, columnDefinition = "varchar(100)")
    private String email;

    @Column(name = "password", nullable = false, columnDefinition = "varchar(80)")
    private String password;

    @Column(name = "reg_date", nullable = false)
    private LocalDate regDate;

/*    @Column(name = "verification_code", columnDefinition = "varchar(80)")
    private String verificationCode;

    @Column(name = "enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean enabled;*/

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Card> carts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Form> forms;

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
    }

    public User(String firstName, String lastName, LocalDate birthDate, String phone, String email, String password, LocalDate regDate, UserRole userRole) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.regDate = regDate;
        this.userRole = userRole;
    }


    @Transient
    private int age;

    @Transient
    private int term;

    // This method returns the age of the current user
    @SortComparator(ComparatorAge.class)
    public int getAge() {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(this.birthDate, currentDate);
        return period.getYears();
    }

    // This method returns the term user in the library
    @SortComparator(ComparatorTerm.class)
    public int getTerm() {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(this.regDate, currentDate);
        return period.getYears();
    }

}
