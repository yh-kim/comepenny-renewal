package com.pickth.comepennyrenewal.myinfo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.pickth.comepennyrenewal.idea.IdeaListItem;
import com.pickth.comepennyrenewal.net.service.IdeaService;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kim on 2017-02-22.
 */

public class MyInfoWriteFragement extends MyInfoBaseFragment {
    public static Fragment newInstance() {
        Bundle args = new Bundle();

        MyInfoWriteFragement fragment = new MyInfoWriteFragement();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void getIdeaList() {
        new IdeaService().getIdeaListByUser(userId,offset)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            try {
                                JSONObject jObject = new JSONObject(response.body().string());

                                JSONArray retArr = jObject.getJSONArray("ret");
                                for (int i = 0; i < retArr.length(); i++) {
                                    JSONObject obj = retArr.getJSONObject(i);

                                    int ideaId = obj.getInt("id");
                                    int ideaUserId = obj.getInt("userId");
                                    int boothId = obj.getInt("boothId");
                                    String email = obj.getString("email");
                                    String content = obj.getString("content");
                                    int hit = obj.getInt("hit");
                                    String date = obj.getString("date");
                                    int likeNum = obj.getInt("likeNum");
                                    int commentNum = obj.getInt("commentNum");

                                    // Item 객체로 만들어야함
                                    IdeaListItem item = new IdeaListItem(content, email, boothNames[boothId - 1], hit, commentNum, likeNum, ideaId);
                                    // Item 객체를 ArrayList에 넣는다
                                    arrList.add(item);

                                    // Adapter에게 데이터를 넣었으니 갱신하라고 알려줌
                                    adapter.notifyDataSetChanged();
//                                adapter.notifyItemChanged(i+offset+1);
                                }
                                count = jObject.getInt("cnt");
                                offset += count;
                                isScroll = true;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if(response.code() == 404 && offset == 0) {
                            tvIdeaNotFound.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
}
