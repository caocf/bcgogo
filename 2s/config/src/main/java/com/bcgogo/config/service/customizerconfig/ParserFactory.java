package com.bcgogo.config.service.customizerconfig;

import com.bcgogo.enums.config.PageCustomizerConfigScene;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-5-26
 * Time: 下午6:52
 */
public class ParserFactory {
  private static Map<PageCustomizerConfigScene, IPageCustomizerConfigContentParser> parserMap = new HashMap<PageCustomizerConfigScene, IPageCustomizerConfigContentParser>();

  static {
    parserMap.put(PageCustomizerConfigScene.PRODUCT, new PageCustomizerConfigProductContentParser());
    parserMap.put(PageCustomizerConfigScene.ORDER, new PageCustomizerConfigOrderContentParser());
  }

  public static <T> IPageCustomizerConfigContentParser<T> getParserByName(PageCustomizerConfigScene scene) {
    return parserMap.get(scene);
  }

}
