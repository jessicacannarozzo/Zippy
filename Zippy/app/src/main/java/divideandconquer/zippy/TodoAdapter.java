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

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                TodoItem item = dataSnapshot.getValue(TodoItem.class);
                mTodoItemIds.add(dataSnapshot.getKey());
                mTodoItems.add(item);
                notifyItemInserted(mTodoItems.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }

        };
        ref.addChildEventListener(childEventListener);
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

    @Override
    public int getItemCount() {return mTodoItems.size();}

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }

}