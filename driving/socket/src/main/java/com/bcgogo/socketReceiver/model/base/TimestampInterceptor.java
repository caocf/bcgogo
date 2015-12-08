package com.bcgogo.socketReceiver.model.base;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-3-10
 * Time: 下午1:43
 * To change this template use File | Settings | File Templates.
 */
public class TimestampInterceptor extends EmptyInterceptor {

  @Override
  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
    if ( entity instanceof RecordTimestamp ) {
      boolean flag = false;
      for ( int i=0; i<propertyNames.length; i++ ) {
        if ( "creationDate".equals( propertyNames[i] ) ) {
          state[i] = System.currentTimeMillis();
          flag = true;
        }
        if ( "lastModified".equals( propertyNames[i] ) ) {
          state[i] = System.currentTimeMillis();
        }
      }
      return flag;
    }
    return false;
  }

  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
    if ( entity instanceof RecordTimestamp ) {
      for ( int i=0; i < propertyNames.length; i++ ) {
        if ( "lastModified".equals( propertyNames[i] ) ) {
          currentState[i] = System.currentTimeMillis();
          return true;
        }
      }
    }
    return false;
  }
}
