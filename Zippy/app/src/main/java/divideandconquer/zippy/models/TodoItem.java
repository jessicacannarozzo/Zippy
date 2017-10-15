package divideandconquer.zippy.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class TodoItem {

    public String uid;
    public String author;
    public String item;
    public Boolean checked = false;

    public Date timechecked;

    public TodoItem() {
    }

    public TodoItem(String uid, String author, String item) {
        this.uid = uid;
        this.author = author;
        this.item = item;
    }

    public void setTimechecked(Date timechecked) {
        this.timechecked = timechecked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
