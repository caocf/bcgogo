package com.bcgogo.user.service.wx;

import com.bcgogo.common.Result;
import com.bcgogo.wx.UploadMediaResult;
import com.bcgogo.wx.WXArticleDTO;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-23
 * Time: 下午2:45
 * To change this template use File | Settings | File Templates.
 */
public interface IWXMediaManager {

  UploadMediaResult uploadImage(Long shopId,String picUrl) throws Exception;

  Result uploadArticles(Long shopId,WXArticleDTO articleDTO) throws Exception;

  String downLoadArticles(Long shopId,String mediaId) throws Exception;


}
