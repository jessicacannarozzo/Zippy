package divideandconquer.zippy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import divideandconquer.zippy.models.ListItem;
import divideandconquer.zippy.models.TodoItem;
import divideandconquer.zippy.models.User;

/**
 * Created by geoff on 2017-10-13.
 */

public class TodoDetailActivity extends BaseActivity implements View.OnClickListener{


    private static final String TAG = "TodoDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";
    private String mTodoKey;


    //Databae references
    private DatabaseReference mTodoReference;
    private DatabaseReference mTodoItemReference;
    private ValueEventListener mTodoListener;
    private TodoAdapter mAdapter;

    //UI Fields
    private EditText mItemField;
    private Button mNewItemButton;
    private RecyclerView mTodoRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

        mTodoKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mTodoKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mTodoReference = FirebaseDatabase.getInstance().getReference()
                .child("/todo-lists/").child(mTodoKey);
        mTodoItemReference = FirebaseDatabase.getInstance().getReference()
                .child("todo-items").child(mTodoKey);

        mItemField = findViewById(R.id.field_todo_item_text);
        mNewItemButton = findViewById(R.id.button_add_new_todo_item);

        mTodoRecycler = findViewById(R.id.recycler_todo_list);

        mNewItemButton.setOnClickListener(this);
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
                TodoItem post2 = dataSnapshot.getValue(TodoItem.class);
                //This is where the owner of the list is saved as well as the name of the list and other stuff like that
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(TodoDetailActivity.this, "Failed to load TodoList.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mTodoReference.addValueEventListener(todoListener);
        // [END post_value_event_listener]

        // Keep copy of todoAdapter listener so we can remove it when app stops
        mTodoListener = todoListener;

        // Listen for new todo list items
        mAdapter = new TodoAdapter(this, mTodoItemReference);
        mTodoRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mTodoListener != null) {
            mTodoReference.removeEventListener(mTodoListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_add_new_todo_item) {
            String commentText = mItemField.getText().toString();
            if(commentText.isEmpty()) {
                Toast.makeText(TodoDetailActivity.this, "Cannot Add empty item.",
                        Toast.LENGTH_SHORT).show();
            } else {
                postItem();
            }
        }
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
                        String commentText = mItemField.getText().toString();

                        TodoItem comment = new TodoItem(uid, authorName, commentText);

                        // Push the comment, it will appear in the list
                        mTodoItemReference.push().setValue(comment);

                        // Clear the field
                        mItemField.setText(null);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkboxView;
        public TextView itemNameView;

        public TodoViewHolder(View itemView) {
            super(itemView);

            checkboxView = itemView.findViewById(R.id.todo_checkbox);
            itemNameView = itemView.findViewById(R.id.todo_item_name);

            checkboxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.w("Hello", "Hello");
                }
            });
        }

        public void bindToItem(TodoItem item) {
            checkboxView.setChecked(item.checked);
            itemNameView.setText(item.item);

        }

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
            intent.putExtra(TodoDetailActivity.EXTRA_POST_KEY, getIntent().getStringExtra(EXTRA_POST_KEY));
            startActivity(intent);

            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



}
