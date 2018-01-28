package divideandconquer.zippy.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import divideandconquer.zippy.Activities.GroceryListActivity;
import divideandconquer.zippy.R;
import divideandconquer.zippy.models.UserProfileColorService;
import divideandconquer.zippy.models.ListItem;
import divideandconquer.zippy.viewholder.ListViewHolder;

/**
 * Created by geoff on 2017-10-10.
 */

public abstract class ShoppingListFragment extends Fragment {

    private static final String TAG = "ShoppingListFragment";

    protected DatabaseReference mDatabase;
    protected FirebaseRecyclerAdapter<ListItem, ListViewHolder> mAdapter;
    protected RecyclerView mRecycler;
    protected LinearLayoutManager mManager;
    protected TextDrawable.IBuilder cBuilder;
    protected TestAdapter test;

    public ShoppingListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_lists, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = rootView.findViewById(R.id.list_of_lists);
        mRecycler.setHasFixedSize(true);

        //Generate a icon builder for the photos
        cBuilder = TextDrawable.builder()
                .beginConfig()
                .withBorder(0)
                .toUpperCase()
                .endConfig()
                .round();


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        test = getAdapter(mDatabase);
        mRecycler.setAdapter(test);
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public abstract Query getQuery(DatabaseReference databaseReference);
    protected abstract TestAdapter getAdapter(final DatabaseReference ref);

    public abstract class TestAdapter extends RecyclerView.Adapter<ListViewHolder> {
        private static final String TAG = "GroceryListAdapter";
        protected DatabaseReference mDatabaseReference;
        protected DatabaseReference mAllListsReference;
        protected ChildEventListener mChildEventListener;

        protected List<String> mGroceryItemIds;
        protected List<ListItem> mGroceryItems;

        public TestAdapter(DatabaseReference ref) {
            super();
            mGroceryItemIds = new ArrayList<>();
            mGroceryItems = new ArrayList<>();
            mAllListsReference = ref.child("todo-lists");
            mChildEventListener = blah();
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }

        public abstract ChildEventListener blah();

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_list, parent, false);
            return new ListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            ListItem groceryItem = mGroceryItems.get(position);
            String mGroceryItemId = mGroceryItemIds.get(position);

            final String postKey = mGroceryItemId;

            if (groceryItem.usersCount <= 1) {
                holder.sharedView.setImageResource(R.drawable.ic_person_black_24dp);
            } else {
                holder.sharedView.setImageResource(R.drawable.ic_group_black_24dp);
            }

            //Generate an icon using the first letter of their username
            String name = groceryItem.author.substring(0,1).toUpperCase();
            if (name.length() == 1) {
                int color = UserProfileColorService.getInstance().getGenerator().getColor(groceryItem.uid);
                TextDrawable drawable = cBuilder.build(name, color);
                holder.photoView.setImageDrawable(drawable);
            }

            // Set click listener for the whole list view
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Launch PostDetailActivity
                    Intent intent = new Intent(getActivity(), GroceryListActivity.class);
                    intent.putExtra(GroceryListActivity.EXTRA_POST_KEY, postKey);

                    startActivity(intent);
                }
            });

            holder.bindToListItem(groceryItem);
        }

        @Override
        public int getItemCount() {
            return mGroceryItems.size();
        }
    }

}


