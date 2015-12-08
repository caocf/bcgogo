package com.bcgogo.wx.message.resp;

/**
 * 音乐消息
 * User: ndong
 * Date: 14-8-13
 * Time: 上午9:33
 * To change this template use File | Settings | File Templates.
 */
public class MusicMsg extends BaseMsg {
    /**
     * 音乐
     */
    private Music Music;

    public Music getMusic() {
        return Music;
    }

    public void setMusic(Music music) {
        Music = music;
    }
}
