package divideandconquer.zippy.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// Android Studio dynamically generated class,
// created during build process to dynamically identify all assets
// (from strings to android widgets to layouts), for usage in java classes in Android app
import divideandconquer.zippy.R;


import divideandconquer.zippy.models.ListItem;

public class ListViewHolder extends RecyclerView.ViewHolder {


    public TextView nameView;
    public ImageView sharedView;
    public TextView numSharesView;
    public ImageView photoView;

    public ListViewHolder(View itemView) {
        super(itemView);

        nameView = itemView.findViewById(R.id.list_name);
        sharedView = itemView.findViewById(R.id.shared);
        numSharesView = itemView.findViewById(R.id.list_num_of_users);
        photoView = itemView.findViewById(R.id.list_author_photo);
    }


    public void bindToListItem(ListItem listItem) {
        nameView.setText(listItem.listName);
        numSharesView.setText(String.valueOf(listItem.usersCount));
    }
}
