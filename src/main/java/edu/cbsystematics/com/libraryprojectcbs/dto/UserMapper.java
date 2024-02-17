package edu.cbsystematics.com.libraryprojectcbs.dto;

import edu.cbsystematics.com.libraryprojectcbs.models.User;


public class UserMapper {

    // Convert User JPA Entity into UserDTO
    public static UserDTO mapToUserDto(User user) {
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
    public static User mapToUser(UserDTO userDTO) {
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