package edu.cbsystematics.com.libraryprojectcbs.dto.author;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorBookDTO {

    @NotNull(message = "First Name is required")
    @Size(min = 2, max = 50, message = "First Name should be between 2 and 50 characters")
    @Pattern.List({
            @Pattern(regexp = "^[\\p{Alpha}\\d\\- .]+$", message = "First Name should contain only alphabets, space, dot, and hyphen"),
            @Pattern(regexp = "^[^\\s].*$", message = "First Name should not start with space"),
            @Pattern(regexp = "^.*[^\\s]$", message = "First Name should not end with space"),
            @Pattern(regexp = "^((?!  ).)*$", message = "First Name should not contain consecutive spaces"),
            @Pattern(regexp = "^[^a-z].*$", message = "First Name should not start with a lowercase character")
    })
    private String firstName;

    @NotNull(message = "LastName Name is required")
    @Size(min = 2, max = 50, message = "Last Name should be between 2 and 50 characters")
    @Pattern.List({
            @Pattern(regexp = "^[\\p{Alpha}\\d\\- .]+$", message = "Last Name should contain only alphabets and space"),
            @Pattern(regexp = "^[^\\s].*$", message = "Last Name should not start with space"),
            @Pattern(regexp = "^.*[^\\s]$", message = "Last Name should not end with space"),
            @Pattern(regexp = "^((?!  ).)*$", message = "Last Name should not contain consecutive spaces"),
            @Pattern(regexp = "^[^a-z].*$", message = "Last Name should not start with a lowercase character")
    })
    private String lastName;

}