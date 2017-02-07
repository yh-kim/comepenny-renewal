package com.pickth.comepennyrenewal.net.service;

import com.pickth.comepennyrenewal.util.StaticUrl;

import retrofit2.Retrofit;

/**
 * Created by Kim on 2017-02-06.
 */

public class BaseService<T> {
    Retrofit retrofit;
    T service;

    public BaseService(final Class<T> clazz) {
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticUrl.BASE_URL)
                .build();
        service = retrofit.create(clazz);
    }

    public T getAPI() {
        return service;
    }
}
