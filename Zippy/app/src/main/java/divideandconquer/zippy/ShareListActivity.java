package divideandconquer.zippy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
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
import java.util.Map;
import java.util.Set;

import divideandconquer.zippy.models.ListItem;
import divideandconquer.zippy.models.User;

/**
 * Created by geoff on 2017-10-10.
 */

public class ShareListActivity extends BaseActivity {

    private static final String TAG = "ShareListActivity";
    private static final String REQUIRED = "Required";

    public static final String EXTRA_POST_KEY = "post_key";
    private String listKey;
    private EditText targetField;

    private FloatingActionButton mSubmitButton;

    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);

        listKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (listKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

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
        final Context context = this;

        // Disable button so there are no multi-posts
        setEditingEnabled(false);

        ref = FirebaseDatabase.getInstance().getReference();

        //check if it is the same email as the current user
        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(targetEmail)) {
            Toast.makeText(getApplicationContext(), "You already have access", Toast.LENGTH_SHORT).show();

        } else if (isTargetValid(targetField)) { //Check if the email entered is legit
            Toast.makeText(this, "Sharing...", Toast.LENGTH_SHORT).show();


            //Get the user
            //Get the ListItem
            //Update the access and count
            //Update the share-list for the shared user
            //Update the user-list table for the owners uid
            //Update all the users in the shared-list uids
            final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
            //Limit the ammount of occurences to one, so if they don't exist it should be null
            userRef.orderByChild("email").equalTo(targetEmail).limitToFirst(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //This will return null if no one is found.
                    if (dataSnapshot.getValue() == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Person not found.", Toast.LENGTH_SHORT).show(); //if it gets here without finish() being called
                            }
                        });

                    } else {
                        //Get the first User that is why it is limitToFirst to the first occurence
                        DataSnapshot dUser = dataSnapshot.getChildren().iterator().next();
                        User targetUser = dUser.getValue(User.class);
                        final String targetID = dUser.getKey();
                        if (targetUser.email.equals(targetEmail)) { //found target user
                            Log.i("Share List Email:", targetUser.email);

                            final DatabaseReference listRef = ref.child("shared").child(targetID).child(listKey);

                            //Listen for the orginal list a single time
                            listRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //Get the original list

                                    if (dataSnapshot.getValue() != null) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "User Already has access.", Toast.LENGTH_SHORT).show(); //if it gets here without finish() being called
                                            }
                                        });
                                    }  else {

                                        ref.child("todo-lists").child(listKey).runTransaction(new Transaction.Handler() {
                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                ListItem updatedList = mutableData.getValue(ListItem.class);

                                                //List Item is null means it doesn't exist
                                                if (updatedList == null) {
                                                    return Transaction.success(mutableData);
                                                }

                                                updatedList.usersCount++; //increment usersCount
                                                updatedList.access.put(targetID, true);

                                                listRef.setValue(true);

                                                //Add listID -> access -> targetID: true and report success
                                                mutableData.setValue(updatedList);
                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                                //transaction completed
                                                Log.i("Updated List: ", dataSnapshot.toString());
                                                Log.d(TAG, "postTransaction:onComplete:" + databaseError);

                                                finish();
                                                setEditingEnabled(true);

                                            }


                                        });






//
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d(TAG, "listRef.addListenerForSingleValueEvent:onCancelled:" + databaseError);

                                }
                            });

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "userRef:onCancelled:" + databaseError);

                }
            });

        }
        setEditingEnabled(true);
    }

    //check if target email is valid (if it's in the DB)
    boolean isTargetValid(EditText targetField) {


        //check if field is empty
        if (targetField.getText().toString().trim().length() == 0) {
            targetField.setError("Error.");
            setEditingEnabled(true);
            return false;
        } else { //check if it is an email
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
