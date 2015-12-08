package com.bcgogo.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-6-26
 * Time: 下午12:31
 */
public class ClientAssortedMessageResult {
  private List<ClientAssortedMessage> messages = new ArrayList<ClientAssortedMessage>();
  private Long nextRequestTimeInterval;

  public ClientAssortedMessageResult() {
    super();
  }

  public ClientAssortedMessageResult(Long clientNextRequestTimeInterval) {
    this.setNextRequestTimeInterval(clientNextRequestTimeInterval);
  }

  public List<ClientAssortedMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<ClientAssortedMessage> messages) {
    this.messages = messages;
  }

  public Long getNextRequestTimeInterval() {
    return nextRequestTimeInterval;
  }

  public void setNextRequestTimeInterval(Long nextRequestTimeInterval) {
    this.nextRequestTimeInterval = nextRequestTimeInterval;
  }
}
