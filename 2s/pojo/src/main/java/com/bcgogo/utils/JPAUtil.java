package com.bcgogo.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Blob;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-9
 * Time: 上午9:47
 */
public class JPAUtil {

  public static byte[] blobToBytes(Blob blob) throws IOException {
    BufferedInputStream is = null;
    try {
      is = new BufferedInputStream(blob.getBinaryStream());
      byte[] bytes = new byte[(int) blob.length()];
      int len = bytes.length;
      int offset = 0;
      int read = 0;
      while (offset < len && (read = is.read(bytes, offset, len - offset)) >= 0) {
        offset += read;
      }
      return bytes;
    } catch (Exception e) {
      return null;
    } finally {
      try {
        if(is!=null){
          is.close();
          is = null;
        }
      } catch (IOException e) {
        return null;
      }
    }
  }


}
