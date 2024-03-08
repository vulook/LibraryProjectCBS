package edu.cbsystematics.com.libraryprojectcbs.dto;

import edu.cbsystematics.com.libraryprojectcbs.utils.validemail.ValidEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class PasswordForgotDTO {

    @NotNull(message = "Email is required")
    @ValidEmail
    @Email
    private String email;

}
