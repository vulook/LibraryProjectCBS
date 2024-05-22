package edu.cbsystematics.com.libraryprojectcbs.dto.user;

import edu.cbsystematics.com.libraryprojectcbs.models.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    // Convert User JPA Entity into UserDTO
    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getPhone(),
                user.getEmail(),
                user.getPassword()
        );
    }

    // Convert UserDTO into User JPA Entity
    public static User toEntity(UserDTO userDTO) {
        return new User(
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getBirthDate(),
                userDTO.getPhone(),
                userDTO.getEmail(),
                userDTO.getPassword()
        );
    }

}