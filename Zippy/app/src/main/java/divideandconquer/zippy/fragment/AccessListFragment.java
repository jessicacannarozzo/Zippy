package divideandconquer.zippy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import divideandconquer.zippy.R;

/**
 * Created by navi on 20/11/17.
 */


public class AccessListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.access_list_fragment, container, false);
        return view;
    }

    private void getUsers() {
        DatabaseReference userList = FirebaseDatabase.getInstance().getReference("todo-lists");
    }
}
