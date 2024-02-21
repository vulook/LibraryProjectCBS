package edu.cbsystematics.com.libraryprojectcbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class LibraryProjectCbsApplication {

    // Define user roles as constants
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_LIBRARIAN = "ROLE_LIBRARIAN";
    public static final String ROLE_READER = "ROLE_READER";

    // Define home page URLs for each role
    public static final String ADMIN_HOME_URL = "/library/admin/home/";
    public static final String LIBRARIAN_HOME_URL = "/library/librarian/home/";
    public static final String READER_HOME_URL = "/library/reader/home/";

    // Minimum age requirement for users
    public static final int MIN_AGE = 6;

    // The number of items to display per page.
    public static final int SET_PAGE_SIZE = 7;


    public static void main(String[] args) {

        SpringApplication.run(LibraryProjectCbsApplication.class, args);

    }

}

