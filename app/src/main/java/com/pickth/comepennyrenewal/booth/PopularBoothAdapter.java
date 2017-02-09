package com.pickth.comepennyrenewal.booth;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.util.StaticUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Kim on 2017-02-01.
 */

public class PopularBoothAdapter extends RecyclerView.Adapter<PopularBoothAdapter.PopularBoothViewHolder> {
    private ArrayList<BoothListItem> arrList = null;
    private AdapterView.OnItemClickListener mOnItemClickListener = null;
    private Context context = null;

    public PopularBoothAdapter(Context context, ArrayList<BoothListItem> arrList) {
        this.context = context;
        this.arrList = arrList;
    }

    @Override
    public PopularBoothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.row_popular_booth, parent, false);
        return new PopularBoothViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(PopularBoothViewHolder holder, int position) {
        BoothListItem item = arrList.get(position);

        holder.tvPopularBooth.setText(item.getName());

        Picasso.with(context)
                .load(StaticUrl.FILE_URL+item.getImgUrl())
                .fit()
                .into(holder.ivPopularBooth);

    }

    @Override
    public int getItemCount() {
        return arrList.size();
    }

    public void onItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void onItemHolderClick(PopularBoothViewHolder itemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    public class PopularBoothViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        PopularBoothAdapter adapter;
        TextView tvPopularBooth;
        ImageView ivPopularBooth;
        public PopularBoothViewHolder(View itemView, PopularBoothAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.adapter = adapter;

            tvPopularBooth = (TextView)itemView.findViewById(R.id.tv_popular_booth);
            ivPopularBooth = (ImageView)itemView.findViewById(R.id.iv_popular_booth);
        }

        @Override
        public void onClick(View view) {
            adapter.onItemHolderClick(this);
        }
    }
}
