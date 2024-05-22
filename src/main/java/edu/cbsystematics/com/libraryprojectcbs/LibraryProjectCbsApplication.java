package edu.cbsystematics.com.libraryprojectcbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * FileName: LibraryProjectCbsApplication
 * Author: Andriy Vulook
 * Date: 18.02.2024 12:20
 * Description: Library Project CBS Application
 */


@EnableAspectJAutoProxy(proxyTargetClass=true)
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class LibraryProjectCbsApplication {

    // Define user roles
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_LIBRARIAN = "ROLE_LIBRARIAN";
    public static final String ROLE_READER = "ROLE_READER";
    public static final String ROLE_WORKER = "ROLE_WORKER";


    // Define home page URLs for each role
    public static final String ADMIN_HOME_URL = "/library/admin/home/";
    public static final String LIBRARIAN_HOME_URL = "/library/librarian/home/";
    public static final String READER_HOME_URL = "/library/reader/home/";
    public static final String WORKER_HOME_URL = "/library/worker/home/";

    public static final String ANONYMOUS_HOME_URL = "/library/anonymous/";
    public static final String BOOK_HOME_URL = "/library/books/";
    public static final String AUTHOR_HOME_URL = "/library/authors/";
    public static final String CARD_HOME_URL = "/library/cards/";


    // The default reading period for books is 30 days
    public static final int READING_PERIOD = 30;


    // Minimum age requirement for users
    public static final int MIN_AGE = 6;


    // The number of items to display per page.
    public static final int SET_PAGE_SIZE_CONST = 7;


    //Lifetime of the verification token, min
    public static final int MINUTES_TO_EXPIRE_CONST = 5;


    public static void main(String[] args) {

        SpringApplication.run(LibraryProjectCbsApplication.class, args);

    }

}

