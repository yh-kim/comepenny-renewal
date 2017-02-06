package com.pickth.comepennyrenewal.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.dto.CommentItem;

import java.util.ArrayList;

/**
 * Created by Kim on 2017-01-30.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ADAPTEE_OFFSET = 1;
    private ArrayList<CommentItem> arrList;
    private View headerView;

    public CommentAdapter(ArrayList<CommentItem> arrList) {
        this.arrList = arrList;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(viewType == TYPE_HEADER){
            itemView = headerView;
        }
        else {
            itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.row_comment, parent, false);
        }
        // Set Font
//        SetFont.setGlobalFont(parent.getContext(), itemView);

        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        if(position == 0 && holder.getItemViewType() == TYPE_HEADER && isUseHeader()){
            return;
        }
        position -= isUseHeader()?1:0;
        CommentItem item = arrList.get(position);

        byte[] mailarray = item.getUserEmail().getBytes();
        String email_view = new String(mailarray, 0, 3);
        String hide_email = email_view + "*****";

        holder.userId.setText(hide_email);
        holder.content.setText(item.getContent());
        holder.commentTime.setText(item.getCommentTime());

//
//        if (!item.getUserImg().contains("null")) {
//            loader.displayImage("https://s3-ap-northeast-1.amazonaws.com/comepenny/" + item.getUser_comment_img(), holder.img);
//        }
//        else{
//            loader.displayImage("https://s3-ap-northeast-1.amazonaws.com/comepenny/myinfo_userimage.png", holder.img);
//        }
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

    public int getBasicItemCount() {
        return arrList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView userId, content, commentTime;

        public CommentViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.iv_comment_basic);
            userId = (TextView) itemView.findViewById(R.id.tv_comment_userid);
            content = (TextView) itemView.findViewById(R.id.tv_comment);
            commentTime = (TextView) itemView.findViewById(R.id.tv_comment_time);
        }
    }
}
