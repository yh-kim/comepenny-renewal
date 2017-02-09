package com.pickth.comepennyrenewal.net.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Kim on 2017-02-07.
 */

public class IdeaService extends BaseService {
    public IdeaService() {
        super(IdeaAPI.class);
    }

    /**
     * 아이디어의 상세 정보를 가져옵니다
     * @param ideaId
     * @param userId
     * @return
     */
    public Call<ResponseBody> getIdea(int ideaId, String userId) {
        return getAPI().getIdea(ideaId, userId);
    }

    /**
     * 아이디어 리스트를 최신순으로 불러옵니다
     * @param offset 시작점
     * @return
     */
    public Call<ResponseBody> getIdeaList(int offset) {
        return getAPI().getIdeaList(offset);
    }

    /**
     * 해당 부스에 있는 아이디어 리스트를 최신순으로 불러옵니다
     * @param boothId 부스 아이디
     * @param offset 시작점
     * @return
     */
    public Call<ResponseBody> getIdeaListByBooth(int boothId, int offset) {
        return getAPI().getIdeaListByBooth(boothId, offset);
    }

    /**
     * 유저가 좋아요한 아이디어 리스트를 최신순으로 불러옵니다
     * @param userId 유저 아이디
     * @param offset 시작점
     * @return
     */
    public Call<ResponseBody> getIdeaListByLike(String userId, int offset) {
       return getAPI().getIdeaListByLike(userId, offset);
    }

    /**
     * 아이디어를 작성합니다
     * @param userId 유저 아이디
     * @param boothId 부스 아이디
     * @param content 아이디어 컨텐츠
     * @return
     */
    public Call<ResponseBody> postIdea(String userId, int boothId, String content) {
        return getAPI().postIdea(userId, boothId, content);
    }

    /**
     * 아이디어를 좋아합니다
     * @param ideaId 아이디어 아이디
     * @param userId 유저 아이디
     * @return
     */
    public Call<ResponseBody> postLike(int ideaId, String userId) {
        return getAPI().postLike(ideaId, userId);
    }

    /**
     * 아이디어를 삭제합니다
     * @param ideaId 아이디어 아이디
     * @return
     */
    public Call<ResponseBody> deleteIdea(int ideaId) {
        return getAPI().deleteIdea(ideaId);
    }

    /**
     * 좋아요를 취소합니다
     * @param ideaId 아이디어 아이디
     * @param userId 유저 아이디
     * @return
     */
    public Call<ResponseBody> deleteLike(int ideaId, String userId) {
        return getAPI().deleteLike(ideaId, userId);
    }


    @Override
    public IdeaAPI getAPI() {
        return (IdeaAPI)super.getAPI();
    }

    public interface IdeaAPI {

        @GET("/idea/{id}")
        Call<ResponseBody> getIdea(@Path("id") int ideaId, @Query("user_id") String userId);

        @GET("/idea")
        Call<ResponseBody> getIdeaList(@Query("offset")int offset);

        @GET("/idea/{booth_id}/booth")
        Call<ResponseBody> getIdeaListByBooth(@Path("booth_id") int boothId, @Query("offset")int offset);

        @GET("/idea/like/{user_id}")
        Call<ResponseBody> getIdeaListByLike(@Path("user_id") String userId, @Query("offset")int offset);

        @FormUrlEncoded
        @POST("/idea")
        Call<ResponseBody> postIdea(@Field("user_id") String userId, @Field("booth_id") int boothId, @Field("content") String content);

        @FormUrlEncoded
        @POST("/idea/{id}")
        Call<ResponseBody> putIdea(@Path("id") int ideaId, @Field("content") String content);

        @FormUrlEncoded
        @POST("/idea/{id}/like")
        Call<ResponseBody> postLike(@Path("id") int ideaId, @Field("user_id") String userId);

        @DELETE("/idea/{id}")
        Call<ResponseBody> deleteIdea(@Path("id") int ideaId);

        @DELETE("/idea/{id}/like")
        Call<ResponseBody> deleteLike(@Path("id") int ideaId, @Field("user_id") String userId);
    }
}
