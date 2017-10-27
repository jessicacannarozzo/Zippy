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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import divideandconquer.zippy.models.ListItem;
import divideandconquer.zippy.models.User;

/**
 * Created by geoff on 2017-10-10.
 */

public class ShareListActivity extends BaseActivity {

    private static final String TAG = "ShareListActivity";
    private static final String REQUIRED = "Required";

    public static final String EXTRA_POST_KEY = "post_key";
    private String mTodoKey;

    private DatabaseReference mDatabase;

    private EditText mListNameField;

    private FloatingActionButton mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);

        mTodoKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mTodoKey == null) {
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
        String targetPerson = findViewById(R.id.targetEmail).toString(); //get target person's email
        final String listName = mListNameField.getText().toString(); //get

        // listName is required
        if (TextUtils.isEmpty(listName)) {
            mListNameField.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Sharing...", Toast.LENGTH_SHORT).show();

        DatabaseReference todoListRef = mDatabase.child("todo-list").child(mTodoKey);
        DatabaseReference userRef = mDatabase.child("users");

        //reference: https://firebase.google.com/docs/database/android/lists-of-data

        //check if person is in our DB
        if (isTargetValid(targetPerson)) {
            int targetUID = getTargetUID(targetPerson); //get target's UID

            //add to access array in DB
            //list -> access.push(targetUID)
            //targetUID->access.push(list ID)
            

        } else { //they're not in our DB
            Toast.makeText(this, "Person not found.", Toast.LENGTH_SHORT);
        }

    }


    //check if target email is valid (if it's in the DB)
    boolean isTargetValid(String email) {
        DatabaseReference ref = mDatabase.child("users");



        return false; //will change
    }


    //get shared target's UID via email entered
    //return: target UID
    int getTargetUID(String email) {
        return 0;
    }

    private void setEditingEnabled(boolean enabled) {
        mListNameField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }


}
