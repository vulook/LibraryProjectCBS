package edu.cbsystematics.com.libraryprojectcbs.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "authors")
public class Author {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;

    @NotNull(message = "First Name is required")
    @Size(min = 2, max = 50, message = "First Name should be between 2 and 50 characters")
    @Pattern.List({
            @Pattern(regexp = "^[\\p{Alpha}\\d\\- .]+$", message = "First Name should contain only alphabets, space, dot, and hyphen"),
            @Pattern(regexp = "^[^\\s].*$", message = "First Name should not start with space"),
            @Pattern(regexp = "^.*[^\\s]$", message = "First Name should not end with space"),
            @Pattern(regexp = "^((?! ).)*$", message = "First Name should not contain consecutive spaces"),
            @Pattern(regexp = "^[^a-z].*$", message = "First Name should not start with a lowercase character")
    })
    @Column(name = "first_name", nullable = false, columnDefinition = "varchar(50)")
    private String firstName;

    @NotNull(message = "LastName Name is required")
    @Size(min = 2, max = 50, message = "Last Name should be between 2 and 50 characters")
    @Pattern.List({
            @Pattern(regexp = "^[\\p{Alpha}\\d\\- .]+$", message = "Last Name should contain only alphabets and space"),
            @Pattern(regexp = "^[^\\s].*$", message = "Last Name should not start with space"),
            @Pattern(regexp = "^.*[^\\s]$", message = "Last Name should not end with space"),
            @Pattern(regexp = "^((?! ).)*$", message = "Last Name should not contain consecutive spaces"),
            @Pattern(regexp = "^[^a-z].*$", message = "Last Name should not start with a lowercase character")
    })
    @Column(name = "last_name", nullable = false, columnDefinition = "varchar(50)")
    private String lastName;

    @ManyToMany(mappedBy = "authors", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    private Set<Book> books = new HashSet<>();

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id + + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author author)) return false;

        if (!getFirstName().equals(author.getFirstName())) return false;
        return getLastName().equals(author.getLastName());
    }

    @Override
    public int hashCode() {
        int result = getFirstName().hashCode();
        result = 31 * result + getLastName().hashCode();
        return result;
    }

}
