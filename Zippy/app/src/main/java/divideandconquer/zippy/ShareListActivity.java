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

        mListNameField = findViewById(R.id.share_list_title);
        mSubmitButton = findViewById(R.id.fab_submit_share_list);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSharedList();
            }
        });
    }


    private void submitSharedList() {
        final String listName = mListNameField.getText().toString();

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



        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User p = mutableData.getValue(User.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                p.access.put(mTodoKey, true);
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });

        todoListRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ListItem p = mutableData.getValue(ListItem.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
//                p.access.put("7SrRbdVQMZX22T2wDpOTLslIClF2", true);

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                finish();
            }
        });



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
