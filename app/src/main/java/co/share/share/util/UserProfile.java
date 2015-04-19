package co.share.share.util;

public class UserProfile {


    String userName;

    static UserProfile profile;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
       return userName;
    }

    public static UserProfile getInstance() {
        if (profile == null)
            profile = new UserProfile();
        return profile;
    }

}
