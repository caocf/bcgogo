package com.bcgogo.user.service.app;

import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.wx.WXShareDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/12/4
 * Time: 14:44.
 */
@Component
public class WXShareService implements IWXShareService {

    private static final Logger LOG = LoggerFactory.getLogger(WXShareService.class);

    @Autowired
    private UserDaoManager userDaoManager;

    @Override
    public WXShareDTO shareInfo(String appUserNo) {

        UserWriter userWriter = userDaoManager.getWriter();
        return userWriter.wxShareInfo(appUserNo);
    }
}
