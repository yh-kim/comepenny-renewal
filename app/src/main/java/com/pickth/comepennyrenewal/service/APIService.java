package com.pickth.comepennyrenewal.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Kim on 2017-01-13.
 */

public interface APIService {
    @GET("/booth")
    Call<ResponseBody> getBooths();
}
