package com.bcgogo.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.enums.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by XinyuQiu on 14-7-6.
 */
public class BGEnumUtil extends EnumUtils {
  private static final Logger LOG = LoggerFactory.getLogger(BGEnumUtil.class);

  public static boolean compareSame(Enum oldEnum, Enum newEnum) {
    if (oldEnum != null && newEnum != null) {
      return oldEnum.equals(newEnum);
    } else if (oldEnum == null && newEnum == null) {
      return true;
    } else {
      return false;
    }

  }

}

