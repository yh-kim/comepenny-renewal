package com.pickth.comepennyrenewal.net.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Kim on 2017-02-08.
 */

public class CommentService extends BaseService {
    public CommentService() {
        super(CommentAPI.class);
    }

    /**
     * 댓글 목록 가져오기
     * @param ideaId 아이디어 아이디
     * @param offset 시작점
     * @return
     */
    public Call<ResponseBody> getCommentList(int ideaId, int offset) {
        return getAPI().getCommentList(ideaId, offset);
    }

    /**
     * 댓글을 작성합니다
     * @param ideaId 아이디어 아이디
     * @param userId 유저 아이디
     * @param comment 댓글 내용
     * @return
     */
    public Call<ResponseBody> postComment(int ideaId, String userId, String comment) {
        return getAPI().postComment(ideaId, userId, comment);
    }

    /**
     * 댓글을 수정합니다
     * @param commentId 댓글 아이디
     * @param comment 수정할 댓글 내용
     * @return
     */
    public Call<ResponseBody> putComment(int commentId, String comment) {
        return getAPI().putComment(commentId, comment);
    }

    /**
     * 댓글을 삭제합니다
     * @param commentId 댓글 아이디
     * @return
     */
    public Call<ResponseBody> deleteComment(int commentId) {
        return getAPI().deleteCommnet(commentId);
    }

    @Override
    public CommentAPI getAPI() {
        return (CommentAPI)super.getAPI();
    }

    public interface CommentAPI {
        @GET("/comment/{idea_id}")
        public Call<ResponseBody> getCommentList(@Path("idea_id") int ideaId, @Query("offset") int offset);

        @FormUrlEncoded
        @POST("/comment")
        public Call<ResponseBody> postComment(@Field("idea_id") int ideaId, @Field("user_id") String userId, @Field("comment") String comment);

        @PUT("/comment/{comment_id}")
        public Call<ResponseBody> putComment(@Path("comment_id") int commentId, @Query("comment") String comment);

        @DELETE("/comment/{comment_id}")
        public Call<ResponseBody> deleteCommnet(@Path("comment_id") int CommentId);
    }
}
