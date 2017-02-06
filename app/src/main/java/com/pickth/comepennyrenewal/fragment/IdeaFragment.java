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
import com.pickth.comepennyrenewal.util.SetFont;
import com.pickth.comepennyrenewal.util.StaticUrl;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kim on 2017-01-13.
 */

public class IdeaFragment extends Fragment {
    View rootView;
    View headerView;
    IdeaAdapter adapter;
    RecyclerView.LayoutManager rvLayoutManager;
    ArrayList<IdeaListItem> arrList = new ArrayList<IdeaListItem>(  );
    ArrayList<BoothListItem> arrListBooth = new ArrayList<>();

    private static String API_URL = StaticUrl.BASE_URL;

    @BindView(R.id.rv_main_idea)
    RecyclerView rvMainIdea;

    public static Fragment newInstance() {

        Bundle args = new Bundle();

        IdeaFragment fragment = new IdeaFragment();
        fragment.setArguments(args);
        return fragment;
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
        }

        // Connect adapter
        {
            adapter = new IdeaAdapter(arrList);
            rvMainIdea.setAdapter(adapter);
            adapter.setHeaderView(headerView);
            adapter.onItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent itIdeaDetail = new Intent(rootView.getContext(), IdeaDetailActivity.class);
                    startActivity(itIdeaDetail);
                    getActivity().overridePendingTransition(0,0);
                }
            });
        }

        // Input test value
        {
            for(int i=0; i<12; i++){
                arrList.add(new IdeaListItem("content"+i, "email" +i, "부스 이름", 100*i, i, i*100, i));
            }
            adapter.notifyDataSetChanged();
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

        // Connect server
        {
            //getMainIdeas();
        }

        return rootView;
    }
}
