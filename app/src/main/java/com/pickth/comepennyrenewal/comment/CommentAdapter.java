package com.pickth.comepennyrenewal.comment;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.idea.IdeaHeaderViewHolder;
import com.pickth.comepennyrenewal.util.StaticUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Kim on 2017-01-30.
 */

public class CommentAdapter extends RecyclerView.Adapter {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ADAPTEE_OFFSET = 1;
    private ArrayList<CommentItem> arrList;
    private View headerView = null;
    private Intent intent;
    View itemView;
    AdapterView.OnItemLongClickListener itemLongClickListener;

    public CommentAdapter(ArrayList<CommentItem> arrList, Intent intent) {
        this.arrList = arrList;
        this.intent = intent;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER){
            itemView = headerView;
            return new IdeaHeaderViewHolder(itemView, intent);
        }
        else {
            itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.row_comment, parent, false);
            return new CommentViewHolder(itemView);
        }
        // Set Font
//        SetFont.setGlobalFont(parent.getContext(), itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position == 0 && holder.getItemViewType() == TYPE_HEADER && isUseHeader()){
            onBindHeaderViewHolder((IdeaHeaderViewHolder)holder, position);
        }
        else{
            onBindeCommentItemViewHolder((CommentViewHolder)holder, position);
        }

        return;
    }

    private void onBindHeaderViewHolder(IdeaHeaderViewHolder holder, int position){
//        holder.getIdea();
//        holder.initializationContent();
    }

    private void onBindeCommentItemViewHolder(CommentViewHolder holder, int position) {
        position -= isUseHeader()?1:0;
        CommentItem item = arrList.get(position);

        Picasso.with(itemView.getContext())
                .load(StaticUrl.FILE_URL+item.getUserImg())
                .fit()
                .into(holder.img);
        byte[] mailarray = item.getUserEmail().getBytes();
        String email_view = new String(mailarray, 0, 3);
        String hide_email = email_view + "*****";

        holder.userId.setText(hide_email);
        holder.content.setText(item.getContent());
        holder.commentTime.setText(item.getCommentTime());
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && isUseHeader()) {
            return TYPE_HEADER;
        }

        return super.getItemViewType(position) + TYPE_ADAPTEE_OFFSET;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public boolean isUseHeader() {
        return headerView!=null? true : false;
    }

    @Override
    public int getItemCount() {
        int itemCount = getBasicItemCount();
        if(isUseHeader()){
            itemCount++;
        }
        return itemCount;
    }

    public void onItemLongClickListener(AdapterView.OnItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    private void onViewHolderItemLongClick(CommentViewHolder commentHolder) {
        if (itemLongClickListener != null) {
            itemLongClickListener.onItemLongClick(null, commentHolder.itemView,
                    commentHolder.getAdapterPosition(), commentHolder.getItemId());

        }
    }


    public int getBasicItemCount() {
        return arrList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        ImageView img;
        TextView userId, content, commentTime;

        public CommentViewHolder(View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);

            img = (ImageView) itemView.findViewById(R.id.iv_comment_basic);
            userId = (TextView) itemView.findViewById(R.id.tv_comment_userid);
            content = (TextView) itemView.findViewById(R.id.tv_comment);
            commentTime = (TextView) itemView.findViewById(R.id.tv_comment_time);
        }

        @Override
        public boolean onLongClick(View view) {
            if(getItemViewType() != TYPE_HEADER){
                onViewHolderItemLongClick(this);
            }
            return false;
        }
    }
}
