package edu.cbsystematics.com.libraryprojectcbs.config.comporator;

import edu.cbsystematics.com.libraryprojectcbs.models.User;

import java.util.Comparator;

public class ComparatorAge implements Comparator<User> {

    @Override
    public int compare(User user1, User user2) {
        int age1 = user1.getAge();
        int age2 = user2.getAge();

        return Integer.compare(age1, age2);
    }

}

