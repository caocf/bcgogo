package com.bcgogo.user.service.wx;

import com.bcgogo.wx.WXRequestParam;

import java.util.Map;

/**
 * 被动响应消息处理
 * User: ndong
 * Date: 14-8-18
 * Time: 下午11:48
 * To change this template use File | Settings | File Templates.
 */
public interface IWXHandler {

   String doHandle(WXRequestParam param) throws Exception;

}
