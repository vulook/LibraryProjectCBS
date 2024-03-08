package edu.cbsystematics.com.libraryprojectcbs.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class ErrorMessageDetails {
    private String timestamp;

    private int status;

    private String path;

    private String username;

    private String role;

    private String error;

    private String message;

}