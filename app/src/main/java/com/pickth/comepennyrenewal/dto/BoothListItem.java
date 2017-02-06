package com.pickth.comepennyrenewal.dto;

/**
 * Created by Kim on 2017-01-13.
 */

public class BoothListItem {
    String imgUrl;
    private String name;
    private int id;
    private int likeNum;
    private int ideaNum;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getIdeaNum() {
        return ideaNum;
    }

    public void setIdeaNum(int ideaNum) {
        this.ideaNum = ideaNum;
    }

    public BoothListItem(String imgUrl, String name, int id, int likeNum, int ideaNum) {

        this.imgUrl = "/booth/"+imgUrl+".png";
        this.name = name;
        this.id = id;
        this.likeNum = likeNum;
        this.ideaNum = ideaNum;
    }
}
