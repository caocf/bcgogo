package com.bcgogo.wx.message.resp;

/**
 * 文本消息
 * User: ndong
 * Date: 14-8-7
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public class TextMsg extends BaseMsg {
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}