package edu.cbsystematics.com.libraryprojectcbs.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.SET_PAGE_SIZE;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationConstants {

    public static final Integer DEFAULT_PAGE_Number = 1;

    public static final Integer DEFAULT_PAGE_SIZE = SET_PAGE_SIZE;

    public static final String DEFAULT_SORT_DIRECTION = "ASC";

    public static final String DEFAULT_FIELD = "id";


}