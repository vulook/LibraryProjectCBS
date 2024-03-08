package edu.cbsystematics.com.libraryprojectcbs.utils.role;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.*;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleUtilsForDescription {
    public static String getRoleDescription(String roleName) {
        return switch (roleName) {
            case ROLE_ADMIN -> "Administrator role with full access and control over the system";
            case ROLE_LIBRARIAN -> "Librarian role responsible for managing and organizing library resources";
            case ROLE_READER -> "Reader role with access to browse and borrow library resources";
            case ROLE_WORKER -> "Worker role responsible for janitorial tasks: sweep and mop the floors, dust furniture and shelves, take out the trash.";
            default -> throw new IllegalArgumentException("Invalid role name");
        };
    }
}