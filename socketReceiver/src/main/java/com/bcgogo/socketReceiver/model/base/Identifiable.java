package com.bcgogo.socketReceiver.model.base;

import javax.persistence.MappedSuperclass;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-28
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
public abstract class Identifiable extends GeneratedIdentifier<String> {
  protected Identifiable() {
  }

  protected Identifiable(long createTime) {
    super(createTime);
  }
}