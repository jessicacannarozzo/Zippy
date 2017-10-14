package divideandconquer.zippy.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import divideandconquer.zippy.R;


import divideandconquer.zippy.models.ListItem;

public class ListViewHolder extends RecyclerView.ViewHolder {


    public TextView nameView;
    public ImageView sharedView;
    public TextView numSharesView;

    public ListViewHolder(View itemView) {
        super(itemView);

        nameView = itemView.findViewById(R.id.list_name);
        sharedView = itemView.findViewById(R.id.shared);
        numSharesView = itemView.findViewById(R.id.list_num_of_users);
    }

    public void bindToListItem(ListItem listItem) {
        nameView.setText(listItem.listName);
        numSharesView.setText(String.valueOf(listItem.access.size()));
    }
}
