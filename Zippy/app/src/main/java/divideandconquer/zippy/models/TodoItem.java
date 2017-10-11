package divideandconquer.zippy.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class TodoItem {

    public String uid;
    public String author;
    public String item;

    public TodoItem() {
    }

    public TodoItem(String uid, String author, String item) {
        this.uid = uid;
        this.author = author;
        this.item = item;
    }

}
