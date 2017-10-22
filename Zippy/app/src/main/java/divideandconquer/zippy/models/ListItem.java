package divideandconquer.zippy.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ListItem {

    public String uid;
    public String author;
    public String listName;
    public int usersCount = 0;
    public Map<String, Boolean> access = new HashMap<>();

    public ListItem() {
    }

    public ListItem(String uid, String author, String listName) {
        this.uid = uid;
        this.author = author;
        this.listName = listName;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("listName", listName);
        result.put("usersCount", usersCount);
        result.put("access", access);

        return result;
    }

}
