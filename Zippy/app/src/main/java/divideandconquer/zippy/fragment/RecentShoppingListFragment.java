package divideandconquer.zippy.fragment;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import divideandconquer.zippy.models.ListItem;

public class RecentShoppingListFragment extends ShoppingListFragment {

    public RecentShoppingListFragment() {}

    public Query getQuery(final DatabaseReference databaseReference) {

        Query keyQuery = databaseReference.getDatabase().getReference("shared-lists").child(getUid());
        return keyQuery;
    }
}
