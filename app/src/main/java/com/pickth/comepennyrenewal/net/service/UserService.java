package com.pickth.comepennyrenewal.net.service;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

/**
 * Created by Kim on 2017-02-08.
 */

public class UserService extends BaseService {
    public UserService() {
        super(UserAPI.class);
    }

    /**
     * 사용자 정보를 수정
     * @param userId 유저 아이디
     * @return
     */
    public Call<ResponseBody> putUserInfo(String userId, MultipartBody.Part file) {
        return getAPI().putUserInfo(userId, file);
    }


    @Override
    public UserAPI getAPI() {
        return (UserAPI)super.getAPI();
    }

    public interface UserAPI {
        @Multipart
        @PUT("/awsS3/user")
        public Call<ResponseBody> putUserInfo(@Part("user_id") String userId, @Part MultipartBody.Part file);
    }
}
