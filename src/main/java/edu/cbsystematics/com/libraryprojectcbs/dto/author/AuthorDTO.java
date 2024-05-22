package edu.cbsystematics.com.libraryprojectcbs.dto.author;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private Long bookCount;

}