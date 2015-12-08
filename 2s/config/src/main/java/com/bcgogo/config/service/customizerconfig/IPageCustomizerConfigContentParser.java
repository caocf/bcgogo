package com.bcgogo.config.service.customizerconfig;

/**
 * User: ZhangJuntao
 * Date: 13-5-26
 * Time: 下午1:14
 */
public interface IPageCustomizerConfigContentParser<T> {
  T parseJsonToDto(String json);

  String parseDtoToJson(T content);
}
