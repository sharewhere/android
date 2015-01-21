package co.share.share.models;

import java.util.List;

public class User {

    // User info
    private String firstName;
    private String lastName;
    private String emailAddress;
    private boolean isEmailHidden;

    private List<Transaction> userLendTransactions;
    private List<Transaction> userBorrowTransactions;

    // TODO: Gamification
    private String userScore;
    private List<Badge> badgeList;

    public User() {

    }
}
