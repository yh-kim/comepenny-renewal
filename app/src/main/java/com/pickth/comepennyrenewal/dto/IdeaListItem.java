package com.pickth.comepennyrenewal.dto;

/**
 * Created by Kim on 2017-01-13.
 */

public class IdeaListItem {
    private String content;
    private String email;
    private String boothName;
    private int ViewCount;
    private int commentCount;
    private int LikeCount;
    private int id;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBoothName() {
        return boothName;
    }

    public void setBoothName(String boothName) {
        this.boothName = boothName;
    }

    public int getViewCount() {
        return ViewCount;
    }

    public void setViewCount(int viewCount) {
        ViewCount = viewCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getLikeCount() {
        return LikeCount;
    }

    public void setLikeCount(int likeCount) {
        LikeCount = likeCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public IdeaListItem(String content, String email, String boothName, int viewCount, int commentCount, int likeCount, int id) {

        this.content = content;
        this.email = email;
        this.boothName = boothName;
        ViewCount = viewCount;
        this.commentCount = commentCount;
        LikeCount = likeCount;
        this.id = id;
    }
}
