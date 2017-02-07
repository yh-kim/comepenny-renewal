package com.pickth.comepennyrenewal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.activity.BoothDetailActivity;
import com.pickth.comepennyrenewal.activity.IdeaDetailActivity;
import com.pickth.comepennyrenewal.activity.WriteBoothSelectActivity;
import com.pickth.comepennyrenewal.adapter.IdeaAdapter;
import com.pickth.comepennyrenewal.adapter.PopularBoothAdapter;
import com.pickth.comepennyrenewal.dto.BoothListItem;
import com.pickth.comepennyrenewal.dto.IdeaListItem;
import com.pickth.comepennyrenewal.net.service.IdeaService;
import com.pickth.comepennyrenewal.util.SetFont;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kim on 2017-01-13.
 */

public class IdeaFragment extends Fragment {
    private boolean isScroll = false;
    private int count = 6;
    private int offset = 0;
    int selectedItem = 0;

    IdeaService ideaService = new IdeaService();

    private IdeaAdapter adapter;
    private LinearLayoutManager rvLayoutManager;
    private ArrayList<IdeaListItem> arrList = new ArrayList<IdeaListItem>(  );
    private ArrayList<BoothListItem> arrListBooth = new ArrayList<>();

    // view
    private View rootView;
    private View headerView;

    @BindView(R.id.rv_main_idea)
    RecyclerView rvMainIdea;

    public static Fragment newInstance() {

        Bundle args = new Bundle();

        IdeaFragment fragment = new IdeaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Initlist (초기화 메소드)
    public void initializationList() {
        //초기화
        isScroll = true;
        offset = 0;
        arrList.clear();

        getIdeaList();
        return;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_idea, container, false);
        ButterKnife.bind(this, rootView);

        // frgment 폰트 설정
        SetFont.setGlobalFont(rootView.getContext(), rootView);

        // header view
        {
            headerView = getActivity().getLayoutInflater().inflate(R.layout.header_popular_booth, null, false);

            PopularBoothAdapter adapterPopularBoot = new PopularBoothAdapter(getContext(),arrListBooth);
            LinearLayoutManager rvPopularBoothLayoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.HORIZONTAL, false);

            RecyclerView rvPopularBooth = (RecyclerView)headerView.findViewById(R.id.rv_popular_booth);
            rvPopularBooth.setLayoutManager(rvPopularBoothLayoutManager);
            rvPopularBooth.setAdapter(adapterPopularBoot);
            adapterPopularBoot.onItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent itBoothDetail = new Intent(rootView.getContext(), BoothDetailActivity.class);
                    itBoothDetail.putExtra("boothId", arrListBooth.get(i).getId());
                    itBoothDetail.putExtra("boothName", arrListBooth.get(i).getName());
                    startActivity(itBoothDetail);
                    getActivity().overridePendingTransition(0,0);
                }
            });

            for(int i=0; i<8; i++) {
                arrListBooth.add(new BoothListItem((i+1)+"", (i+1) + "", i+1, i + 100, i + 1000));
            }
            adapterPopularBoot.notifyDataSetChanged();
        }

        // RecyclerView
        {
            rvLayoutManager = new LinearLayoutManager(rootView.getContext());
            rvMainIdea.setLayoutManager(rvLayoutManager);
            rvMainIdea.setNestedScrollingEnabled(true);

            rvMainIdea.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int firstVisibleItem = rvLayoutManager.findFirstVisibleItemPosition();
                    int visibleItemCount = rvMainIdea.getChildCount();
                    int totalItemCount = rvLayoutManager.getItemCount();

                    if ((firstVisibleItem + visibleItemCount) > totalItemCount - 2) {
                        //서버로부터 받아온 List개수를 count
                        //지금까지 받아온 개수를 offset
                        if (count != 0 && offset > 3 && offset % 6 == 0) {
                            if (isScroll) {
                                //스크롤 멈추게 하는거
                                isScroll = false;
                                getIdeaList();
                            }
                        }
                    }
                }
            });
        }

        // Connect adapter
        {
            adapter = new IdeaAdapter(arrList);
            rvMainIdea.setAdapter(adapter);
            adapter.setHeaderView(headerView);
            adapter.onItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedItem = i - 1;
                    Intent itIdeaDetail = new Intent(rootView.getContext(), IdeaDetailActivity.class);
                    itIdeaDetail.putExtra("idea_id", arrList.get(selectedItem).getId());
                    itIdeaDetail.putExtra("email", arrList.get(selectedItem).getEmail());
                    startActivityForResult(itIdeaDetail, 0);
                    getActivity().overridePendingTransition(0,0);
                }
            });
        }

        // fab
        {
            FloatingActionButton fab = ButterKnife.findById(rootView, R.id.main_fab);
            fab.setImageResource(R.drawable.btn_float2);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent itWriteBoothSelect = new Intent(rootView.getContext(), WriteBoothSelectActivity.class);
                    startActivity(itWriteBoothSelect);
                }
            });
        }

        initializationList();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (arrList.size() == 0) {
            initializationList();
            return;
        }

        switch (resultCode) {
            // 일반적 상황 (조회수, 좋아요수, 댓글수, 컨텐츠 업데이트)
            case 0:
                IdeaListItem backItem0 = arrList.get(selectedItem);
                backItem0.setHit(backItem0.getHit() + 1);

                adapter.notifyDataSetChanged();
                break;
            case 1:
                String backContent = data.getStringExtra("backContent");

                IdeaListItem backItem1 = arrList.get(selectedItem);
                backItem1.setContent(backContent);
                backItem1.setHit(backItem1.getHit() + 1);

                adapter.notifyDataSetChanged();
                break;

            // 삭제된 상황 (아이템 지우기)
            case 2:
                arrList.remove(selectedItem);

                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void getIdeaList() {
        ideaService.getIdeaList(offset)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body().string());

                            JSONArray retArr = jObject.getJSONArray("ret");
                            for (int i=0; i<retArr.length(); i++) {
                                JSONObject obj = retArr.getJSONObject(i);

                                int ideaId = obj.getInt("id");
                                int ideaUserId = obj.getInt("userId");
                                int boothId =obj.getInt("boothId");
                                String content = obj.getString("content");
                                int hit =obj.getInt("hit");
                                String date = obj.getString("date");
                                int likeNum =obj.getInt("likeNum");
                                int commentNum =obj.getInt("commentNum");

                                // Item 객체로 만들어야함
                                IdeaListItem item = new IdeaListItem(content,ideaUserId+"@test.com", "booth name", hit, commentNum, likeNum, ideaId);
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
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
}
