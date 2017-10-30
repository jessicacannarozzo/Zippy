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
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
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


        return new GroceryListActivity.GroceryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GroceryListActivity.GroceryItemViewHolder viewHolder, int position) {
        //position of grocery item in array
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

}