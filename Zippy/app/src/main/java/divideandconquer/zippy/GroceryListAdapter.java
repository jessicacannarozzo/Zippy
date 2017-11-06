package divideandconquer.zippy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import divideandconquer.zippy.models.GroceryItem;
import divideandconquer.zippy.models.ListItem;

/**
 * Created by navi on 21/10/17.
 */

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListActivity.GroceryItemViewHolder> {

    private static final String TAG = "GroceryListAdapter";
    private Context mContext;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private List<String> mGroceryItemIds = new ArrayList<>();
    private List<GroceryItem> mGroceryItems = new ArrayList<>();

    public GroceryListAdapter(final Context context, DatabaseReference ref) {
        mContext = context;
        mDatabaseReference = ref;

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                GroceryItem item = dataSnapshot.getValue(GroceryItem.class);
                mGroceryItemIds.add(dataSnapshot.getKey());
                mGroceryItems.add(item);
                notifyItemInserted(mGroceryItems.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                GroceryItem newItem = dataSnapshot.getValue(GroceryItem.class);
                String groceryKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int groceryIndex = mGroceryItemIds.indexOf(groceryKey);
                if (groceryIndex > -1) {
                    // Replace with the new data
                    mGroceryItems.set(groceryIndex, newItem);

                    // Update the RecyclerView
                    notifyItemChanged(groceryIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + groceryKey);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                GroceryItem newItem = dataSnapshot.getValue(GroceryItem.class);
                removeGroceryItem(newItem, dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());

            }

        };
        ref.addChildEventListener(childEventListener);
        mChildEventListener = childEventListener;
    }

    @Override
    public GroceryListActivity.GroceryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.grocery_item, parent, false);

        // Refactor: also pass this instance to simplify the references that only this adapter holds. Useful when removing a list item.
        return new GroceryListActivity.GroceryItemViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(final GroceryListActivity.GroceryItemViewHolder viewHolder, int position) {
        //position of grocery item in array
        Log.d("test", String.valueOf(position));
        GroceryItem groceryItem = mGroceryItems.get(position);
        String mGroceryItemId = mGroceryItemIds.get(position);

        viewHolder.setGroceryItem(groceryItem, mGroceryItemId, mDatabaseReference);
    }

    @Override
    public int getItemCount() {return mGroceryItems.size();}

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }

    public void removeGroceryItem(GroceryItem groceryItem, String id) {
        int index = mGroceryItemIds.indexOf(id);
        mGroceryItemIds.remove(index);
        mGroceryItems.remove(index);
        notifyItemRemoved(index);
    }
}