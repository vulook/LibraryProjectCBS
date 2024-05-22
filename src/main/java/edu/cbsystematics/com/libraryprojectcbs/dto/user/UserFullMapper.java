package edu.cbsystematics.com.libraryprojectcbs.dto.user;

import edu.cbsystematics.com.libraryprojectcbs.models.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFullMapper {

    // Convert User JPA Entity into UserDTO
    public static UserFullDTO toDTO(User user) {
        return new UserFullDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getPhone(),
                user.getEmail(),
                user.getUserRole(),
                user.getRegDate(),
                user.isEnabled(),
                user.getPassword()
        );
    }

    // Convert UserDTO into User JPA Entity
    public static User toEntity(UserFullDTO userFullDTO) {
        return new User(
                userFullDTO.getFirstName(),
                userFullDTO.getLastName(),
                userFullDTO.getBirthDate(),
                userFullDTO.getPhone(),
                userFullDTO.getEmail(),
                userFullDTO.getPassword(),
                userFullDTO.getRegDate(),
                userFullDTO.isEnabled(),
                userFullDTO.getUserRole()
        );
    }

}