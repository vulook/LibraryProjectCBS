package edu.cbsystematics.com.libraryprojectcbs.exception;

public class AdminDeletionException extends RuntimeException {

  public AdminDeletionException(String message) {
    super(message);
  }

  public AdminDeletionException(String message, Throwable cause) {
    super(message, cause);
  }

}