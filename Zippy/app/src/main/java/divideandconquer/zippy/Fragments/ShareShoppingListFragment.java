package divideandconquer.zippy.Fragments;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import divideandconquer.zippy.models.ListItem;

public class ShareShoppingListFragment extends ShoppingListFragment {

    public ShareShoppingListFragment() {super();}

    protected TestAdapter getAdapter(final DatabaseReference ref) {
        test = new TestAdapter(ref) {
            @Override
            public ChildEventListener blah() {
                mDatabaseReference = ref.child("shared").child(getUid());
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


                                ListItem newList = dataSnapshot.getValue(ListItem.class);
                                String listItemKey = dataSnapshot.getKey();

                                // [START_EXCLUDE]
                                int listIndex = mGroceryItemIds.indexOf(listItemKey);
                                if (listIndex > -1) {
                                    // Replace with the new data
                                    mGroceryItems.set(listIndex, newList);

                                    // Update the RecyclerView
                                    notifyItemChanged(listIndex);
                                }

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                String listItemKey = dataSnapshot.getKey();
                                Log.d("onChildRemoved", listItemKey);

                                // [START_EXCLUDE]
                                int listIndex = mGroceryItemIds.indexOf(listItemKey);
                                if (listIndex > -1) {
                                    // Remove data from the list
                                    mGroceryItemIds.remove(listItemKey);
                                    mGroceryItems.remove(listIndex);

                                    // Update the RecyclerView
                                    notifyItemRemoved(listIndex);
                                }

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getContext(), "Failed to Lists.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                        int listIndex = mGroceryItemIds.indexOf(dataSnapshot.getKey());
                        if (listIndex > -1) {
                            // Remove data from the list
                            mGroceryItemIds.remove(listIndex);
                            mGroceryItems.remove(listIndex);
                            // Update the RecyclerView
                            notifyItemRemoved(listIndex);
                        }

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

    public Query getQuery(final DatabaseReference databaseReference) {

        Query keyQuery = databaseReference.child("shared-Lists").child(getUid());
        return keyQuery;
    }
}
