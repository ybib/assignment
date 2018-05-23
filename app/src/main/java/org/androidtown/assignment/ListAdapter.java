package org.androidtown.assignment;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    List<Member_list> mMember_list;
    String user_id;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView member;

        public ViewHolder(View itemView) {
            super(itemView);
            member = (TextView) itemView.findViewById(R.id.member);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapter(List<Member_list> mMember_list) {
        this.mMember_list = mMember_list;
        //this.user_id = user_id;  // 로그인한 유저아이디.
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.member_list, parent, false);


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String[] temp = mMember_list.get(position).getMemberlist();
        holder.member.setText(temp[0]);
        //holder.member.setText(mMember_list.get(position).getText());


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mMember_list.size();
    }
}
