package divideandconquer.zippy.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class RecentShoppingListFragment extends ShoppingListFragment {

    public RecentShoppingListFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("todo-lists")
                .limitToFirst(100);
        // [END recent_posts_query]

        return recentPostsQuery;
    }
}
