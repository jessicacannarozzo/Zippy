package divideandconquer.zippy.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class GroceryItem {

    public String uid;
    public String author;
    public String item;
    public Boolean checked = false;

    public Date timechecked;

    public GroceryItem() {
    }

    public GroceryItem(String uid, String author, String item) {
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("item", item);
        result.put("checked", checked);

        return result;
    }
}
