package divideandconquer.zippy.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by geoff on 2017-10-10.
 */

public class MyShoppingListFragment extends ShoppingListFragment {


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("user-lists")
                .child(getUid());
    }


}
