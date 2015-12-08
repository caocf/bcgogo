package com.bcgogo.user.service.wx.impl;

import com.bcgogo.api.response.HttpResponse;
import com.bcgogo.common.Result;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.wx.IWXMediaManager;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.*;
import com.bcgogo.wx.MsgType;
import com.bcgogo.wx.UploadMediaResult;
import com.bcgogo.wx.WXArticleDTO;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.article.NewsArticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 上传下载多媒体文件 (多媒体文件,发送到微信服务器3天后自动删除)
 * User: ndong
 * Date: 14-9-23
 * Time: 下午2:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WXMediaManager implements IWXMediaManager {
  public static final Logger LOG = LoggerFactory.getLogger(WXMediaManager.class);

  /**
   *  上传图片到微信空间
   * @param shopId
   * @param picUrl
   * @return
   * @throws Exception
   */
  @Override
  public  UploadMediaResult uploadImage(Long shopId,String picUrl) throws Exception {
    //读网络图片,并生成临时文件到路径
    byte[] imgBytes= FileUtil.readUrlDate(picUrl);
    String path =ServiceManager.getService(IConfigService.class).getConfig("wx_cfg_path", ShopConstant.BC_SHOP_ID);
    path+="_temp_"+shopId+System.currentTimeMillis()+".jpg";
    try{
      String accessToken= WXHelper.getAccessTokenByShopId(shopId);
      String url=WXConstant.URL_MEDIA_UPLOAD.replace("{ACCESS_TOKEN}",accessToken).replace("{TYPE}", MsgType.image.toString());
      File readFile=FileUtil.getFileFromBytes(imgBytes,path);
      HttpResponse response=HttpUtils.uploadFile(url, readFile);
      String content=response.getContent();
      return JsonUtil.jsonToObj(content, UploadMediaResult.class);
    } finally {
      //todo 删除临时文件
    }

  }

  public static void main(String[]args){

    try {
      for(int i=0;i<105;i++){
        String picUrl="https://res.wx.qq.com/mpres/htmledition/images/icon/emotion/"+i+".gif";
        byte[] imgBytes= FileUtil.readUrlDate(picUrl);
       String path="D:\\emotion\\"+i+".gif";
        File readFile=FileUtil.getFileFromBytes(imgBytes,path);
        System.out.println();
      } }catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
}


  /**
   * 上传图文消息素材
   * @param shopId
   * @param articleDTO
   * @return
   * @throws Exception
   */
  @Override
  public Result uploadArticles(Long shopId,WXArticleDTO articleDTO) throws Exception {
    Result result=new Result();
    if(StringUtil.isAllEmpty(shopId,articleDTO))  {
      return result.LogErrorMsg("illegal param");
    }
    //upload image
    UploadMediaResult mResult=uploadImage(shopId,articleDTO.getPicUrl());
    if(StringUtil.isEmpty(mResult.getMedia_id())){
      LOG.error("wx:upload image failed,{}",mResult.getErrmsg());
      return result.LogErrorMsg(mResult.getErrmsg());
    }
    articleDTO.setMediaId(mResult.getMedia_id());
    //生成图文消息
    mResult=doUploadArticles(shopId,articleDTO);
    if(StringUtil.isEmpty(mResult.getMedia_id())){
      LOG.error("wx:uploadNews failed,{}",mResult.getErrmsg());
      return result.LogErrorMsg(mResult.getErrmsg());
    }
    result.setData(mResult.getMedia_id());
    return result;
  }

  /**
   * 上传图文消息素材
   * @param shopId
   * @param articleDTO
   * @return
   * @throws Exception
   */
  private UploadMediaResult doUploadArticles(Long shopId,WXArticleDTO articleDTO) throws Exception {
    NewsArticle article=articleDTO.toNewsArticle();
//    article.setContent_source_url("www.qq.com");
    String accessToken= WXHelper.getAccessTokenByShopId(shopId);
    String url= WXConstant.URL_UPLOAD_NEWS.replace("{ACCESS_TOKEN}", accessToken);
    NewsArticle [] articles=new NewsArticle[1];
    articles[0]=article;
    Map<String,NewsArticle[]> data=new HashMap<String, NewsArticle[]>();
    data.put("articles",articles);
    HttpResponse response= HttpUtils.sendPost(url, JsonUtil.objectToJson(data));
    String content=response.getContent();
    return JsonUtil.jsonToObj(content,UploadMediaResult.class);
  }


  @Override
  public String downLoadArticles(Long shopId,String mediaId) throws Exception {
    String accessToken= WXHelper.getAccessTokenByShopId(shopId);
    String url= WXConstant.URL_DOWNLOAD_NEWS.replace("{ACCESS_TOKEN}", accessToken).replace("{MEDIA_ID}", mediaId);
    HttpResponse response= HttpUtils.sendPost(url);
    return response.getContent();
  }

}
