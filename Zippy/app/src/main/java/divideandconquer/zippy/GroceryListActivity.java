package divideandconquer.zippy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import divideandconquer.zippy.models.Game;
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

    //Database reference
    private DatabaseReference mGroceryListReference;
    private DatabaseReference mGameDatabaseReference;
    private DatabaseReference mGroceryItemReference;


    private ValueEventListener mTodoListener;
    private GroceryListAdapter mAdapter;

    //Do not use this field for data that must be accurate
    private ListItem listItem;
    //UI Fields
    private TextView mGroceryItemField;
    private Button mNewGroceryItemButton;
    private RecyclerView mTodoRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list_detail);

        mTodoKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mTodoKey == null) {
        }

        // Initialize Database
        mGroceryListReference = FirebaseDatabase.getInstance().getReference()
                .child("/todo-lists/").child(mTodoKey);
        mGroceryItemReference = FirebaseDatabase.getInstance().getReference()
                .child("todo-items").child(mTodoKey);
        mGameDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("games").child(mTodoKey);


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
                if(post != null) {
                    //set list title:
                    TextView title = (TextView) findViewById(R.id.list_name);
                    title.setText(post.listName);

                    //Update the post and update the menu options
                    listItem = post;
                    invalidateOptionsMenu();
                } else {
                    finish();
                }


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

        // these should all be private
        private CheckBox checkboxView;
        private ImageView thumbsDownView;
        private ImageView photoView;
        private TextView itemNameView;
        private EditText editItemNameView;
        private GroceryItem groceryItem;
        private String groceryItemId;
        private Button mRemoveGroceryItemButton;
        private DatabaseReference mDatabaseReference;


        // Refactor: simplifying reference to adapter, useful when removing an item
        public GroceryItemViewHolder(View itemView, final GroceryListAdapter groceryListAdapter) {
            super(itemView);

            checkboxView = itemView.findViewById(R.id.todo_checkbox);
            thumbsDownView = itemView.findViewById(R.id.thumbs_down);

            //Set thumbs down image
            TextDrawable thumbsDown = TextDrawable.builder()
                    .buildRect("\uD83D\uDC4E", Color.TRANSPARENT);
            this.thumbsDownView.setImageDrawable(thumbsDown);

            // itemNameView and editItemNameView are different states of the same item text instance, necessary for switching between view/edit
            itemNameView = itemView.findViewById(R.id.todo_item_name);
            editItemNameView = itemView.findViewById(R.id.todo_edit_item_name);
            mRemoveGroceryItemButton = itemView.findViewById(R.id.button_todo_rem_item);
            photoView = itemView.findViewById(R.id.todo_author_photo);


            // checkbox click cycle:
            // unchecked -> checked -> thumbs down -> unchecked...

            //on checkbox checked:
            checkboxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    boolean changed = false;
                    boolean checkedOwnBox = false;
                    Integer originalState = groceryItem.state;
                    String originalUid = null;
                    if(groceryItem.checkedUid != null) {
                        originalUid = groceryItem.checkedUid.toString();
                    }
                    if (groceryItem != null) {
                        if (groceryItem.state == 0 && isChecked) {
                            groceryItem.state = 1;
                            changed = true;
                        } else if (groceryItem.state == 1 && !isChecked) {
                            groceryItem.state = 2;
                            groceryItem.checkedUid = "";
                            checkboxView.setVisibility(View.GONE);
                            thumbsDownView.setVisibility(View.VISIBLE);
                            changed = true;
                        }
                        groceryItem.checkedUid = FirebaseAuth.getInstance().getUid();
                    }

                    Log.i("STATE","Check-box clicked. State: "+ groceryItem.state);

                    if (changed) {
                        mDatabaseReference.child(groceryItemId).setValue(groceryItem);

                        updateGameScore(originalState, originalUid);
                    }
                }
            });

            //on item clicked for edition:
            thumbsDownView.setOnClickListener(new android.view.View.OnClickListener(){

                @Override
                public void onClick(View v) {
//                    Log.d("thumbs-dwn-CLICK","state: "+ this.groceryItem.state);

                    // when user clicks on an item, we make the editing box visible, and hide the textview
                    thumbsDownView.setVisibility(View.GONE);
                    checkboxView.setChecked(false);
                    checkboxView.setVisibility(View.VISIBLE);

                    groceryItem.state = 0;
                    mDatabaseReference.child(groceryItemId).setValue(groceryItem);
                    Log.i("STATE","thumbs-down clicked. State: "+ groceryItem.state);
                }
            });

            //on item clicked for edition:
            itemNameView.setOnClickListener(new android.view.View.OnClickListener(){

                @Override
                public void onClick(View v) {
//                    Log.d("TEXTBOX-CLICK","Item text: "+ editItemNameView.getText() + "!!!");

                    // when user clicks on an item, we make the editing box visible, and hide the textview
                    itemNameView.setVisibility(View.GONE);
                    editItemNameView.setVisibility(View.VISIBLE);
                }
            });

            // The item text edition is saved and updates firebase whenever user changes focus or confirms on keyboard
            // changing focus handler:
            editItemNameView.setOnFocusChangeListener(
                    new android.view.View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean onFocus) {
                            if (!onFocus) {
                                userUpdatedItem(groceryListAdapter);
                            }
                        }
                    }
            );

            // keyboard text editing confirmation handler:
            editItemNameView.setOnEditorActionListener(
                    new TextView.OnEditorActionListener() {

                        @Override
                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                            // listening for check confirmation button on keyboard is clicked
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                userUpdatedItem(groceryListAdapter);
                                return true;

                            } else {
                                return false;
                            }

                        }
                    }
            );

            mRemoveGroceryItemButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
//                    Log.d("REMOVE-ITEM","Item to be removed: "+ itemNameView.getText());
                    removeGroceryItem();
                }
            });



        }

        private void updateGameScore(final Integer originalState, final String originalUid) {
            final DatabaseReference mGameReference = FirebaseDatabase.getInstance().getReference()
                    .child("games").child(mDatabaseReference.getKey()).child("scores");


            mGameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Map<String, Object> scores = (Map<String, Object>) dataSnapshot.getValue();
                    if (scores != null) {
                        Long myScore;
                        //If you check
                        if(originalState == 0 && groceryItem.state == 1) {
                            myScore = (Long) scores.get(groceryItem.checkedUid);
                            if(myScore == null) {
                                myScore = (long) 0;
                            }
                            myScore++;
                            scores.put(groceryItem.checkedUid, myScore);
                        } else if(originalState == 1 && groceryItem.state == 2) { //If you uncheck
                            myScore = (Long) scores.get(originalUid);
                            if(myScore == null) {
                                myScore = (long) 1;
                            }
                            myScore--;
                            scores.put(originalUid, myScore);
                        }
                        mGameReference.setValue(scores);

                        final Map<String, Object> scoresFinal = scores;
                        //Get Score
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long count = 0;
                                for(Object x : scoresFinal.values()) {
                                    count += (long) x;
                                }
                                long childrenCount = dataSnapshot.getChildrenCount();
                                if(childrenCount == count) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("games").child(mDatabaseReference.getKey()).child("active").setValue(false);
                                    Log.i("STATE","EQUA: "+ childrenCount);

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });




                    } else {
                        Map<String, Integer> newScores = new HashMap<String, Integer>();

                        if(originalState == 0 && groceryItem.state == 1) {
                            newScores.put(groceryItem.checkedUid, 1);
                        } else if(originalState == 1 && groceryItem.state == 2) { //If you uncheck
                            newScores.put(originalUid, 0);
                        }
                        mGameReference.setValue(newScores);
                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }



        private void userUpdatedItem(GroceryListAdapter groceryListAdapter) {
            // when updating an item text, we switch back the visibilities
            itemNameView.setVisibility(View.VISIBLE);
            editItemNameView.setVisibility(View.GONE);

            String newText = this.editItemNameView.getText().toString();

            if (!newText.isEmpty()) {
                // and update the text view with whatever the user typed before
                updateText(newText);

                if (this.checkboxView.getVisibility() == View.VISIBLE) {
                    if (!this.checkboxView.isChecked()) {
                        updateCheckBox(0);
                    } else {
                        updateCheckBox(1);
                    }
                } else {
                    updateCheckBox(2);
                }
            }
            else{
                // if the string is empty, we remove the item from the list
                removeGroceryItem();
            }

        }

        private void updateText(String text) {

            // we only need to update if the item text result on our app doesn't match the one on firebase
            if (this.groceryItem != null && !this.groceryItem.item.equals(text)) {
                this.groceryItem.item = text;
                this.itemNameView.setText(text);
                this.editItemNameView.setText(text);

                mDatabaseReference.child(groceryItemId).setValue(groceryItem);
            }
        }

        // not ideal to make this public, but useful for reset feature
        public void updateCheckBox(int state) {
            // states: 0= unchecked, 1= checked, 2= not found (out of stock)

            // we only need to update if the check box result on our app doesn't match the one on firebase
            if (this.groceryItem != null && this.groceryItem.state != state) {
                this.groceryItem.state = state;

                switch(state){
                    case 0:
                        this.thumbsDownView.setVisibility(View.GONE);
                        this.checkboxView.setVisibility(View.VISIBLE);
                        this.checkboxView.setChecked(false);
                        break;

                    case 1:
                        this.thumbsDownView.setVisibility(View.GONE);
                        this.checkboxView.setVisibility(View.VISIBLE);
                        this.checkboxView.setChecked(true);
                        break;

                    default: //case 2 = out of stock
                        this.checkboxView.setVisibility(View.GONE);
                        this.thumbsDownView.setVisibility(View.VISIBLE);

//                        //Set thumbs down image
//                        TextDrawable thumbsDown = TextDrawable.builder()
//                                .buildRect("\uE421", Color.WHITE);
//                        this.thumbsDownView.setImageDrawable(thumbsDown);
                        break;
                }

                mDatabaseReference.child(groceryItemId).setValue(groceryItem);
            }
        }

        //refer to GroceryListAdapter.java > onBindViewHolder to see where we receive item
        public void setGroceryItem(GroceryItem item, String id, DatabaseReference ref) {
            this.mDatabaseReference = ref;
            groceryItem = item;
            groceryItemId = id;
            this.itemNameView.setText(item.item);
            //this.checkboxView.setChecked(item.state);
            this.editItemNameView.setText(item.item);

            switch(item.state){
                case 0:
                    this.thumbsDownView.setVisibility(View.GONE);
                    this.checkboxView.setVisibility(View.VISIBLE);
                    this.checkboxView.setChecked(false);
                    break;

                case 1:
                    this.thumbsDownView.setVisibility(View.GONE);
                    this.checkboxView.setVisibility(View.VISIBLE);
                    this.checkboxView.setChecked(true);
                    break;

                default: //case 2 = out of stock
                    this.checkboxView.setVisibility(View.GONE);
                    this.thumbsDownView.setVisibility(View.VISIBLE);

                    break;
            }

            //Set the profile image
            String name = this.groceryItem.author.substring(0,1).toUpperCase();
            if (name.length() == 1) {
                int color = UserProfileColorService.getInstance().getGenerator().getColor(this.groceryItem.uid);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(name, color);
                this.photoView.setImageDrawable(drawable);
            }
        }

        private void removeGroceryItem() {
            // remove item from database
            mDatabaseReference.child(this.groceryItemId).removeValue();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //Dont show the menu item if it's not owned by the user
        if (listItem.uid.equals(FirebaseAuth.getInstance().getUid())) {
            menu.findItem(R.id.delete_list).setVisible(true);
        } else {
            menu.findItem(R.id.delete_list).setVisible(false);
        }

        return true;
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

        } else if (i == R.id.reset_list){
//           Log.i("RESET", "reset clicked!");
            mAdapter.resetList();
            return true;
        } else if (i == R.id.delete_list) {
            deleteList();
            finish();
            return true;
        } else if (i == R.id.game_mode) {
            Intent intent = new Intent(this, GameModeActivity.class);
            intent.putExtra(GroceryListActivity.EXTRA_POST_KEY, getIntent().getStringExtra(EXTRA_POST_KEY));
            startActivity(intent);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void deleteList() {

        //Run transaction to delete the list from all areas
        mGroceryListReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ListItem updatedList = mutableData.getValue(ListItem.class);

                if (updatedList == null) {
                    return Transaction.success(mutableData);
                }

                //Get database references
                DatabaseReference sharedLists = FirebaseDatabase.getInstance().getReference("shared");
                DatabaseReference ownedLists = FirebaseDatabase.getInstance().getReference("owned");
                DatabaseReference todoItems = FirebaseDatabase.getInstance().getReference("todo-items");

                //delete todoItems
                todoItems.child(mTodoKey).removeValue();

                //Delete access for the shared keys
                for (String userKey : updatedList.access.keySet()) {
                    sharedLists.child(userKey).child(mTodoKey).removeValue();
                }

                //Delete the owner from owned table
                ownedLists.child(updatedList.uid).child(mTodoKey).removeValue();

                mutableData.setValue(updatedList);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.i("Deleted List: ", dataSnapshot.toString());
                Log.d(TAG, "groceryList:onComplete:" + databaseError);

            }
        });

        mGroceryListReference.removeValue();
    }
}