package divideandconquer.zippy;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
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

    private void updateSharedLists(ListItem updatedList) {
        for (String account : updatedList.access.keySet()) {
            DatabaseReference listRef = FirebaseDatabase.getInstance().getReference("shared-lists").child(account);


        }
    }

    private void submitSharedList() {
        targetField = (EditText) findViewById(R.id.targetEmail); //get email field
        final String targetEmail = targetField.getText().toString(); //get target person's email
        final Context context = this;

        // Disable button so there are no multi-posts
        setEditingEnabled(false);

        //check if email was entered
        if (isTargetValid(targetField)) {
            Toast.makeText(this, "Sharing...", Toast.LENGTH_SHORT).show();

            //add to access array in DB
            //list -> access.push(targetUID)
            //targetUID->access.push(list ID)
            //increment usersCount
            final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
            userRef.orderByChild("email").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User targetUser = dataSnapshot.getValue(User.class);
                    if (targetUser.email.equals(targetEmail)) { //found target user
//                        Log.i("Share List Email:", targetUser.email);
//                        Log.i("Parent: ", dataSnapshot.getKey().toString()); target UID!!!
                        final String targetID = dataSnapshot.getKey().toString();

                        //add list access to user
                        targetUser.access.put(listKey, true); //add list to User's accessible lists
                        userRef.child(targetID).setValue(targetUser); //update user in DB

                        //add user access to list and update Userscount
                        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference("todo-lists").child(listKey);
                        listRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                ListItem updatedList = mutableData.getValue(ListItem.class);

                                if (updatedList == null) return Transaction.success(mutableData); //something went wrong, but we'll still safely exit this

                                updatedList.usersCount++; //increment usersCount
                                updatedList.access.put(targetID, true);


                                //add to shared lists
                                DatabaseReference sharedLists= FirebaseDatabase.getInstance().getReference("shared-Lists");
                                sharedLists.child(targetID).child(listKey).setValue(updatedList);

                                //Remove users key from keyset
                                Set<String> usersKeySet = updatedList.access.keySet();
                                usersKeySet.remove(updatedList.uid);

                                //Update the rest of the shared-accounts
                                for (String userKey : usersKeySet) {
                                    sharedLists.child(userKey).child(listKey).setValue(updatedList);
                                }

                                //update the owners user-list
                                DatabaseReference ownerUserRef = FirebaseDatabase.getInstance().getReference("user-lists").child(updatedList.uid);
                                ownerUserRef.child(listKey).setValue(updatedList);


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
                            }
                        });
                    }
                    Toast.makeText(context, "Person not found.", Toast.LENGTH_SHORT); //if it gets here without finish() being called
                }

                //do not remove or face the wrath of
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            setEditingEnabled(true);

        } else { //they're not in our DB
            Toast.makeText(context, "Invalid input.", Toast.LENGTH_SHORT);
        }

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
