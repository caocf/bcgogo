package com.bcgogo.service;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-6
 * Time: 16:15
 */
public interface IClientService {

  boolean validateSerialNo() throws Exception;

  void download(String url,String path) throws IOException;

}
