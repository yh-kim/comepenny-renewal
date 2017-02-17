package com.pickth.comepennyrenewal.net.service;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

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
    public Call<ResponseBody> postUser(String id, String email, String profileImage, String thumbnailImage, String nickname) {
        return getAPI().postUser(id, email, profileImage, thumbnailImage, nickname);
    }

    /**
     * 사용자 정보를 수정
     * @return
     */
    public Call<ResponseBody> putUserInfo(MultipartBody.Part file) {
        return getAPI().putUserInfo(file);
    }

    /**
     * 유저 정보를 지웁니다
     * @param userId
     * @return
     */
    public Call<ResponseBody> deleteUser(String userId) {
        return getAPI().deleteUser(userId);
    }


    @Override
    public UserAPI getAPI() {
        return (UserAPI)super.getAPI();
    }

    public interface UserAPI {
        @FormUrlEncoded
        @POST("/user")
        public Call<ResponseBody> postUser(@Field("id") String id, @Field("email") String email, @Field("profile_image") String profileImage, @Field("thumbnail_image") String thumbnailImage, @Field("nickname") String nickname);

        @Multipart
        @POST("/awsS3/user")
        public Call<ResponseBody> putUserInfo(@Part MultipartBody.Part file);

        @DELETE("/user/{user_id}")
        public Call<ResponseBody> deleteUser(@Path("user_id") String userId);
    }
}
