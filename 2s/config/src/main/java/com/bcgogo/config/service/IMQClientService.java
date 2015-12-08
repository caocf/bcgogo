package com.bcgogo.config.service;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-31
 * Time: 下午5:29
 */
public interface IMQClientService {

  boolean isOnLine(String name) throws IOException;

}
