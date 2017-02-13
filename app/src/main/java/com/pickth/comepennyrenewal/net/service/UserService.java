package com.pickth.comepennyrenewal.net.service;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Kim on 2017-02-08.
 */

public class UserService extends BaseService {
    public UserService() {
        super(UserAPI.class);
    }

    /**
     * 회원가입
     * @return
     */
    public Call<ResponseBody> postUser() {
        return getAPI().postUser();
    }

    /**
     * 사용자 정보를 수정
     * @return
     */
    public Call<ResponseBody> putUserInfo(MultipartBody.Part file) {
        return getAPI().putUserInfo(file);
    }


    @Override
    public UserAPI getAPI() {
        return (UserAPI)super.getAPI();
    }

    public interface UserAPI {
        @POST("/user")
        public Call<ResponseBody> postUser();

        @Multipart
        @POST("/awsS3/user")
        public Call<ResponseBody> putUserInfo(@Part MultipartBody.Part file);
    }
}
