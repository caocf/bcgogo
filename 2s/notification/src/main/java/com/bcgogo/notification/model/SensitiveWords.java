package com.bcgogo.notification.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.SensitiveWordsDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-7
 * Time: 下午4:17
 */

/**
 * '
 * 短信敏感词库
 */
@Entity
@Table(name = "sensitive_words")
public class SensitiveWords extends LongIdentifier {
  private byte[] words;

  @Column(name = "words")
  @Lob
  public byte[] getWords() {
    return words;
  }

  public SensitiveWords() {
  }

  public void setWords(byte[] words) {
    this.words = words;
  }

  public SensitiveWordsDTO toDTO() {
    SensitiveWordsDTO sensitiveWordsDTO = new SensitiveWordsDTO();
    sensitiveWordsDTO.setWords(this.getWords());
    sensitiveWordsDTO.setId(this.getId());
    return sensitiveWordsDTO;
  }

  public SensitiveWords(SensitiveWordsDTO sensitiveWordsDTO) {
    this.setWords(sensitiveWordsDTO.getWords());
    this.setId(sensitiveWordsDTO.getId());
  }
}
