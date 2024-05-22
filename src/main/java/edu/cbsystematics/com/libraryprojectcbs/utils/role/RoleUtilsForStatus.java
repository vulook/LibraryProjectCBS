package edu.cbsystematics.com.libraryprojectcbs.utils.role;

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
            case ROLE_WORKER -> "worker";
            default -> null; // Return null for unknown roles
        };
    }


}