package edu.cbsystematics.com.libraryprojectcbs.dto.book;

import edu.cbsystematics.com.libraryprojectcbs.dto.author.AuthorBookDTO;
import edu.cbsystematics.com.libraryprojectcbs.models.GenreType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

	private Long id;

	@NotNull(message = "ISBN is required")
	@Pattern(regexp = "^[0-9\\-]+$", message = "ISBN can only contain digits and hyphens")
    private String ISBN;

	@NotNull(message = "Book Name is required")
	@Pattern(regexp = "^[A-Z0-9].*", message = "Book titles must start with an uppercase letter or a digit")
	@Pattern(regexp = "^[^\\s].*$", message = "Book titles should not start with space")
	@Pattern(regexp = "^[^a-z].*$", message = "Book titles should not start with a lowercase character")
	private String bookName;

	@NotNull(message = "Genre Type is required")
	private GenreType genreType;

	@NotNull(message = "Page Count is required")
	@Positive(message = "Page Count must be greater than 0")
	private int pageCount;

	@NotNull(message = "Book Amount is required")
	@Positive(message = "Book Amount must be greater than 0")
	private int bookAmount;

	@NotNull(message = "Book Available is required")
	@Positive(message = "Book Available must be greater than 0")
	private int bookAvailable;

	@Valid
	private Set<AuthorBookDTO> authors;

}