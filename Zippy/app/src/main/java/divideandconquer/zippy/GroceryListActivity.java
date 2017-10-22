package divideandconquer.zippy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import divideandconquer.zippy.models.ListItem;
import divideandconquer.zippy.models.GroceryItem;
import divideandconquer.zippy.models.User;

/**
 * Created by geoff on 2017-10-13.
 */

public class GroceryListActivity extends BaseActivity {

    private static final String TAG = "GroceryListActivity";
    public static final String EXTRA_POST_KEY = "post_key";
    private String mTodoKey;

    //Databae references
    private DatabaseReference mGroceryListReference;
    private DatabaseReference mGroceryItemReference;
    private ValueEventListener mTodoListener;
    private GroceryListAdapter mAdapter;

    //UI Fields
    private EditText mGroceryItemField;
    private Button mNewGroceryItemButton;
    private RecyclerView mTodoRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list_detail);

        mTodoKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mTodoKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mGroceryListReference = FirebaseDatabase.getInstance().getReference()
                .child("/todo-lists/").child(mTodoKey);
        mGroceryItemReference = FirebaseDatabase.getInstance().getReference()
                .child("todo-items").child(mTodoKey);

        // getting UI components
        mGroceryItemField = findViewById(R.id.field_todo_item_text);
        mNewGroceryItemButton = findViewById(R.id.button_add_new_todo_item);
        mTodoRecycler = findViewById(R.id.recycler_todo_list);

        mNewGroceryItemButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String commentText = mGroceryItemField.getText().toString();
                if(commentText.isEmpty()) {
                    Toast.makeText(GroceryListActivity.this, "Cannot Add empty item.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    postItem();
                }
            }
        });

        mTodoRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener todoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListItem post = dataSnapshot.getValue(ListItem.class);
                GroceryItem post2 = dataSnapshot.getValue(GroceryItem.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(GroceryListActivity.this, "Failed to load TodoList.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mGroceryListReference.addValueEventListener(todoListener);
        // [END post_value_event_listener]

        // Keep copy of todoAdapter listener so we can remove it when app stops
        mTodoListener = todoListener;

        // Listen for new todo list items
        mAdapter = new GroceryListAdapter(this, mGroceryItemReference);
        mTodoRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mTodoListener != null) {
            mGroceryListReference.removeEventListener(mTodoListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    private void postItem() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.username;

                        // Create new Todo Item
                        String commentText = mGroceryItemField.getText().toString();

                        GroceryItem comment = new GroceryItem(uid, authorName, commentText);

                        // Push the comment, it will appear in the list
                        mGroceryItemReference.push().setValue(comment);

                        // Clear the field
                        mGroceryItemField.setText(null);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    public static class GroceryItemViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkboxView;
        public TextView itemNameView;
        private GroceryItem groceryItem;
        private String groceryItemId;
        private DatabaseReference mDatabaseReference;

        public GroceryItemViewHolder(View itemView) {
            super(itemView);

            checkboxView = itemView.findViewById(R.id.todo_checkbox);
            itemNameView = itemView.findViewById(R.id.todo_item_name);

            //on checkbox checked:
            checkboxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    groceryItem.checked = isChecked;
//                    Log.w("Item: ", groceryItem.item + " " + String.valueOf(mDatabaseReference.getKey()));
                    mDatabaseReference.child(groceryItemId).setValue(groceryItem); //only updating checked
                }
            });
        }

        //refer to GroceryListAdapter.java > onBindViewHolder to see where we receive item
        public void setGroceryItem(GroceryItem item) {groceryItem = item;}
        public void setGroceryItemId(String item) {groceryItemId = item;}
        public void setGroceryItemReference(DatabaseReference ref) {mDatabaseReference = ref;}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.share_list) {

            Intent intent = new Intent(this, ShareListActivity.class);
            intent.putExtra(GroceryListActivity.EXTRA_POST_KEY, getIntent().getStringExtra(EXTRA_POST_KEY));
            startActivity(intent);

            finish();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
