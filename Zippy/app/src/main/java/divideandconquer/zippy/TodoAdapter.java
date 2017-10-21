package divideandconquer.zippy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import divideandconquer.zippy.models.TodoItem;

/**
 * Created by navi on 21/10/17.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoDetailActivity.TodoViewHolder> {

    private static final String TAG = "TodoDetailActivity";
    private Context mContext;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private List<String> mTodoItemIds = new ArrayList<>();
    private List<TodoItem> mTodoItems = new ArrayList<>();

    public TodoAdapter(final Context context, DatabaseReference ref) {
        mContext = context;
        mDatabaseReference = ref;

        // Create child event listener
        // [START child_event_listener_recycler]
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                TodoItem comment = dataSnapshot.getValue(TodoItem.class);

                // [START_EXCLUDE]
                // Update RecyclerView
                mTodoItemIds.add(dataSnapshot.getKey());
                mTodoItems.add(comment);
                notifyItemInserted(mTodoItems.size() - 1);
                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                TodoItem newComment = dataSnapshot.getValue(TodoItem.class);
                String commentKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int commentIndex = mTodoItemIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    // Replace with the new data
                    mTodoItems.set(commentIndex, newComment);

                    // Update the RecyclerView
                    notifyItemChanged(commentIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int commentIndex = mTodoItemIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    // Remove data from the list
                    mTodoItemIds.remove(commentIndex);
                    mTodoItems.remove(commentIndex);

                    // Update the RecyclerView
                    notifyItemRemoved(commentIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                TodoItem movedComment = dataSnapshot.getValue(TodoItem.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());

            }

        };
        ref.addChildEventListener(childEventListener);
        // [END child_event_listener_recycler]

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener;
    }

    @Override
    public TodoDetailActivity.TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.include_todo_name, parent, false);
        return new TodoDetailActivity.TodoViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final TodoDetailActivity.TodoViewHolder viewHolder, int position) {

        TodoItem todoItem = mTodoItems.get(position);
        viewHolder.itemNameView.setText(todoItem.item);
        viewHolder.checkboxView.setChecked(todoItem.checked);
    }

//        private void onCheckedClicked(DatabaseReference postRef) {
//            postRef.runTransaction(new Transaction.Handler() {
//                @Override
//                public Transaction.Result doTransaction(MutableData mutableData) {
//                    TodoItem p = mutableData.getValue(TodoItem.class);
//                    if (p == null) {
//                        return Transaction.success(mutableData);
//                    }
//                    p.setChecked(!p.checked);
//
//
//                    // Set value and report transaction success
//                    mutableData.setValue(p);
//                    return Transaction.success(mutableData);
//                }
//
//                @Override
//                public void onComplete(DatabaseError databaseError, boolean b,
//                                       DataSnapshot dataSnapshot) {
//                    // Transaction completed
//                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
//                }
//            });
//        }


    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }



}