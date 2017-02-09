package com.pickth.comepennyrenewal.idea;

/**
 * Created by Kim on 2017-01-13.
 */

public class IdeaListItem {
    private String content;
    private String email;
    private String boothName;
    private int hit;
    private int commentNum;
    private int likeNum;
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

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public IdeaListItem(String content, String email, String boothName, int hit, int commentNum, int likeNum, int id) {

        this.content = content;
        this.email = email;
        this.boothName = boothName;
        this.hit = hit;
        this.commentNum = commentNum;
        this.likeNum = likeNum;
        this.id = id;
    }
}
