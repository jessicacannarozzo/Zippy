package divideandconquer.zippy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import divideandconquer.zippy.models.ListItem;
import divideandconquer.zippy.models.User;

/**
 * Created by geoff on 2017-10-10.
 */

public class NewListActivity extends BaseActivity {

    private static final String TAG = "NewListActivity";
    private static final String REQUIRED = "Required";
    public static String listName = "";
    private DatabaseReference mDatabase;

    private EditText mlistNameField;
    private FloatingActionButton mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        mlistNameField = findViewById(R.id.field_title);
        mSubmitButton = findViewById(R.id.fab_submit_post);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    private void submitPost() {
        listName = mlistNameField.getText().toString();

        // listName is required
        if (TextUtils.isEmpty(listName)) {
            mlistNameField.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewListActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewList(userId, user.username, listName);

                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [E ND_EXCLUDE]
                    }
                });
    }

    private void setEditingEnabled(boolean enabled) {
        mlistNameField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewList(String userId, String username, String listName) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("todo-lists").push().getKey();
        ListItem listItem = new ListItem(userId, username, listName);
        //Add user to access
        listItem.access.put(userId, true);

        Map<String, Object> postValues = listItem.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/todo-lists/" + key, postValues);
        childUpdates.put("/user-lists/" + userId + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);

        //Update the users access table with the new post id.
        DatabaseReference postsRef = mDatabase.child("/users/" + userId + "/" + "access");
        Map<String, Object> hopperUpdates = new HashMap<String, Object>();
        hopperUpdates.put(key, true);
        postsRef.updateChildren(hopperUpdates);





    }








}
