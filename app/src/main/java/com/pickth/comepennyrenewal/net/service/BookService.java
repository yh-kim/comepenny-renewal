package com.pickth.comepennyrenewal.net.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Kim on 2017-02-14.
 */

public class BookService extends BaseService {
    public BookService() {
        super(BookAPI.class);
    }

    @Override
    public BookAPI getAPI() {
        return (BookAPI)super.getAPI();
    }

    public Call<ResponseBody> getBook(String type, String value) {
        return getAPI().getBook(type, value);
    }


    public interface BookAPI {
        @GET("/book")
        public Call<ResponseBody> getBook(@Query("type") String type,@Query("value") String value);
    }
}
