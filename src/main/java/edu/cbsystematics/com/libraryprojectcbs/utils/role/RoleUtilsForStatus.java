package edu.cbsystematics.com.libraryprojectcbs.utils.role;

import edu.cbsystematics.com.libraryprojectcbs.exception.UserRoleNotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.*;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleUtilsForStatus {

    public static String getRoleLabel(String role) {
        return switch (role) {
            case ROLE_ADMIN -> "admin";
            case ROLE_LIBRARIAN -> "librarian";
            case ROLE_READER -> "reader";
            default ->
                    throw new UserRoleNotFoundException("Unknown role: " + role);
        };
    }


}