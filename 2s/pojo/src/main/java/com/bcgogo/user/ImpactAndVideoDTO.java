package com.bcgogo.user;

/**
 * Created by Administrator on 2015/10/28.
 */
public class ImpactAndVideoDTO {

    private long vid;            //视频编号
    private String[] video;
    private String[] picture;
    private String place;       //碰撞地点
    private long date;          //碰撞时间
    private int type;           //碰撞类型        0:正常行驶 1:停车监控
    private int progress;       //视频上传进度    0:完成 1:没完成

    public long getVid() {
        return vid;
    }

    public void setVid(long vid) {
        this.vid = vid;
    }

    public String[] getVideo() {
        return video;
    }

    public void setVideo(String[] video) {
        this.video = new String[video.length];
        System.arraycopy(video ,0,this.video,0,video.length);
    }

    public String[] getPicture() {
        return picture;
    }

    public void setPicture(String[] picture) {
        this.picture = new String[picture.length];
        System.arraycopy(picture,0,this.picture,0,picture.length);
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
