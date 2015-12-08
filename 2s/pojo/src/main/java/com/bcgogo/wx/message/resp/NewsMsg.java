package com.bcgogo.wx.message.resp;

import com.bcgogo.wx.WXArticleDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 多图文消息，
 * 单图文的时候 Articles 只放一个就行了
 * User: ndong
 * Date: 14-8-13
 * Time: 上午9:31
 * To change this template use File | Settings | File Templates.
 */
public class NewsMsg extends BaseMsg {
   // 图文消息个数，限制为10条以内
    private String ArticleCount;
    // 多条图文消息信息，默认第一个item为大图
    private List<WXArticleDTO> Articles=new ArrayList<WXArticleDTO>();

  public String getArticleCount() {
    return ArticleCount;
  }

  public void setArticleCount(String articleCount) {
    ArticleCount = articleCount;
  }

  public List<WXArticleDTO> getArticles() {
    return Articles;
  }

  public void setArticles(List<WXArticleDTO> articles) {
    Articles = articles;
  }
}
