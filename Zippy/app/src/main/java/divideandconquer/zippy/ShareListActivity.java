package divideandconquer.zippy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import divideandconquer.zippy.models.ListItem;
import divideandconquer.zippy.models.User;

/**
 * Created by geoff on 2017-10-10.
 */

public class ShareListActivity extends BaseActivity {

    private static final String TAG = "ShareListActivity";
    private static final String REQUIRED = "Required";

    public static final String EXTRA_POST_KEY = "post_key";
    private String listID;

    private DatabaseReference mDatabase;

    private EditText targetField;

    private FloatingActionButton mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);

        listID = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (listID == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        mSubmitButton = findViewById(R.id.fab_submit_share_list);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSharedList();
            }
        });
    }

    private void submitSharedList() {
        targetField = (EditText) findViewById(R.id.targetEmail); //get email field
        final String targetEmail = targetField.getText().toString(); //get target person's email

        // Disable button so there are no multi-posts
        setEditingEnabled(false);


//        DatabaseReference todoListRef = mDatabase.child("todo-list").child(mTodoKey);

        //check if person is in our DB
        if (isTargetValid(targetField)) {
            Toast.makeText(this, "Sharing...", Toast.LENGTH_SHORT).show();

            //add to access array in DB
            //list -> access.push(targetUID)
            //targetUID->access.push(list ID)
            //increment usersCount
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
            userRef.orderByChild("email").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User targetUser = dataSnapshot.getValue(User.class);
                    if (targetUser.email.equals(targetEmail)) {
                        Log.i("Share List Email:", targetUser.email);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });






        } else { //they're not in our DB
            Toast.makeText(this, "Person not found.", Toast.LENGTH_SHORT);
        }

    }


    //check if target email is valid (if it's in the DB)
    boolean isTargetValid(EditText targetField) {
        DatabaseReference ref = mDatabase.child("users");

        //check if field is empty
        if (targetField.getText().toString().trim().length() == 0) {
            targetField.setError("Error.");
            setEditingEnabled(true);
            return false;
        } else { //check if target is in the DB
            ref.orderByChild("email").equalTo(targetField.getText().toString());
            return true;
        }
    }

    private void setEditingEnabled(boolean enabled) {
        targetField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }


}
