package com.bcgogo.user.service.app;

import com.bcgogo.wx.WXShareDTO;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/12/4
 * Time: 14:02.
 */
public interface IWXShareService {

    WXShareDTO shareInfo(String appUserNo);
}
