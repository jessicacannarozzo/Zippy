package divideandconquer.zippy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import divideandconquer.zippy.GroceryListActivity;
import divideandconquer.zippy.R;
import divideandconquer.zippy.models.ListItem;
import divideandconquer.zippy.viewholder.ListViewHolder;

/**
 * Created by geoff on 2017-10-10.
 */

public abstract class ShoppingListFragment extends Fragment {

    private static final String TAG = "ShoppingListFragment";

    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<ListItem, ListViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

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

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);



        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ListItem>()
                .setQuery(postsQuery, ListItem.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<ListItem, ListViewHolder>(options) {

            @Override
            public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new ListViewHolder(inflater.inflate(R.layout.item_list, viewGroup, false));
            }



            protected void onBindViewHolder(ListViewHolder viewHolder, int position, final ListItem model) {
                final DatabaseReference listRef = getRef(position);

                final String postKey = listRef.getKey();

                // Set click listener for the whole list view
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), GroceryListActivity.class);
                        intent.putExtra(GroceryListActivity.EXTRA_POST_KEY, postKey);

                        startActivity(intent);
                    }
                });

                viewHolder.bindToListItem(model);

            }
        };
        mRecycler.setAdapter(mAdapter);
    }



    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);



}


