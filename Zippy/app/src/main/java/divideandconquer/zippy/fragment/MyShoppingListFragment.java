package divideandconquer.zippy.fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import divideandconquer.zippy.models.ListItem;

/**
 * Created by geoff on 2017-10-10.
 */

public class MyShoppingListFragment extends ShoppingListFragment {


    public TestAdapter getAdapter(final DatabaseReference ref) {
        test = new TestAdapter(ref) {
            @Override
            public ChildEventListener blah() {
                mDatabaseReference = ref.child("owned").child(getUid());
                ChildEventListener childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(final DataSnapshot myListsDataSnapshot, String s) {

                        mAllListsReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot allListsDataSnapshot, String s) {
                                if (myListsDataSnapshot.getKey().equals(allListsDataSnapshot.getKey())) {
                                    ListItem item = allListsDataSnapshot.getValue(ListItem.class);
                                    mGroceryItemIds.add(allListsDataSnapshot.getKey());
                                    mGroceryItems.add(item);
                                    notifyItemInserted(mGroceryItems.size() - 1);
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
                };
                return childEventListener;
            }
        };

        return test;
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("user-lists").child(getUid());
    }


}
