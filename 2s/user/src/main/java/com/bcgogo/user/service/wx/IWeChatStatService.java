package com.bcgogo.user.service.wx;

import com.bcgogo.wx.WXAccountStatDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-1-20
 * Time: 15:11
 */
public interface IWeChatStatService {

  List<WXAccountStatDTO> getWXAccountStatDTO();

  String getWXAccountStatStr();

}
