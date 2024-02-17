package edu.cbsystematics.com.libraryprojectcbs.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@NoArgsConstructor
@Table(name = "user_role")
public class UserRole {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "smallint(3)")
    private Long id;

    @NotNull(message = "User Role is required")
    @Size(min = 5, max = 40, message = "User Role should be between 5 and 40 characters")
    @Pattern.List({
    @Pattern(regexp = "^[A-Z]+_[A-Z]+$", message = "User Role may contain only uppercase letters"),
    @Pattern(regexp = "^ROLE_[A-Z]+$", message = "User Role must start with 'ROLE_'"),
    @Pattern(regexp = "^ROLE_\\S+$", message = "User Role must not contain spaces")
    })
    @Column(name = "role_name", nullable = false, columnDefinition = "varchar(40)")
    private String roleName;

    @NotNull(message = "Description is required")
    @Size(min = 10, max = 200, message = "Description should be between 10 and 200 characters")
    @Column(name = "description", nullable = false, columnDefinition = "varchar(200)")
    private String description;

    @OneToMany(mappedBy = "userRole")
    private List<User> users;

    public UserRole(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

}
