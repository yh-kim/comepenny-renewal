package com.pickth.comepennyrenewal.idea;

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
import com.pickth.comepennyrenewal.booth.BoothDetailActivity;
import com.pickth.comepennyrenewal.booth.BoothListItem;
import com.pickth.comepennyrenewal.booth.PopularBoothAdapter;
import com.pickth.comepennyrenewal.net.service.BoothService;
import com.pickth.comepennyrenewal.net.service.IdeaService;
import com.pickth.comepennyrenewal.util.SetFont;
import com.pickth.comepennyrenewal.util.StaticNumber;
import com.pickth.comepennyrenewal.write.WriteBoothSelectActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindArray;
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
    private int count = StaticNumber.GET_IDEA_COUNT;
    private int offset = 0;
    int selectedItem = 0;
    PopularBoothAdapter adapterPopularBoot;

    @BindArray(R.array.booth_names)
    String[] boothNames;

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
    public void initializeList() {
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

            adapterPopularBoot = new PopularBoothAdapter(getContext(),arrListBooth);
            LinearLayoutManager rvPopularBoothLayoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.HORIZONTAL, false);

            RecyclerView rvPopularBooth = (RecyclerView)headerView.findViewById(R.id.rv_popular_booth);
            rvPopularBooth.setLayoutManager(rvPopularBoothLayoutManager);
            rvPopularBooth.setAdapter(adapterPopularBoot);
            adapterPopularBoot.onItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent itBoothDetail = new Intent(rootView.getContext(), BoothDetailActivity.class);
                    itBoothDetail.putExtra("boothId", arrListBooth.get(i).getId());
                    itBoothDetail.putExtra("boothNames", arrListBooth.get(i).getName());
                    startActivity(itBoothDetail);
                    getActivity().overridePendingTransition(0,0);
                }
            });

            getPopularBoothList();
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
                        if (count != 0 && offset > 3 && offset % StaticNumber.GET_IDEA_COUNT == 0) {
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
                    startActivityForResult(itWriteBoothSelect, 1);
                }
            });
        }

        initializeList();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (arrList.size() == 0) {
            initializeList();
            return;
        }

        // 추가버튼 눌렀을 때
        if(requestCode == 1) {
            initializeList();
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
                // 수정했을 때
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
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    private void getPopularBoothList() {
        new BoothService()
                .getBoothList()
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            try {
                                JSONObject jObject = new JSONObject(response.body().string());

                                JSONArray retArr = jObject.getJSONArray("ret");
                                for (int i = 0; i < 5; i++) {
                                    JSONObject obj = retArr.getJSONObject(i);

                                    int boothId = obj.getInt("id");
                                    int ideaNum = obj.getInt("ideaNum");
                                    int likeNum = obj.getInt("likeNum");
                                    String imgUrl = boothId + "";
                                    String boothName = obj.getString("name");

                                    // Item 객체로 만들어야함
                                    BoothListItem item = new BoothListItem(imgUrl, boothName, boothId, ideaNum, likeNum);
                                    // Item 객체를 ArrayList에 넣는다
                                    arrListBooth.add(item);

                                    // Adapter에게 데이터를 넣었으니 갱신하라고 알려줌
                                    adapterPopularBoot.notifyDataSetChanged();
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
