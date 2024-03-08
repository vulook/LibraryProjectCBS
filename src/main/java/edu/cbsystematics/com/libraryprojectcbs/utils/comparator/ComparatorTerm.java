package edu.cbsystematics.com.libraryprojectcbs.utils.comparator;

import edu.cbsystematics.com.libraryprojectcbs.models.User;

import java.util.Comparator;

public class ComparatorTerm implements Comparator<User> {

    @Override
    public int compare(User user1, User user2) {
        int term1 = user1.getTerm();
        int term2 = user2.getTerm();

        return Integer.compare(term1, term2);
    }

}
