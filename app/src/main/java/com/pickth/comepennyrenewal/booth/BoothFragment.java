package com.pickth.comepennyrenewal.booth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.net.service.BoothService;
import com.pickth.comepennyrenewal.util.SetFont;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kim on 2017-01-13.
 */

public class BoothFragment extends Fragment {
    View rootView;
    BoothAdapter adapter;
    ArrayList<BoothListItem> arrList = new ArrayList<BoothListItem>();

    @BindView(R.id.gv_main_booth)
    GridView gvMainBooth;


    @OnItemClick(R.id.gv_main_booth) void click(View v, int position){
        Intent itBoothDetail = new Intent(rootView.getContext(), BoothDetailActivity.class);
        itBoothDetail.putExtra("boothId", arrList.get(position).getId());
        itBoothDetail.putExtra("boothName", arrList.get(position).getName());
        startActivity(itBoothDetail);
        getActivity().overridePendingTransition(0,0);
    }

    public static Fragment newInstance() {
        Bundle args = new Bundle();

        BoothFragment fragment = new BoothFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_booth, container, false);
        ButterKnife.bind(this, rootView);

        // frgment 폰트 설정
        SetFont.setGlobalFont(rootView.getContext(), rootView);

        // Connect adapter
       {
           adapter = new BoothAdapter(rootView.getContext(), R.layout.row_booth, arrList);
           gvMainBooth.setAdapter(adapter);
        }

        {
            getBooths();
        }

        return rootView;
    }

    private void getBooths(){
        BoothService boothService = new BoothService();
        boothService.getBoothList().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jObject = new JSONObject(response.body().string());

                        JSONArray retArr = jObject.getJSONArray("ret");
                        for (int i = 0; i < retArr.length(); i++) {
                            JSONObject obj = retArr.getJSONObject(i);

                            int boothId = obj.getInt("id");
                            int ideaNum = obj.getInt("ideaNum");
                            int likeNum = obj.getInt("likeNum");
                            String imgUrl = boothId + "";
                            String boothName = obj.getString("name");

                            // Item 객체로 만들어야함
                            BoothListItem item = new BoothListItem(imgUrl, boothName, boothId, ideaNum, likeNum);
                            // Item 객체를 ArrayList에 넣는다
                            arrList.add(item);

                            // Adapter에게 데이터를 넣었으니 갱신하라고 알려줌
                            adapter.notifyDataSetChanged();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
