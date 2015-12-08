package com.bcgogo.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-3-1
 * Time: 上午9:39
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class GoodsSaleGuideController {
  private static final Logger LOG = LoggerFactory.getLogger(GoodsSaleGuideController.class);

  public Object startGuide(HttpServletRequest request){
    Long currentFlow = 1L;

    return null;
  }
}
