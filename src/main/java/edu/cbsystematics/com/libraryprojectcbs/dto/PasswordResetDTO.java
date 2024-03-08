package edu.cbsystematics.com.libraryprojectcbs.dto;

import edu.cbsystematics.com.libraryprojectcbs.utils.validmatch.FieldMatch;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match")
public class PasswordResetDTO {

    @NotNull(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters")
    @Pattern.List({
            @Pattern(regexp = ".*[0-9].*", message = "Password should contain at least one digit"),
            @Pattern(regexp = ".*[a-z].*", message = "Password should contain at least one lowercase and uppercase letter"),
            @Pattern(regexp = ".*[A-Z].*", message = "Password should contain at least one uppercase letter"),
            @Pattern(regexp = ".*[!@#&()\\[\\]{}:;',?/*~$^+=<>].*", message = "Password should contain at least one special character"),
            @Pattern(regexp = "^[\\S]+$", message = "Password must not contain spaces")
    })
    private String password;

    @NotNull(message = "Confirm Password is required")
    private String confirmPassword;

    private String token;

}
