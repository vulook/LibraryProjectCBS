package edu.cbsystematics.com.libraryprojectcbs.dto;

import edu.cbsystematics.com.libraryprojectcbs.models.UserRole;
import edu.cbsystematics.com.libraryprojectcbs.utils.validdate.AgeConstraint;
import edu.cbsystematics.com.libraryprojectcbs.utils.validdate.ValidData;
import edu.cbsystematics.com.libraryprojectcbs.utils.validemail.ValidEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFullDTO {

    private Long id;

    @NotNull(message = "First Name is required")
    @Size(min = 3, max = 50, message = "First Name should be between 3 and 50 characters")
    @Pattern.List({
            @Pattern(regexp = "^[\\p{Alpha} ]*$", message = "First Name should contain only alphabets and space"),
            @Pattern(regexp = "^[^\\s].*$", message = "First Name should not start with space"),
            @Pattern(regexp = "^.*[^\\s]$", message = "First Name should not end with space"),
            @Pattern(regexp = "^((?!  ).)*$", message = "First Name should not contain consecutive spaces"),
            @Pattern(regexp = "^[^a-z].*$", message = "First Name should not start with a lowercase character")
    })
    private String firstName;

    @NotNull(message = "Last Name is required")
    @Size(min = 3, max = 50, message = "Last Name should be between 3 and 50 characters")
    @Pattern.List({
            @Pattern(regexp = "^[\\p{Alpha} ]*$", message = "Last Name should contain only alphabets and space"),
            @Pattern(regexp = "^[^\\s].*$", message = "Last Name should not start with space"),
            @Pattern(regexp = "^.*[^\\s]$", message = "Last Name should not end with space"),
            @Pattern(regexp = "^((?!  ).)*$", message = "Last Name should not contain consecutive spaces"),
            @Pattern(regexp = "^[^a-z].*$", message = "Last Name should not start with a lowercase character")
    })
    private String lastName;

    @NotNull(message = "Birth Date is required")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @AgeConstraint
    private LocalDate birthDate;

    @NotNull(message = "Phone is required")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{2}[- .]?\\d{2}$", message = "Enter a valid phone number")
    private String phone;

    @NotNull(message = "Email is required")
    @ValidEmail
    @Email(message = "Email address should be valid")
    @Pattern(regexp = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$", message = "Email should be valid")
    private String email;

    @NotNull(message = "Role is required")
    private UserRole userRole;

    //@NotNull(message = "Date of registration is required")
    //@ValidData
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate regDate;

    @Size(min = 8, max = 70, message = "Password should be between 8 and 20 characters")
    @Pattern.List({
            @Pattern(regexp = ".*[0-9].*", message = "Password should contain at least one digit"),
            @Pattern(regexp = ".*[a-z].*", message = "Password should contain at least one lowercase letter"),
            @Pattern(regexp = ".*[A-Z].*", message = "Password should contain at least one uppercase letter"),
            @Pattern(regexp = ".*[!@#&()\\[\\]{}:;',?/*~$^+=<>].*", message = "Password should contain at least one special character"),
            @Pattern(regexp = "^[\\S]+$", message = "Password must not contain spaces")
    })
    private String password;

}