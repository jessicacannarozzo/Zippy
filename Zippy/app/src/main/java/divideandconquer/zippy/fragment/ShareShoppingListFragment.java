package divideandconquer.zippy.fragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class ShareShoppingListFragment extends ShoppingListFragment {

    public ShareShoppingListFragment() {}

    public Query getQuery(final DatabaseReference databaseReference) {

        Query keyQuery = databaseReference.child("shared-Lists").child(getUid());
        return keyQuery;
    }
}
