package com.pickth.comepennyrenewal.net.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Kim on 2017-02-07.
 */

public class BoothService extends BaseService {
    public BoothService() {
        super(BoothAPI.class);
    }

    /**
     * 부스 리스트를 가져옵니다
     * @return
     */
    public Call<ResponseBody> getBoothList() {
        return getAPI().getBoothList();
    }


    @Override
    public BoothAPI getAPI() {
        return (BoothAPI)super.getAPI();
    }

    public interface BoothAPI {
        @GET("/booth")
        Call<ResponseBody> getBoothList();
    }
}
