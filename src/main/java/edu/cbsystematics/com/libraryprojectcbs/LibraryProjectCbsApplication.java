package edu.cbsystematics.com.libraryprojectcbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * FileName: LibraryProjectCbsApplication
 * Author: Andriy Vulook
 * Date: 03.01.2024 12:20
 * Description: Library Project CBS Application
 */

@EnableAspectJAutoProxy(proxyTargetClass=true)
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class LibraryProjectCbsApplication {

    // Define user roles as constants
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_LIBRARIAN = "ROLE_LIBRARIAN";
    public static final String ROLE_READER = "ROLE_READER";
    public static final String ROLE_WORKER = "ROLE_WORKER";


    // Define home page URLs for each role
    public static final String ADMIN_HOME_URL = "/library/admin/home/";
    public static final String LIBRARIAN_HOME_URL = "/library/librarian/home/";
    public static final String READER_HOME_URL = "/library/reader/home/";


    // The default reading period for books is 30 days
    public static final int READING_PERIOD = 30;


    // Minimum age requirement for users
    public static final int MIN_AGE = 6;


    // The number of items to display per page.
    public static final int SET_PAGE_SIZE = 7;


    public static void main(String[] args) {

        SpringApplication.run(LibraryProjectCbsApplication.class, args);

    }

}

