package edu.cbsystematics.com.libraryprojectcbs.config;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.SET_PAGE_SIZE;


public class PaginationConstants {

    public static final int DEFAULT_PAGE_Number = 1;

    public static final int DEFAULT_PAGE_SIZE = SET_PAGE_SIZE;

    public static final String DEFAULT_SORT_DIRECTION = "ASC";

    public static final String DEFAULT_FIELD = "id";


    // Private constructor to prevent instantiation
    private PaginationConstants() {
        // Do nothing
    }

}