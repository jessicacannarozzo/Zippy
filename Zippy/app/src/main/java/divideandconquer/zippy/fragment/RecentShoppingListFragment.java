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

    @Override
    public Query getQuery(final DatabaseReference databaseReference) {

        final List<String> lists = new ArrayList<>();

        final Query recentPostsQuery;

        databaseReference.child("todo-lists").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                lists.clear();
                final String list_uid = dataSnapshot.getKey();

                databaseReference.child("todo-lists/" + list_uid + "/access").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if (dataSnapshot.getKey().equals(getUid())) {
                            lists.add(list_uid);

                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recentPostsQuery = databaseReference.child("todo-lists").equalTo(lists.get(0));

        return recentPostsQuery;
    }
}
