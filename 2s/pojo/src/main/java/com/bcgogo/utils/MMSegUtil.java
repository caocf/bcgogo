package com.bcgogo.utils;

import com.chenlb.mmseg4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-2-22
 * Time: 下午1:36
 * To change this template use File | Settings | File Templates.
 */
public class MMSegUtil {
  public static final Logger LOG = LoggerFactory.getLogger(MMSegUtil.class);
  private static MaxWordSeg maxWordSeg = null;
  private static SimpleSeg simpleSeg = null;

  static{
      if(maxWordSeg==null || simpleSeg==null){
        Resource url = new DefaultResourceLoader().getResource("data");
        File file= null;
        try {
          if(url!=null){
            file = url.getFile();
          }
        } catch (IOException e) {
          LOG.error("default mmseg4j dic Resource not exist");
          e.printStackTrace();
        }
        if(file!=null){
          maxWordSeg = new MaxWordSeg(Dictionary.getInstance(file));
          simpleSeg = new SimpleSeg(Dictionary.getInstance(file));
        }else{
          LOG.error("default mmseg4j dic path not exist");
          maxWordSeg = new MaxWordSeg(Dictionary.getInstance());
          simpleSeg = new SimpleSeg(Dictionary.getInstance());
        }
      }
  }

  public synchronized static List<String> getTocken(String phrase) throws Exception {
    List<String> result = new ArrayList<String>();
    MMSeg mmSeg = new MMSeg(new StringReader(phrase), maxWordSeg);
    Word word = null;
    while ((word = mmSeg.next()) != null) {
      String w = word.getString();
      result.add(w);
    }
    return result;
  }

  public static synchronized List<String> getTockenBySimpleSeg(String phrase) throws Exception {
    List<String> result = new ArrayList<String>();
    MMSeg mmSeg = new MMSeg(new StringReader(phrase), simpleSeg);
    Word word = null;
    while ((word = mmSeg.next()) != null) {
      String w = word.getString();
      result.add(w);
    }
    return result;
  }
}
