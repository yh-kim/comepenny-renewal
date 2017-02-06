package com.pickth.comepennyrenewal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.activity.WriteBoothSelectActivity;
import com.pickth.comepennyrenewal.dto.BoothListItem;
import com.pickth.comepennyrenewal.util.SetFont;
import com.pickth.comepennyrenewal.util.StaticUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Kim on 2017-01-13.
 */

public class BoothAdapter extends ArrayAdapter<BoothListItem> {
    //LayoutInflater -> XML을 동적으로 만들 때 필요
    private LayoutInflater inflater = null;
    //Context -> Activity Class의 객체
    private Context contentContext = null;


    public BoothAdapter(Context context, int resource, ArrayList<BoothListItem> objects) {
        super(context, resource, objects);

        //context는 함수를 호출한 activiy
        //resource는 row_xxx.xml 의 정보
        this.contentContext = context;
        this.inflater = LayoutInflater.from(context);
    }


    //ArrayList에 저장되어있는 데이터를 fragment에 넣는 method
    //List 하나마다 getView가 한번 실행된다
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //position -> List번호
        ViewHolder holder;

        //XML 파일이 비어있는 상태라면
        if(convertView == null){
            //layout 설정
            convertView = inflater.inflate(R.layout.row_booth,null);

            //TextView 폰트 지정
            SetFont.setGlobalFont(contentContext, convertView);

            holder = new ViewHolder();

            //row에 있는 정보들을 holder로 가져옴
            holder.rowBoothBox = (LinearLayout) convertView.findViewById(R.id.row_booth_box);
            holder.ivImg = (ImageView) convertView.findViewById(R.id.img_main_company);
            holder.tvLikeNum = (TextView) convertView.findViewById(R.id.txt_boothmain_like);
            holder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        }

        holder = (ViewHolder)convertView.getTag();

        // 쓰는 창이라면
        if(contentContext instanceof WriteBoothSelectActivity){
            holder.rowBoothBox.setVisibility(View.INVISIBLE);
        }

        BoothListItem item = getItem(position);

        holder.tvName.setText(item.getName());
        holder.tvLikeNum.setText(item.getLikeNum() + "");

        Picasso.with(convertView.getContext())
                .load(StaticUrl.FILE_URL+item.getImgUrl())
                .fit()
                .into(holder.ivImg);

        return convertView;

    }


    class ViewHolder {
        LinearLayout rowBoothBox;
        ImageView ivImg;
        TextView tvLikeNum, tvIdeaNum, tvName;

    }
}
