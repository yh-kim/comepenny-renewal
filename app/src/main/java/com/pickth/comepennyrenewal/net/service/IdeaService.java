package com.pickth.comepennyrenewal.net.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Kim on 2017-02-07.
 */

public class IdeaService extends BaseService {
    public IdeaService() {
        super(IdeaAPI.class);
    }

    @Override
    public IdeaAPI getAPI() {
        return (IdeaAPI)super.getAPI();
    }

    public Call<ResponseBody> getIdea(String ideaId, String userId) {
        return getAPI().getIdea(ideaId, userId);
    }

    public interface IdeaAPI {
        @GET("/idea/{idea_id}")
        Call<ResponseBody> getIdea(@Path("idea_id") String ideaId, @Field("user_id") String userId);
    }
}
