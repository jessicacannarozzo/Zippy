package divideandconquer.zippy.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {


    public String username;
    public String email;
    public String displayName;

    public User() {
    }

    public User(String username, String email, String displayName) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
    }



}
