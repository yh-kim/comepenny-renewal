package com.pickth.comepennyrenewal.book;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.util.SetFont;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Kim on 2017-02-15.
 */

public class BookAdapter extends ArrayAdapter<BookListItem> {
    //LayoutInflater -> XML을 동적으로 만들 때 필요
    private LayoutInflater inflater = null;
    //Context -> Activity Class의 객체
    private Context contentContext = null;


    public BookAdapter(Context context, int resource, ArrayList<BookListItem> objects) {
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
            convertView = inflater.inflate(R.layout.row_book,null);

            //TextView 폰트 지정
            SetFont.setGlobalFont(contentContext, convertView);

            holder = new ViewHolder();

            //row에 있는 정보들을 holder로 가져옴
            holder.tvBookItemTitle = (TextView) convertView.findViewById(R.id.tv_book_item_title);
            holder.tvBookItemAuthor = (TextView) convertView.findViewById(R.id.tv_book_item_author);
            holder.tvBookItemPublisher = (TextView) convertView.findViewById(R.id.tv_book_item_publisher);
            holder.ivBookItemImage = (ImageView) convertView.findViewById(R.id.iv_book_item_image);
            convertView.setTag(holder);
        }

        holder = (ViewHolder)convertView.getTag();
        BookListItem item = getItem(position);

        holder.tvBookItemTitle.setText(item.getTitle());
        holder.tvBookItemAuthor.setText(item.getAuthor());
        holder.tvBookItemPublisher.setText(item.getPublisher());

        String imgPath = item.getImage().split("\\?")[0];

        Picasso.with(convertView.getContext())
                .load(imgPath)
                .fit()
                .into(holder.ivBookItemImage);

        return convertView;

    }


    class ViewHolder {
        TextView tvBookItemTitle, tvBookItemAuthor, tvBookItemPublisher;
        ImageView ivBookItemImage;
    }
}
