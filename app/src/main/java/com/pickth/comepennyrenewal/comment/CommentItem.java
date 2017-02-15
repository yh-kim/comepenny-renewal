package com.pickth.comepennyrenewal.comment;

/**
 * Created by Kim on 2017-01-30.
 */

public class CommentItem {
    private String userImg;
    private String content;
    private String userEmail;
    private String commentTime;
    private int commentId;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CommentItem(String userImg, String content, String userEmail, String commentTime, int commentId) {
        this.userImg = userImg;
        this.content = content;
        this.userEmail = userEmail;
        this.commentTime = commentTime;
        this.commentId = commentId;
    }

    public String getUserImg() {

        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
}
