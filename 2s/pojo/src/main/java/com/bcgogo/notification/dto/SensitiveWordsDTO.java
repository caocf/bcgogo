package com.bcgogo.notification.dto;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-7
 * Time: 下午4:25
 *
 */
public class SensitiveWordsDTO implements Serializable {
  private static final Logger LOG = LoggerFactory.getLogger(SensitiveWordsDTO.class);
  private Long id;
  private byte[] words;
  private Long syncTime;
  public List<String> letters;

  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public byte[] getWords() {
    return words;
  }

  public void setWords(byte[] words) {
    this.words = words;
    if (words == null) return;
    String s = "";
    s = new String(words);
    if (StringUtils.isNotBlank(s)) {
      String[] strWords = s.split(" ");
      List<String> wordSet = new ArrayList<String>();
      if (!ArrayUtils.isEmpty(strWords)) {
        Collections.addAll(wordSet, strWords);
      }
      this.setLetters(wordSet);
    }
  }

  public List<String> getLetters() {
    return letters;
  }

  public void setLetters(List<String> letters) {
    this.letters = letters;
  }
}
