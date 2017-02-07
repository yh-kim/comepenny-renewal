package com.pickth.comepennyrenewal.net.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Kim on 2017-02-07.
 */

public class IdeaService extends BaseService {
    public IdeaService() {
        super(IdeaAPI.class);
    }

    public Call<ResponseBody> getIdea(int ideaId, String userId) {
        return getAPI().getIdea(ideaId, userId);
    }

    public Call<ResponseBody> getIdeaList(int offset) {
        return getAPI().getIdeaList(offset);
    }

    @Override
    public IdeaAPI getAPI() {
        return (IdeaAPI)super.getAPI();
    }

    public interface IdeaAPI {
        /**
         * 아이디어의 상세 정보를 가져옵니다
         * @param ideaId
         * @param userId
         * @return
         */
        @GET("/idea/{idea_id}")
        Call<ResponseBody> getIdea(@Path("idea_id") int ideaId, @Query("user_id") String userId);

        /**
         * 아이디어 리스트를 최신순으로 불러옵니다
         * @param offset
         * @return
         */
        @GET("/idea")
        Call<ResponseBody> getIdeaList(@Query("offset")int offset);

//        @FormUrlEncoded
//        @GET("/idea/{idea_id}")
//        Call<ResponseBody> getIdea(@Path("idea_id") int ideaId, @Field("user_id") int userId);
    }
}
