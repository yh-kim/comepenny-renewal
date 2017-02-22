package com.pickth.comepennyrenewal.myinfo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.idea.IdeaAdapter;
import com.pickth.comepennyrenewal.idea.IdeaDetailActivity;
import com.pickth.comepennyrenewal.idea.IdeaListItem;
import com.pickth.comepennyrenewal.util.DataManagement;
import com.pickth.comepennyrenewal.util.SetFont;
import com.pickth.comepennyrenewal.util.StaticNumber;

import java.util.ArrayList;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kim on 2017-02-21.
 */

public abstract class MyInfoBaseFragment extends Fragment {
    protected String userId = "";
    protected View rootView;
    protected boolean isScroll = false;
    protected int count = StaticNumber.GET_IDEA_COUNT;
    protected int offset = 0;
    protected int selectedItem = 0;

    protected IdeaAdapter adapter;
    protected LinearLayoutManager rvLayoutManager;
    protected ArrayList<IdeaListItem> arrList = new ArrayList<IdeaListItem>(  );

    @BindArray(R.array.booth_names)
    String[] boothNames;

    @BindView(R.id.rv_fragment_idea)
    RecyclerView rvMainIdea;

    @BindView(R.id.tv_idea_not_found)
    TextView tvIdeaNotFound;

    protected abstract void getIdeaList();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_idea_list, container, false);
        ButterKnife.bind(this, rootView);

        userId = DataManagement.getAppPreferences(getActivity(), "user_id");

        // frgment 폰트 설정
        SetFont.setGlobalFont(rootView.getContext(), rootView);

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
            adapter.onItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedItem = i;
                    Intent itIdeaDetail = new Intent(rootView.getContext(), IdeaDetailActivity.class);
                    itIdeaDetail.putExtra("idea_id", arrList.get(selectedItem).getId());
                    startActivityForResult(itIdeaDetail, 0);
                    getActivity().overridePendingTransition(0,0);
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

        switch (resultCode) {
            // 일반적 상황 (조회수, 좋아요수, 댓글수, 컨텐츠 업데이트), 수정했을 때
            case 0:
            case 1:
                String backContent = data.getStringExtra("backContent");
                int backView = data.getIntExtra("backView", 0);
                int backComment = data.getIntExtra("backComment", 0);
                int backLike = data.getIntExtra("backLike", 0);

                IdeaListItem backItem = arrList.get(selectedItem);
                backItem.setContent(backContent);
                backItem.setHit(backView);
                backItem.setCommentNum(backComment);
                backItem.setLikeNum(backLike);

                adapter.notifyDataSetChanged();
                break;

            // 삭제된 상황 (아이템 지우기)
            case 2:
                arrList.remove(selectedItem);

                adapter.notifyDataSetChanged();
                break;
        }
    }

    public void initializeList() {
        //초기화
        isScroll = true;
        offset = 0;
        arrList.clear();

        getIdeaList();
        return;
    }
}
