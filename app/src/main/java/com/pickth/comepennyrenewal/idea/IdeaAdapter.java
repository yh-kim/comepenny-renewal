package com.pickth.comepennyrenewal.idea;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.util.SetFont;

import java.util.ArrayList;

/**
 * Created by Kim on 2017-01-13.
 */

public class IdeaAdapter extends RecyclerView.Adapter<IdeaAdapter.IdeaViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ADAPTEE_OFFSET = 1;
    View headerView;
    private ArrayList<IdeaListItem> arrLIst;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public IdeaAdapter(ArrayList<IdeaListItem> arrLIst) {
        this.arrLIst = arrLIst;
    }

    @Override
    public IdeaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(viewType == TYPE_HEADER){
            itemView = headerView;
        }else{
            itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.row_idea, parent, false);
        }

        // Set Font
        SetFont.setGlobalFont(parent.getContext(), itemView);

        return new IdeaViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(IdeaViewHolder holder, int position) {
        if(position == 0 && isUseHeader() && holder.getItemViewType() == TYPE_HEADER){
            return;
        }
        
        position -= isUseHeader()?1:0;

        IdeaListItem item = arrLIst.get(position);

        // 컨텐츠 라인수 줄이기
        String contents =item.getContent();
        holder.tvTitle.setText(contents);
        int maxLines = 14;
        holder.tvTitle.setMaxLines(maxLines);
        if (holder.tvTitle.getLineCount() > maxLines){
            int lastCharShown =  holder.tvTitle.getLayout().getLineVisibleEnd(maxLines - 2);
            contents = contents.substring(0, lastCharShown) + "\n …";
        }

        // email 가리기
        byte[] mailarray = item.getEmail().getBytes();
        String email_view = new String(mailarray, 0, 3);
        String hide_email = email_view + "*****";

        holder.tvTitle.setText(contents);
        holder.tvEmail.setText(hide_email);
        holder.tvViewCount.setText(item.getHit() + "");
        holder.tvCommentCount.setText(item.getCommentNum() + "");
        holder.tvLikeCount.setText(item.getLikeNum() + "");
        holder.tvBoothName.setText(item.getBoothName());
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && isUseHeader()){
            return TYPE_HEADER;
        }

        return super.getItemViewType(position) + TYPE_ADAPTEE_OFFSET;
//        return position + TYPE_ADAPTEE_OFFSET;
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
        return arrLIst.size();
    }

    public void onItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(IdeaViewHolder itemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public boolean isUseHeader() {
        return headerView!=null? true : false;
    }


    public class IdeaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        IdeaAdapter adapter;
        public TextView tvTitle, tvEmail, tvViewCount,tvCommentCount, tvLikeCount,  tvBoothName;
        public IdeaViewHolder(View itemView, IdeaAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.adapter = adapter;

            tvTitle = (TextView) itemView.findViewById(R.id.tv_idea);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_email);
            tvViewCount = (TextView) itemView.findViewById(R.id.tv_count_view);
            tvCommentCount = (TextView) itemView.findViewById(R.id.tv_count_comment);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tv_count_like);
            tvBoothName =(TextView)itemView.findViewById(R.id.tv_row_booth_name);
        }

        @Override
        public void onClick(View view) {
            if(getItemViewType() != TYPE_HEADER){
                adapter.onItemHolderClick(this);
            }
        }
    }
}
